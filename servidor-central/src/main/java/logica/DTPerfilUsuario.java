package logica;

import java.time.LocalDate;
import java.util.List;

/** Datos inmutables de consulta: no contienen credenciales ni entidades JPA. */
public final class DTPerfilUsuario {
    private final DTUsuarioConsulta usuario;
    private final boolean propio;
    private final List<DTInscripcion> inscripciones;
    private final List<DTEdicionConsulta> ediciones;
    private final List<String> programas;
    private final List<DTInscripcion> aceptados;

    public DTPerfilUsuario(DTUsuarioConsulta usuario, boolean propio, List<DTInscripcion> inscripciones, List<DTEdicionConsulta> ediciones, List<String> programas, List<DTInscripcion> aceptados) {
        this.usuario = usuario;
        this.propio = propio;
        this.inscripciones = List.copyOf(inscripciones);
        this.ediciones = List.copyOf(ediciones);
        this.programas = List.copyOf(programas);
        this.aceptados = List.copyOf(aceptados);
    }

    public DTUsuarioConsulta usuario() { return usuario; }
    public boolean propio() { return propio; }
    public List<DTInscripcion> inscripciones() { return inscripciones; }
    public List<DTEdicionConsulta> ediciones() { return ediciones; }
    public List<String> programas() { return programas; }
    public List<DTInscripcion> aceptados() { return aceptados; }

    public static DTPerfilUsuario crear(DTUsuarioConsulta usuario, String nickConsultante,
            List<DTInscripcion> inscripciones, List<DTEdicionConsulta> ediciones,
            List<String> programas, List<DTInscripcion> aceptados) {
        boolean propio = usuario.nick().equals(nickConsultante);
        boolean estudiante = usuario.rol() == DTAutenticacion.Rol.ESTUDIANTE;
        List<DTInscripcion> visibles = estudiante ? inscripciones.stream()
                .filter(i -> propio || i.getEstado() == EstadoInscripcion.INSCRIPTO
                        || i.getEstado() == EstadoInscripcion.ACEPTADA).toList() : List.of();
        return new DTPerfilUsuario(usuario, propio, visibles,
                estudiante ? List.of() : ediciones, estudiante ? programas : List.of(),
                !estudiante && propio ? aceptados : List.of());
    }
}
