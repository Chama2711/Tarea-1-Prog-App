package soap;

import java.lang.reflect.Proxy;
import java.util.List;
import logica.IServidorCentral;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServicioCatalogoTest {
    @Test public void listadoGeneralDelegaEnLaLogicaExistente() {
        IServidorCentral central = central((metodo, args) -> {
            assertEquals("listarNombresInstitutos", metodo);
            return List.of("INCO", "Eléctrica");
        });
        assertArrayEquals(new String[]{"INCO", "Eléctrica"},
                new ServicioCatalogo(central).listarInstitutos());
    }

    @Test public void listadoDocenteEnviaElNicknameYConservaSoloSusInstitutos() {
        IServidorCentral central = central((metodo, args) -> {
            assertEquals("listarNombresInstitutosDocente", metodo);
            assertArrayEquals(new Object[]{"docente_ñ"}, args);
            return List.of("Eléctrica");
        });
        assertArrayEquals(new String[]{"Eléctrica"},
                new ServicioCatalogo(central).listarInstitutosDocente("docente_ñ"));
    }

    @Test public void rechazoDelCentralNoSeDisfrazaComoListaVacia() {
        var servicio = new ServicioCatalogo(central((metodo, args) -> {
            throw new SecurityException("El usuario no es docente.");
        }));
        assertThrows(SecurityException.class, () -> servicio.listarInstitutosDocente("alumno"));
    }

    private interface Consulta { Object ejecutar(String metodo, Object[] args); }

    @Test public void categoriasGeneralesReutilizanElCatalogoCentral() {
        var servicio = new ServicioCatalogo(central((metodo, args) -> {
            assertEquals("listarNombresCategorias", metodo);
            return List.of("Educación", "Robótica");
        }));
        assertArrayEquals(new String[]{"Educación", "Robótica"}, servicio.listarCategorias());
    }

    @Test public void categoriasDeCursoYProgramaSeDevuelvenComoNombres() {
        var servicio = new ServicioCatalogo(central((metodo, args) -> {
            if (metodo.equals("listarCategoriasCurso")) {
                assertArrayEquals(new Object[]{"Curso ñ"}, args);
                return List.of(new logica.Categoria("Tecnología"));
            }
            assertEquals("listarNombresCategoriasPrograma", metodo);
            assertArrayEquals(new Object[]{"Programa ñ"}, args);
            return List.of("Ciencia");
        }));
        assertArrayEquals(new String[]{"Tecnología"}, servicio.listarCategoriasCurso("Curso ñ"));
        assertArrayEquals(new String[]{"Ciencia"}, servicio.listarCategoriasPrograma("Programa ñ"));
    }

    @Test public void errorDeCategoriasDelCursoConservaLaCausa() {
        var causa = new IllegalArgumentException("Curso inexistente");
        var servicio = new ServicioCatalogo(central((metodo, args) -> { throw causa; }));
        assertSame(causa, assertThrows(IllegalStateException.class,
                () -> servicio.listarCategoriasCurso("inexistente")).getCause());
    }

    private static IServidorCentral central(Consulta consulta) {
        return (IServidorCentral) Proxy.newProxyInstance(IServidorCentral.class.getClassLoader(),
                new Class<?>[]{IServidorCentral.class},
                (p, m, a) -> consulta.ejecutar(m.getName(), a));
    }
}
