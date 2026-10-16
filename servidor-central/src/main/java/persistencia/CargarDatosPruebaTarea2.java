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
import logica.EdicionCurso;
import logica.ProgramaFormacion;


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
    List<String> imagenesEdicionesNuevas = new ArrayList<>();
    List<String> imagenesProgramasNuevas = new ArrayList<>();


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
            cargarEstadosInscripcionesEdiciones(em);

            // Imágenes oficiales de usuarios

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

            // Imágenes oficiales de cursos

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

            cargarImagenCurso(
                    em,"Participacion en investigacion\nsobre el empleo del juego\nKomikan como recurso\ndidactico en la Escuela",
                    "komikan.png", imagenesCursosNuevas
            );

            // Imágenes oficiales de ediciones de cursos

            // E1 - Flor del Ceibo - 2010
            cargarImagenEdicion(
                    em, "Flor del Ceibo - 2010",
                    "edicion-e1.jpg", imagenesEdicionesNuevas
            );

            // E2 - Flor del Ceibo - 2012
            cargarImagenEdicion(
                    em, "Flor del Ceibo - 2012",
                    "edicion-e2.jpg", imagenesEdicionesNuevas
            );

            // E3 - Flor del Ceibo - 2025
            cargarImagenEdicion(
                    em, "Flor del Ceibo - 2025",
                    "edicion-e3.jpg", imagenesEdicionesNuevas
            );

            // E4 - Dalavuelta - 2025
            cargarImagenEdicion(
                    em, "Dalavuelta - 2025",
                    "edicion-e4.jpg", imagenesEdicionesNuevas
            );

            // E7 - Taller de robótica educativa - 2024
            cargarImagenEdicion(
                    em, "Taller de robótica educativa - 2024",
                    "edicion-e7.jpg", imagenesEdicionesNuevas
            );

            // E8 - Taller de robótica educativa - 2026
            cargarImagenEdicion(
                    em, "Taller de robótica educativa - 2026",
                    "edicion-e8.jpg", imagenesEdicionesNuevas
            );

            // E9 - Taller de robótica educativa - 2026-2
            cargarImagenEdicion(
                    em, "Taller de robótica educativa - 2026-2",
                    "edicion-e9.jpg", imagenesEdicionesNuevas
            );

            // Imágenes oficiales de programas de formación

            // P1 - EFI Ingeniería Mecánica
            cargarImagenPrograma(
                    em, "EFI Ingeniería Mecánica",
                    "programa-p1.jpg", imagenesProgramasNuevas
            );

            // P2 - Formación integral
            cargarImagenPrograma(
                    em, "Formación integral",
                    "programa-p2.jpg", imagenesProgramasNuevas
            );

            // P3 - EFI Robótica
            cargarImagenPrograma(
                    em, "EFI Robótica",
                    "programa-p3.jpg", imagenesProgramasNuevas
            );


            em.getTransaction().commit();

            System.out.println(
                    "Categorías, inscripciones a programas e imágenes de Tarea 2 cargadas correctamente."
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


           // Limpiar imágenes nuevas de ediciones
            for (String nombre : imagenesEdicionesNuevas) {
                try {
                    ImagenesEntidades.ediciones().eliminar(nombre);
                } catch (Exception limpiezaError) {
                    e.addSuppressed(limpiezaError);
                }
            }

            // Limpiar imágenes nuevas de programas
            for (String nombre : imagenesProgramasNuevas) {
                try {
                    ImagenesEntidades.programas().eliminar(nombre);
                } catch (Exception limpiezaError) {
                    e.addSuppressed(limpiezaError);
                }
            }


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


private static void cargarImagenEdicion(
        EntityManager em,
        String nombreEdicion,
        String archivo,
        List<String> imagenesEdicionesNuevas
) throws Exception {

    List<EdicionCurso> ediciones = em.createQuery(
            "SELECT e FROM EdicionCurso e WHERE e.nombre = :nombre",
            EdicionCurso.class
    )
    .setParameter("nombre", nombreEdicion)
    .getResultList();

    if (ediciones.isEmpty()) {
        throw new IllegalStateException(
                "No existe la edición: " + nombreEdicion
        );
    }

    EdicionCurso edicion = ediciones.get(0);

    if (edicion.getImagen() != null
            && !edicion.getImagen().isBlank()) {

        System.out.println(
                nombreEdicion + " ya tiene una imagen asignada."
        );
        return;
    }

    Path ruta = Path.of(
            System.getProperty("user.home"),
            "Descargas",
            "edext-imagenes-tarea2",
            "ediciones",
            archivo
    );

    byte[] datos = Files.readAllBytes(ruta);

    String nombre = ImagenesEntidades.ediciones().guardar(datos);

    imagenesEdicionesNuevas.add(nombre);

    edicion.setImagen(nombre);

    System.out.println(
            "Imagen asignada a la edición "
                    + nombreEdicion + ": " + nombre
    );
}


private static void cargarImagenPrograma(
        EntityManager em,
        String nombrePrograma,
        String archivo,
        List<String> imagenesProgramasNuevas
) throws Exception {

    List<ProgramaFormacion> programas = em.createQuery(
            "SELECT p FROM ProgramaFormacion p WHERE p.nombre = :nombre",
            ProgramaFormacion.class
    )
    .setParameter("nombre", nombrePrograma)
    .getResultList();

    if (programas.isEmpty()) {
        throw new IllegalStateException(
                "No existe el programa: " + nombrePrograma
        );
    }

    ProgramaFormacion programa = programas.get(0);

    if (programa.getImagen() != null
            && !programa.getImagen().isBlank()) {

        System.out.println(
                nombrePrograma + " ya tiene una imagen asignada."
        );
        return;
    }

    Path ruta = Path.of(
            System.getProperty("user.home"),
            "Descargas",
            "edext-imagenes-tarea2",
            "programas",
            archivo
    );

    byte[] datos = Files.readAllBytes(ruta);

    String nombre = ImagenesEntidades.programas().guardar(datos);

    imagenesProgramasNuevas.add(nombre);

    programa.setImagen(nombre);

    System.out.println(
            "Imagen asignada al programa "
                    + nombrePrograma + ": " + nombre
    );
}

private static void cargarEstadosInscripcionesEdiciones(EntityManager em) {

    // Formato: nickname, edición, estado
    String[][] datos = {
        {"eleven11", "Flor del Ceibo - 2010", "ACEPTADA"},
        {"chechi", "Flor del Ceibo - 2010", "ACEPTADA"},

        {"costas", "Flor del Ceibo - 2012", "ACEPTADA"},
        {"roro", "Flor del Ceibo - 2012", "RECHAZADA"},
        {"weiss", "Flor del Ceibo - 2012", "ACEPTADA"},

        {"roro", "Flor del Ceibo - 2025", "ACEPTADA"},
        {"jeffw", "Flor del Ceibo - 2025", "ACEPTADA"},

        {"chechi", "Dalavuelta - 2025", "ACEPTADA"},
        {"eleven11", "Dalavuelta - 2025", "RECHAZADA"},
        {"roro", "Dalavuelta - 2025", "ACEPTADA"},
        {"costas", "Dalavuelta - 2025", "ACEPTADA"},
        {"jeffw", "Dalavuelta - 2025", "RECHAZADA"},

        {"costas", "Extensionismo Industrial - 2025", "ACEPTADA"},
        {"chechi", "Extensionismo Industrial - 2025", "ACEPTADA"},
        {"eleven11", "Extensionismo Industrial - 2025", "ACEPTADA"},
        {"weiss", "Extensionismo Industrial - 2025", "ACEPTADA"},

        {"roro", "Inclusión Energética - 2026", "ACEPTADA"},
        {"weiss", "Inclusión Energética - 2026", "RECHAZADA"},
        {"chechi", "Inclusión Energética - 2026", "ACEPTADA"},
        {"eleven11", "Inclusión Energética - 2026", "RECHAZADA"},

        {"weiss", "Taller de robótica educativa - 2024", "RECHAZADA"},
        {"roro", "Taller de robótica educativa - 2024", "RECHAZADA"},
        {"eleven11", "Taller de robótica educativa - 2024", "ACEPTADA"},

        {"jeffw", "Taller de robótica educativa - 2026", "ACEPTADA"},
        {"costas", "Taller de robótica educativa - 2026", "ACEPTADA"},
       // {"roro", "Taller de robótica educativa - 2026", "RECHAZADA"},

        {"chechi", "Participación en investigación sobre\nel empleo del juego Komikan como\nrecurso didáctico en la Escuela - 2026", "ACEPTADA"},
        {"weiss", "Participación en investigación sobre\nel empleo del juego Komikan como\nrecurso didáctico en la Escuela - 2026", "ACEPTADA"},
        {"roro", "Participación en investigación sobre\nel empleo del juego Komikan como\nrecurso didáctico en la Escuela - 2026", "ACEPTADA"},

        {"costas", "Talleres plenarios - 2026", "RECHAZADA"},
        {"weiss", "Talleres plenarios - 2026", "ACEPTADA"},
        {"roro", "Talleres plenarios - 2026", "ACEPTADA"},

        {"weiss", "Seminarios de Resolución de\nProblemas - 2026", "ACEPTADA"},
        {"costas", "Seminarios de Resolución de\nProblemas - 2026", "RECHAZADA"},
        {"roro", "Seminarios de Resolución de\nProblemas - 2026", "ACEPTADA"},
        {"chechi", "Seminarios de Resolución de\nProblemas - 2026", "RECHAZADA"}
    };

    for (String[] dato : datos) {

        List<Inscripcion> inscripciones = em.createQuery(
                "SELECT i FROM Estudiante e JOIN e.inscripciones i "
                + "WHERE e.nick = :nick AND i.edicion.nombre = :edicion",
                Inscripcion.class
        )
        .setParameter("nick", dato[0])
        .setParameter("edicion", dato[1])
        .getResultList();

        if (inscripciones.size() != 1) {
            throw new IllegalStateException(
                    "Se esperaba una inscripción para "
                    + dato[0] + " en " + dato[1]
                    + ", pero se encontraron " + inscripciones.size()
            );
        }

        Inscripcion inscripcion = inscripciones.get(0);

        logica.EstadoInscripcion esperado =
                logica.EstadoInscripcion.valueOf(dato[2]);

        if (inscripcion.getEstado() == esperado) {
            continue;
        }

        if (inscripcion.getEstado()
                != logica.EstadoInscripcion.INSCRIPTO) {
            throw new IllegalStateException(
                    "Estado diferente al esperado para "
                    + dato[0] + " en " + dato[1]
            );
        }

        inscripcion.cambiarEstado(esperado);
    }

    System.out.println(
            "Estados oficiales de inscripciones cargados correctamente."
    );
}


}

