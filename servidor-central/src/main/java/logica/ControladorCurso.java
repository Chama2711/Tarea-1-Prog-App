package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import persistencia.ControladorPersistencia;

public class ControladorCurso {

    private final ControladorPersistencia persistencia;

    public ControladorCurso(ControladorPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    public List<String> listarNombresInstitutos() {
        List<String> nombres = new ArrayList<>();
        for (Instituto instituto : persistencia.listarInstitutos()) {
            nombres.add(instituto.getNombre());
        }
        return nombres;
    }

    public List<String> listarNombresCursosPorInstituto(String nombreInstituto) {
        List<String> nombres = new ArrayList<>();
        if (nombreInstituto == null || nombreInstituto.isBlank()) {
            return nombres;
        }
        for (Curso curso : persistencia.listarCursosPorInstituto(nombreInstituto)) {
            nombres.add(curso.getNombre());
        }
        return nombres;
    }

    public List<String> listarNombresCategorias() {
        List<String> nombres = new ArrayList<>();
        for (Categoria categoria : persistencia.listarCategorias()) {
            nombres.add(categoria.getNombre());
        }
        return nombres;
    }

    public List<String> listarNicknamesDocentes() {
        List<String> nicknames = new ArrayList<>();
        for (Docente docente : persistencia.listarDocentes()) {
            nicknames.add(docente.getNick());
        }
        return nicknames;
    }

    public void altaEdicionCurso(
            String nombreInstituto,
            String nombreCurso,
            String nombreEdicion,
            LocalDate inicio,
            LocalDate fin,
            int cupo,
            List<String> nicknamesDocentes) {
        if (nombreInstituto == null || nombreInstituto.isBlank()
                || nombreCurso == null || nombreCurso.isBlank()) {
            throw new IllegalArgumentException("Seleccione instituto y curso.");
        }
        if (nombreEdicion == null || nombreEdicion.isBlank()) {
            throw new IllegalArgumentException("Indique el nombre de la edición.");
        }
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Revise las fechas de inicio y fin.");
        }
        if (cupo < -1) {
            throw new IllegalArgumentException(
                    "Cupo inválido: use -1 para sin límite o un entero no negativo.");
        }

        Instituto instituto = persistencia.buscarInstituto(nombreInstituto);
        if (instituto == null) {
            throw new IllegalArgumentException("El instituto ya no existe.");
        }
        Curso curso = persistencia.buscarCursoInstituto(nombreCurso, instituto);
        if (curso == null) {
            throw new IllegalArgumentException("El curso ya no existe en ese instituto.");
        }

        Set<Docente> docentes = new HashSet<>();
        if (nicknamesDocentes != null) {
            for (String nick : nicknamesDocentes) {
                Docente docente = persistencia.buscarDocentePorNick(nick);
                if (docente == null) {
                    throw new IllegalArgumentException(
                            "El docente " + nick + " ya no existe.");
                }
                docentes.add(docente);
            }
        }

        EdicionCurso edicion = new EdicionCurso(
                nombreEdicion.trim(), inicio, fin, cupo, LocalDate.now());
        edicion.setDocentes(docentes);
        persistencia.altaEdicionCurso(curso, edicion);
    }

    public void altaCurso(
            String nombreInstituto,
            String nombre,
            String descripcion,
            String duracion,
            int horas,
            int creditos,
            String url,
            List<String> nombresPrevias,
            List<String> nombresCategorias) {
        altaCurso(nombreInstituto, nombre, descripcion, duracion, horas, creditos,
                url, LocalDate.now(), nombresPrevias, nombresCategorias);
    }

    public void altaCurso(String nombreInstituto, String nombre, String descripcion,
            String duracion, int horas, int creditos, String url, LocalDate fechaAlta,
            List<String> nombresPrevias, List<String> nombresCategorias) {

        if (fechaAlta == null) {
            throw new IllegalArgumentException("Seleccione la fecha de alta.");
        }

        if (nombreInstituto == null || nombreInstituto.isBlank()) {
            throw new IllegalArgumentException("Seleccione un instituto.");
        }
        if (nombre == null || nombre.isBlank()
                || descripcion == null || descripcion.isBlank()
                || duracion == null || duracion.isBlank()
                || url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "Complete nombre, descripción, duración y URL.");
        }
        if (horas < 0 || creditos < 0) {
            throw new IllegalArgumentException("Horas y créditos no pueden ser negativos.");
        }
        if (nombresCategorias == null || nombresCategorias.isEmpty()) {
            throw new IllegalArgumentException("Seleccione al menos una categoría.");
        }

        Instituto instituto = persistencia.buscarInstituto(nombreInstituto);
        if (instituto == null) {
            throw new IllegalArgumentException("El instituto ya no existe.");
        }

        Set<Curso> previas = new HashSet<>();
        if (nombresPrevias != null) {
            for (String nombrePrevia : nombresPrevias) {
                Curso previa = persistencia.buscarCurso(nombrePrevia);
                if (previa == null) {
                    throw new IllegalArgumentException(
                            "Una previa seleccionada ya no existe.");
                }
                previas.add(previa);
            }
        }

        Set<Categoria> categorias = new HashSet<>();
        for (String nombreCategoria : nombresCategorias) {
            Categoria categoria = persistencia.buscarCategoria(nombreCategoria);
            if (categoria == null) {
                throw new IllegalArgumentException(
                        "La categoría " + nombreCategoria + " ya no existe.");
            }
            categorias.add(categoria);
        }

        Curso curso = new Curso(nombre.trim(), duracion.trim(), horas, creditos,
                fechaAlta, descripcion.trim(), url.trim());
        curso.setInstituto(instituto);
        curso.setPrevias(previas);
        curso.setCategorias(categorias);
        persistencia.altaCurso(curso);
    }
}
