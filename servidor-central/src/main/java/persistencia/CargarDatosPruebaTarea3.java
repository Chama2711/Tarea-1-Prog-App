package persistencia;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import logica.Curso;
import logica.EdicionCurso;
import logica.EstadoInscripcion;
import logica.Estudiante;
import logica.Inscripcion;
import logica.ValoracionCurso;

/** Carga el anexo de Tarea 3 sobre los datos de CargarDatosPruebaWeb. */
public final class CargarDatosPruebaTarea3 {
    private static final Map<String, String> ESTUDIANTES_BASE = Map.of(
            "EL", "eleven11", "CH", "chechi", "CO", "costas",
            "RO", "roro", "WE", "weiss", "JW", "jeffw");

    private static final Map<String, String> EDICIONES = Map.ofEntries(
            Map.entry("E1", "Flor del Ceibo - 2010"),
            Map.entry("E2", "Flor del Ceibo - 2012"),
            Map.entry("E3", "Flor del Ceibo - 2025"),
            Map.entry("E4", "Dalavuelta - 2025"),
            Map.entry("E5", "Extensionismo Industrial - 2025"),
            Map.entry("E6", "Inclusión Energética - 2026"),
            Map.entry("E7", "Taller de robótica educativa - 2024"),
            Map.entry("E8", "Taller de robótica educativa - 2026"),
            Map.entry("E9", "Taller de robótica educativa - 2026-2"),
            Map.entry("E10", "Participación en investigación sobre\nel empleo del juego Komikan como\n"
                    + "recurso didáctico en la Escuela - 2026"),
            Map.entry("E11", "Herramientas de apoyo a la\nenseñanza de inglés. Instalación y\nevaluación - 26"),
            Map.entry("E12", "MicroBit - 2026"),
            Map.entry("E13", "Talleres plenarios - 2026"),
            Map.entry("E14", "Seminarios de Resolución de\nProblemas - 2026"));

    private static final Map<String, String> CURSOS = Map.of(
            "C1", "Talleres plenarios",
            "C2", "Seminarios de Resolución de Problemas",
            "C3", "Dalavuelta",
            "C4", "Extensionismo Industrial",
            "C5", "Inclusión Energética",
            "C6", "Flor del Ceibo",
            "C7", "Taller de robótica educativa",
            "C8", "Participación en investigación\nsobre el empleo del juego\nKomikan como recurso\n"
                    + "didáctico en la Escuela");

    // Edición, estudiante de la carga inicial, fecha de inscripción, estado y nota según el anexo.
    // El 0 del anexo se interpreta como una evaluación pendiente.
    private static final String INSCRIPCIONES = """
            E1 EL 2010-02-20 ACEPTADA 6
            E1 CH 2010-02-25 ACEPTADA 8
            E2 CO 2012-07-12 ACEPTADA 9
            E2 RO 2012-07-15 RECHAZADA -
            E2 WE 2012-07-30 ACEPTADA 0
            E3 RO 2025-03-10 ACEPTADA 9
            E3 JW 2025-03-15 ACEPTADA 6
            E4 CH 2024-07-25 ACEPTADA 7
            E4 EL 2024-07-28 RECHAZADA -
            E4 RO 2024-08-02 ACEPTADA 7
            E4 CO 2024-08-10 ACEPTADA 6
            E4 JW 2024-08-15 RECHAZADA -
            E5 CO 2025-07-18 ACEPTADA 9
            E5 CH 2025-07-20 ACEPTADA 9
            E5 EL 2025-07-29 ACEPTADA 1
            E5 WE 2025-08-05 ACEPTADA 11
            E6 RO 2026-02-23 ACEPTADA 7
            E6 WE 2026-02-25 RECHAZADA -
            E6 CH 2026-02-28 ACEPTADA 11
            E6 EL 2026-03-03 RECHAZADA -
            E7 WE 2024-02-18 RECHAZADA -
            E7 RO 2024-02-20 RECHAZADA -
            E7 EL 2024-03-03 ACEPTADA 0
            E8 JW 2026-02-18 ACEPTADA 8
            E8 CO 2026-02-22 ACEPTADA 10
            E8 RO 2026-02-23 RECHAZADA -
            E9 WE 2026-08-18 INSCRIPTO -
            E9 CH 2026-08-22 INSCRIPTO -
            E9 RO 2026-09-03 INSCRIPTO -
            E10 CH 2026-07-13 ACEPTADA 0
            E10 WE 2026-07-20 ACEPTADA 0
            E10 RO 2026-07-22 ACEPTADA 0
            E11 WE 2026-06-04 INSCRIPTO -
            E11 EL 2026-07-18 INSCRIPTO -
            E11 JW 2026-08-20 INSCRIPTO -
            E12 CH 2026-07-12 INSCRIPTO -
            E12 RO 2026-07-14 INSCRIPTO -
            E12 EL 2026-07-25 INSCRIPTO -
            E12 JW 2026-08-05 INSCRIPTO -
            E13 CO 2026-03-05 RECHAZADA -
            E13 WE 2026-03-04 ACEPTADA 6
            E13 RO 2026-03-07 ACEPTADA 7
            E14 WE 2026-07-15 ACEPTADA 10
            E14 CO 2026-07-20 RECHAZADA -
            E14 RO 2026-08-06 ACEPTADA 2
            E14 CH 2026-08-30 RECHAZADA -
            """;

