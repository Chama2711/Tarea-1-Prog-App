package web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import web.soap.ClienteCatalogoSoap;
import web.soap.generado.DtCursoConsulta;

/** Compone los listados usando las operaciones de cursos ya publicadas. */
final class ConsultasCursos {
    private ConsultasCursos() { }

    static List<DtCursoConsulta> listar(ClienteCatalogoSoap cliente, String instituto) {
        List<String> nombres = instituto == null || instituto.isBlank()
                ? cliente.listarCursos() : cliente.listarCursosPorInstituto(instituto);
        List<DtCursoConsulta> cursos = new ArrayList<>();
        for (String nombre : nombres) {
            var curso = cliente.consultarCurso(nombre);
            // Puede desaparecer entre la consulta del listado y la del detalle.
            if (curso != null) cursos.add(curso);
        }
        return List.copyOf(cursos);
    }

    static LocalDate fecha(String fecha) {
        return fecha == null || fecha.isBlank() ? null : LocalDate.parse(fecha);
    }
}
