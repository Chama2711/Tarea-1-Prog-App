/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

/**
 *
 * @author elizeth
 */
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.Categoria;
import logica.Curso;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import logica.Estudiante;
import logica.ProgramaFormacion;
import logica.Inscripcion;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import logica.Usuario;

public class CargarDatosPruebaTarea2 {

public static void main(String[] args) {

    String url = "jdbc:mysql://localhost:3306/edext_web"
            + "?zeroDateTimeBehavior=CONVERT_TO_NULL";

    String usuario = "root";
    String contrasena = System.getenv("EDEXT_DB_PASSWORD");

    if (contrasena == null) {
        throw new IllegalStateException(
                "Falta configurar EDEXT_DB_PASSWORD."
        );
    }

    Map<String, Object> propiedades = Map.of(
            "javax.persistence.jdbc.url", url,
            "javax.persistence.jdbc.user", usuario,
            "javax.persistence.jdbc.password", contrasena,
            "javax.persistence.schema-generation.database.action", "none",
            "eclipselink.ddl-generation", "none"
    );

    EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("edEXTPU", propiedades);

    EntityManager em = emf.createEntityManager();
    
    List<String> imagenesNuevas = new ArrayList<>();
    List<String> imagenesCursosNuevas = new ArrayList<>();

    // A partir de aquí conservá el try/catch/finally existente.

        try {
            // Protección: este cargador es exclusivo de edext_web.
            String base = String.valueOf(
                    em.createNativeQuery("SELECT DATABASE()")
                      .getSingleResult()
            );

            if (!"edext_web".equals(base)) {
                throw new IllegalStateException(
                        "La carga solo está permitida en edext_web."
                );
            }

            em.getTransaction().begin();

            cargarCategorias(em);
            cargarInscripcionesProgramas(em);

            cargarImagenUsuario(
                    em, "heisenberg", "heisenberg.png", imagenesNuevas
            );

            cargarImagenUsuario(
                    em, "roro", "roro.jpg", imagenesNuevas
            );

            
            cargarImagenUsuario(
                    em, "chechi", "chechi.jpg", imagenesNuevas
            );
            
            cargarImagenUsuario(
                    em, "jeffw", "jeffw.jpg", imagenesNuevas
            );

            cargarImagenUsuario(
                    em, "benkenobi", "benkenobi.jpg", imagenesNuevas
            );

            cargarImagenUsuario(
                    em, "waston", "waston.jpg", imagenesNuevas
            );
            
            cargarImagenUsuario(
                    em, "timmy", "timmy.jpg", imagenesNuevas
            );
            
            cargarImagenUsuario(
                    em, "house", "house.jpg", imagenesNuevas
            );
            
          cargarImagenCurso(
                    em, "Dalavuelta",
                    "dalavuelta.png", imagenesCursosNuevas
            );

            cargarImagenCurso(
                    em, "Inclusión Energética",
                    "inclusion-energetica.jpg", imagenesCursosNuevas
            );

            cargarImagenCurso(
                    em, "Flor del Ceibo",
                    "extensionismo-industrial.png", imagenesCursosNuevas
            );

            cargarImagenCurso(
                    em, "Taller de robótica educativa",
                    "microbit.jpg", imagenesCursosNuevas
            );

            cargarImagenCurso(
                    em, "MicroBit",
                    "curso-c10.jpg", imagenesCursosNuevas
            );

            
            em.getTransaction().commit();

            System.out.println(
                    "Categorías de Tarea 2 cargadas correctamente."
            );

            } catch (Exception e) {

           if (em.getTransaction().isActive()) {
               em.getTransaction().rollback();
           }

           e.printStackTrace();

           // Limpiar imágenes nuevas de usuarios
           for (String nombre : imagenesNuevas) {
               try {
                   new ImagenesUsuario().eliminar(nombre);
               } catch (Exception limpiezaError) {
                   e.addSuppressed(limpiezaError);
               }
           }

           // Limpiar imágenes nuevas de cursos
           for (String nombre : imagenesCursosNuevas) {
               try {
                   ImagenesEntidades.cursos().eliminar(nombre);
               } catch (Exception limpiezaError) {
                   e.addSuppressed(limpiezaError);
               }
           }

           // Esta instrucción debe quedar al final del catch
           throw new RuntimeException(
                   "Falló la carga de datos de Tarea 2.", e
           );

       } finally {
           em.close();
           emf.close();
       }
    }

