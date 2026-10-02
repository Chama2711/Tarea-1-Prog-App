package logica;

public enum EstadoInscripcion {
    INSCRIPTO("Inscripto"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoInscripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