    // Estudiante, curso y puntuación. El anexo no indica la fecha del voto.
    private static final String VALORACIONES = """
            EL C6 3
            CH C6 4
            CO C6 5
            WE C6 4
            RO C6 3
            JW C6 3
            CH C3 2
            RO C3 5
            CO C3 4
            CO C4 1
            CH C4 2
            EL C4 3
            WE C4 1
            RO C5 5
            CH C5 5
            EL C7 4
            JW C7 5
            CH C8 4
            WE C8 4
            RO C8 4
            WE C1 1
            RO C2 2
            """;

    private CargarDatosPruebaTarea3() { }

    public static void main(String[] args) {
        var emf = Persistence.createEntityManagerFactory("edEXTPU", ConfiguracionBaseDatos.leer());
        try {
            var em = emf.createEntityManager();
            try {
                var transaccion = em.getTransaction();
                transaccion.begin();
                try {
                    cargar(em);
                    transaccion.commit();
                } catch (RuntimeException error) {
                    if (transaccion.isActive()) transaccion.rollback();
                    throw error;
                }
            } finally {
                em.close();
            }
        } finally {
            emf.close();
        }
        System.out.println("Datos de prueba de la Tarea 3 cargados.");
    }

    static void cargar(EntityManager em) {
        Map<String, EdicionCurso> ediciones = new LinkedHashMap<>();
        for (String fila : INSCRIPCIONES.lines().toList()) {
            String[] dato = fila.split(" ");
            EdicionCurso edicion = ediciones.computeIfAbsent(dato[0], ref -> buscarEdicion(em, ref));
            Estudiante estudiante = buscarEstudiante(em, dato[1]);
            EstadoInscripcion estado = EstadoInscripcion.valueOf(dato[3]);
            Integer nota = "-".equals(dato[4]) || "0".equals(dato[4]) ? null : Integer.valueOf(dato[4]);
            cargarInscripcion(em, estudiante, edicion, LocalDate.parse(dato[2]), estado, nota);
        }
        em.flush();
        cerrarEdicionesEvaluadas(em, ediciones);
        cargarValoraciones(em);
    }

    private static EdicionCurso buscarEdicion(EntityManager em, String referencia) {
        EdicionCurso edicion = em.find(EdicionCurso.class, EDICIONES.get(referencia), LockModeType.PESSIMISTIC_WRITE);
        if (edicion == null) {
            throw new IllegalStateException("Falta la edición " + referencia + ". Ejecutá primero la carga inicial.");
        }
        return edicion;
    }