    private static void cargarCategorias(EntityManager em) {

        Categoria social = obtenerOCrearCategoria(em, "Social");
        Categoria industrial = obtenerOCrearCategoria(em, "Industrial");
        Categoria educativo = obtenerOCrearCategoria(em, "Educativo");
        Categoria interdisciplinario =
                obtenerOCrearCategoria(em, "Interdisciplinario");

        // T1 - Social
        asociar(em, "Dalavuelta", social);
        asociar(em, "Taller de robótica educativa", social);
        asociar(em,
                "“Herramientas de apoyo a la\n" +
                "enseñanza de inglés.\n" +
                "Instalación y evaluación”",
                social);

        // T2 - Industrial
        asociar(em, "Extensionismo Industrial", industrial);
        asociar(em, "Inclusión Energética", industrial);
        asociar(em, "MicroBit", industrial);

        // T3 - Educativo
        asociar(em, "Flor del Ceibo", educativo);
        asociar(em, "Taller de robótica educativa", educativo);
        asociar(em,
                "Participación en investigación\n" +
                "sobre el empleo del juego\n" +
                "Komikan como recurso\n" +
                "didáctico en la Escuela",
                educativo);
        asociar(em,
                "“Herramientas de apoyo a la\n" +
                "enseñanza de inglés.\n" +
                "Instalación y evaluación”",
                educativo);
        asociar(em, "MicroBit", educativo);

        // T4 - Interdisciplinario
        asociar(em, "Flor del Ceibo", interdisciplinario);
    }

    private static Categoria obtenerOCrearCategoria(
            EntityManager em, String nombre) {

        var resultados = em.createQuery(
                "SELECT c FROM Categoria c WHERE c.nombre = :nombre",
                Categoria.class
        ).setParameter("nombre", nombre)
         .getResultList();

        if (!resultados.isEmpty()) {
            return resultados.get(0);
        }

        Categoria categoria = new Categoria(nombre);
        em.persist(categoria);

        System.out.println("Categoría creada: " + nombre);

        return categoria;
    }

