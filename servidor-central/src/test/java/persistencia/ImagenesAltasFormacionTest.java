package persistencia;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.persistence.EntityManagerFactory;
import logica.Curso;
import logica.EdicionCurso;
import logica.ProgramaFormacion;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

/** Prueba archivos reales en carpeta temporal, sin usar MySQL ni las fotos personales. */
public class ImagenesAltasFormacionTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();
    private String homeAnterior;
    @Before public void aislarCarpetas() {
        homeAnterior = System.getProperty("user.home");
        System.setProperty("user.home", temporal.getRoot().getAbsolutePath());
    }
    @After public void restaurarCarpetas() { System.setProperty("user.home", homeAnterior); }

    private static class PersistenciaPrueba extends ControladorPersistencia {
        boolean fallar;
        int altas;
        PersistenciaPrueba() {
            super((EntityManagerFactory) Proxy.newProxyInstance(EntityManagerFactory.class.getClassLoader(),
                    new Class<?>[]{EntityManagerFactory.class}, (p, m, a) -> {
                        throw new AssertionError("No debe acceder a MySQL");
                    }));
        }
        @Override public void altaCurso(Curso curso) { guardar(); }
        @Override public void altaProgramaFormacion(ProgramaFormacion programa) { guardar(); }
        @Override public void altaEdicionCurso(Curso curso, EdicionCurso edicion) { guardar(); }
        private void guardar() {
            altas++;
            if (fallar) throw new IllegalArgumentException("Nombre duplicado");
        }
    }
    private byte[] imagen(String formato) throws Exception {
        var salida = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), formato, salida));
        return salida.toByteArray();
    }
    private void alta(PersistenciaPrueba p, boolean edicion, byte[] datos, Curso curso, EdicionCurso e) {
        if (edicion) p.altaEdicionCurso(curso, e, datos);
        else p.altaCurso(curso, datos);
    }
    private Path carpeta(boolean edicion) {
        return temporal.getRoot().toPath().resolve("edEXT/imagenes/" + (edicion ? "ediciones" : "cursos"));
    }
    @Test public void guardaJpgYPngConNombreRelativoEnAmbasAltas() throws Exception {
        for (boolean edicion : new boolean[]{false, true}) for (String formato : new String[]{"png", "jpg"}) {
            var p = new PersistenciaPrueba(); var curso = new Curso(); var e = new EdicionCurso();
            byte[] datos = imagen(formato); alta(p, edicion, datos, curso, e);
            String nombre = edicion ? e.getImagen() : curso.getImagen();
            assertNotNull(nombre); assertEquals(nombre, Path.of(nombre).getFileName().toString());
            assertArrayEquals(datos, Files.readAllBytes(carpeta(edicion).resolve(nombre)));
            assertEquals(1, p.altas);
        }
    }
    @Test public void falloDePersistenciaNoDejaArchivoNiAsociacion() throws Exception {
        for (boolean edicion : new boolean[]{false, true}) {
            var p = new PersistenciaPrueba(); p.fallar = true;
            var curso = new Curso(); var e = new EdicionCurso(); byte[] datos = imagen("png");
            assertThrows(IllegalArgumentException.class, () -> alta(p, edicion, datos, curso, e));
            assertNull(edicion ? e.getImagen() : curso.getImagen());
            try (var archivos = Files.list(carpeta(edicion))) { assertEquals(0, archivos.count()); }
        }
    }
    @Test public void contenidoQueNoEsImagenSeRechazaAntesDeGuardar() {
        for (boolean edicion : new boolean[]{false, true}) {
            var p = new PersistenciaPrueba();
            assertThrows(IllegalArgumentException.class, () -> alta(p, edicion, new byte[]{1,2,3}, new Curso(), new EdicionCurso()));
            assertEquals(0, p.altas); assertFalse(Files.exists(carpeta(edicion)));
        }
    }
    @Test public void limiteDeTamanoTambienSeValidaEnCentral() {
        for (boolean edicion : new boolean[]{false, true}) {
            var p = new PersistenciaPrueba();
            assertThrows(IllegalArgumentException.class, () -> alta(p, edicion, new byte[5*1024*1024+1], new Curso(), new EdicionCurso()));
            assertEquals(0, p.altas); assertFalse(Files.exists(carpeta(edicion)));
        }
    }
    @Test public void sinImagenConservaElAltaExistenteSinCrearCarpetas() {
        for (boolean edicion : new boolean[]{false, true}) {
            var p = new PersistenciaPrueba(); var curso = new Curso(); var e = new EdicionCurso();
            alta(p, edicion, null, curso, e);
            assertEquals(1, p.altas); assertNull(edicion ? e.getImagen() : curso.getImagen());
            assertFalse(Files.exists(carpeta(edicion)));
        }
    }

    @Test public void programaGuardaJpgYPngYRevierteElArchivoSiFallaElAlta() throws Exception {
        Path carpeta = temporal.getRoot().toPath().resolve("edEXT/imagenes/programas");
        for (String formato : new String[]{"png", "jpg"}) {
            var p = new PersistenciaPrueba(); var programa = new ProgramaFormacion();
            byte[] datos = imagen(formato);
            p.altaProgramaFormacion(programa, datos);
            assertArrayEquals(datos, Files.readAllBytes(carpeta.resolve(programa.getImagen())));
            assertArrayEquals(datos, ImagenesEntidades.programas().leer(programa.getImagen()));
        }
        var p = new PersistenciaPrueba(); p.fallar = true;
        var programa = new ProgramaFormacion(); byte[] datos = imagen("png");
        assertThrows(IllegalArgumentException.class, () -> p.altaProgramaFormacion(programa, datos));
        assertNull(programa.getImagen());
        try (var archivos = Files.list(carpeta)) { assertEquals(2, archivos.count()); }
    }

    @Test public void programaRechazaImagenInvalidaOExcedidaYPermiteOmitirla() {
        var p = new PersistenciaPrueba();
        for (byte[] datos : new byte[][]{new byte[]{1, 2, 3}, new byte[5*1024*1024+1]}) {
            assertThrows(IllegalArgumentException.class,
                    () -> p.altaProgramaFormacion(new ProgramaFormacion(), datos));
        }
        assertEquals(0, p.altas);
        assertFalse(Files.exists(temporal.getRoot().toPath().resolve("edEXT/imagenes/programas")));
        p.altaProgramaFormacion(new ProgramaFormacion(), null);
        assertEquals(1, p.altas);
    }
}