    private static Estudiante buscarEstudiante(EntityManager em, String referencia) {
        String nickBase = ESTUDIANTES_BASE.get(referencia);
        if (nickBase == null) {
            throw new IllegalArgumentException("Referencia de estudiante desconocida: " + referencia);
        }

        Estudiante estudiante = em.find(Estudiante.class, nickBase);
        if (estudiante == null) {
            throw new IllegalStateException("Falta el estudiante " + nickBase + " de la carga inicial.");
        }
        return estudiante;
    }

    private static void cargarInscripcion(EntityManager em, Estudiante estudiante, EdicionCurso edicion,
            LocalDate fechaInscripcion, EstadoInscripcion estado, Integer nota) {
        var coincidencias = estudiante.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.getEdicionCurso() != null
                        && edicion.getNombre().equals(inscripcion.getEdicionCurso().getNombre()))
                .toList();
        String referencia = estudiante.getNick() + " / " + edicion.getNombre();
        if (coincidencias.size() > 1) {
            throw new IllegalStateException("Hay inscripciones duplicadas para " + referencia);
        }
        Inscripcion inscripcion;
        if (coincidencias.isEmpty()) {
            inscripcion = new Inscripcion(fechaInscripcion, edicion);
            estudiante.agregoInscripcion(inscripcion);
            em.persist(inscripcion);
        } else {
            inscripcion = coincidencias.getFirst();
        }

        if (inscripcion.getEstado() != estado) {
            if (inscripcion.getEstado() != EstadoInscripcion.INSCRIPTO) {
                throw new IllegalStateException("El estado existente difiere del anexo: " + referencia);
            }
            inscripcion.cambiarEstado(estado);
        }
        if (inscripcion.getCalificacion() != null && !inscripcion.getCalificacion().equals(nota)) {
            throw new IllegalStateException("La calificación existente difiere del anexo: " + referencia);
        }
        inscripcion.setFechaInscripcion(fechaInscripcion);
        if (nota != null && inscripcion.getCalificacion() == null) {
            // El anexo da la fecha de inscripción, no la fecha de calificación.
            inscripcion.registrarCalificacion(nota, edicion.getFechaFin());
        }
    }

    private static void cerrarEdicionesEvaluadas(EntityManager em, Map<String, EdicionCurso> ediciones) {
        for (EdicionCurso edicion : ediciones.values()) {
            if (edicion.isCerrada()) continue;
            Object[] cantidades = em.createQuery("SELECT COUNT(i), COUNT(i.calificacion) FROM Inscripcion i "
                    + "WHERE i.edicion = :edicion AND i.estado = :estado", Object[].class)
                    .setParameter("edicion", edicion)
                    .setParameter("estado", EstadoInscripcion.ACEPTADA)
                    .getSingleResult();
            long aceptados = (Long) cantidades[0];
            long evaluados = (Long) cantidades[1];
            if (aceptados > 0 && aceptados == evaluados) {
                edicion.cerrar();
            }
        }
    }

    private static void cargarValoraciones(EntityManager em) {
        LocalDate fechaCarga = LocalDate.now();
        for (String fila : VALORACIONES.lines().toList()) {
            String[] dato = fila.split(" ");
            Estudiante estudiante = buscarEstudiante(em, dato[0]);
            Curso curso = em.createQuery("SELECT c FROM Curso c WHERE c.nombre = :nombre", Curso.class)
                    .setParameter("nombre", CURSOS.get(dato[1]))
                    .getResultStream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Falta el curso " + dato[1] + " de la carga inicial."));
            int puntuacion = Integer.parseInt(dato[2]);
            var existentes = em.createQuery("SELECT v.puntuacion FROM ValoracionCurso v "
                    + "WHERE v.estudiante = :estudiante AND v.curso = :curso", Integer.class)
                    .setParameter("estudiante", estudiante)
                    .setParameter("curso", curso)
                    .getResultList();
            if (existentes.isEmpty()) {
                em.persist(new ValoracionCurso(estudiante, curso, puntuacion, fechaCarga));
            } else if (existentes.size() != 1 || existentes.getFirst() != puntuacion) {
                throw new IllegalStateException("La valoración existente difiere del anexo: "
                        + estudiante.getNick() + " / " + curso.getNombre());
            }
        }
    }
}
