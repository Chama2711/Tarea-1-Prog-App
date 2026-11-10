package logica;

import java.time.LocalDate;
import java.util.List;

/** Copia de los resultados definitivos; no conserva entidades JPA. */
public final class DTCertificadoPrograma {
    private final String nick, estudiante, programa;
    private final LocalDate emision;
    private final List<Resultado> resultados;
    public DTCertificadoPrograma(String nick, String estudiante, String programa,
            LocalDate emision, List<Resultado> resultados) {
        this.nick = nick; this.estudiante = estudiante; this.programa = programa;
        this.emision = emision; this.resultados = List.copyOf(resultados);
    }
    public String nick() { return nick; }
    public String estudiante() { return estudiante; }
    public String programa() { return programa; }
    public LocalDate emision() { return emision; }
    public List<Resultado> resultados() { return resultados; }
    public static final class Resultado {
        private final String curso, edicion;
        private final int nota;
        private final LocalDate fecha;
        public Resultado(String curso, String edicion, int nota, LocalDate fecha) {
            this.curso = curso; this.edicion = edicion; this.nota = nota; this.fecha = fecha;
        }
        public String curso() { return curso; }
        public String edicion() { return edicion; }
        public int nota() { return nota; }
        public LocalDate fecha() { return fecha; }
    }
}