    private static void asociar(
            EntityManager em, String nombreCurso, Categoria categoria) {

        var resultados = em.createQuery(
                "SELECT c FROM Curso c WHERE c.nombre = :nombre",
                Curso.class
        ).setParameter("nombre", nombreCurso)
         .getResultList();

        if (resultados.isEmpty()) {
            throw new IllegalStateException(
                    "No se encontró el curso: " + nombreCurso
            );
        }

        Curso curso = resultados.get(0);

        boolean existe = curso.getCategorias()
                .stream()
                .anyMatch(c -> c.getId().equals(categoria.getId()));

        if (existe) {
            System.out.println(
                    "Asociación existente: " + curso.getNombre()
                    + " -> " + categoria.getNombre()
            );
            return;
        }

        curso.agregarCategoria(categoria);

        System.out.println(
                "Asociación agregada: " + curso.getNombre()
                + " -> " + categoria.getNombre()
        );
    }
    
    
private static void cargarInscripcionesProgramas(EntityManager em) {

    // P1 - EFI Ingeniería Mecánica
    inscribir(em, "eleven11", "EFI Ingeniería Mecánica", "2026-04-15");
    inscribir(em, "chechi", "EFI Ingeniería Mecánica", "2026-04-20");

    // P2 - Formación integral
    inscribir(em, "costas", "Formación integral", "2026-07-10");
    inscribir(em, "chechi", "Formación integral", "2026-07-10");
    inscribir(em, "eleven11", "Formación integral", "2026-07-12");
    inscribir(em, "weiss", "Formación integral", "2026-07-15");
    inscribir(em, "roro", "Formación integral", "2026-07-18");

    // P3 - EFI Robótica
    inscribir(em, "costas", "EFI Robótica", "2026-08-11");
    inscribir(em, "chechi", "EFI Robótica", "2026-08-15");
    inscribir(em, "roro", "EFI Robótica", "2026-08-22");
    inscribir(em, "jeffw", "EFI Robótica", "2026-08-30");
}

private static void inscribir(
        EntityManager em,
        String nick,
        String nombrePrograma,
        String fecha) {

    var estudiantes = em.createQuery(
            "SELECT e FROM Estudiante e WHERE e.nick = :nick",
            Estudiante.class
    ).setParameter("nick", nick)
     .getResultList();

    if (estudiantes.isEmpty()) {
        throw new IllegalStateException(
                "No se encontró el estudiante: " + nick
        );
    }

    Estudiante estudiante = estudiantes.get(0);

    var programas = em.createQuery(
            "SELECT p FROM ProgramaFormacion p WHERE p.nombre = :nombre",
            ProgramaFormacion.class
    ).setParameter("nombre", nombrePrograma)
     .getResultList();

    if (programas.isEmpty()) {
        throw new IllegalStateException(
                "No se encontró el programa: " + nombrePrograma
        );
    }

    ProgramaFormacion programa = programas.get(0);

    Long existentes = em.createQuery(
            "SELECT COUNT(i) FROM Estudiante e " +
            "JOIN e.inscripciones i " +
            "WHERE e.nick = :nick AND i.programa.id = :programaId",
            Long.class
    ).setParameter("nick", nick)
     .setParameter("programaId", programa.getId())
     .getSingleResult();

    if (existentes > 0) {
        System.out.println(
                "Inscripción existente: " + nick + " -> " + nombrePrograma
        );
        return;
    }

    Inscripcion nueva = new Inscripcion(
            LocalDate.parse(fecha),
            programa
    );

    em.persist(nueva);
    estudiante.agregoInscripcion(nueva);

    System.out.println(
            "Inscripción agregada: " + nick +
            " -> " + nombrePrograma +
            " (" + fecha + ")"
    );
}

    

private static void cargarImagenUsuario(
        EntityManager em,
        String nick,
        String archivo,
        List<String> imagenesNuevas
) throws Exception {

    Usuario usuario = em.find(Usuario.class, nick);

    if (usuario == null) {
        throw new IllegalStateException(
                "No existe el usuario: " + nick
        );
    }

    if (usuario.getImagen() != null
            && !usuario.getImagen().isBlank()) {

        System.out.println(
                nick + " ya tiene una imagen asignada."
        );
        return;
    }

    Path ruta = Path.of(
            System.getProperty("user.home"),
            "Descargas",
            "edext-imagenes-tarea2",
            archivo
    );

    byte[] datos = Files.readAllBytes(ruta);

    ImagenesUsuario almacenamiento = new ImagenesUsuario();

    String nombre = almacenamiento.guardar(datos);

    imagenesNuevas.add(nombre);

    usuario.setImagen(nombre);

    System.out.println(
            "Imagen asignada a " + nick + ": " + nombre
    );
}

private static void cargarImagenCurso(
        EntityManager em,
        String nombreCurso,
        String archivo,
        List<String> imagenesCursosNuevas
) throws Exception {

    List<Curso> cursos = em.createQuery(
            "SELECT c FROM Curso c WHERE c.nombre = :nombre",
            Curso.class
    )
    .setParameter("nombre", nombreCurso)
    .getResultList();

    if (cursos.isEmpty()) {
        throw new IllegalStateException(
                "No existe el curso: " + nombreCurso
        );
    }

    Curso curso = cursos.get(0);

    if (curso.getImagen() != null
            && !curso.getImagen().isBlank()) {

        System.out.println(
                nombreCurso + " ya tiene una imagen asignada."
        );
        return;
    }

    Path ruta = Path.of(
            System.getProperty("user.home"),
            "Descargas",
            "edext-imagenes-tarea2",
            archivo
    );

    byte[] datos = Files.readAllBytes(ruta);

    String nombre = ImagenesEntidades.cursos().guardar(datos);

    imagenesCursosNuevas.add(nombre);

    curso.setImagen(nombre);

    System.out.println(
            "Imagen asignada al curso " + nombreCurso + ": " + nombre
    );
}

    
    
}

