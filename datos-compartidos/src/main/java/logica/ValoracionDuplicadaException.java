package logica;

/** Rechazo de negocio: cada estudiante puede valorar un curso una sola vez. */
public class ValoracionDuplicadaException extends IllegalStateException {
    public ValoracionDuplicadaException(String mensaje) { super(mensaje); }
    public ValoracionDuplicadaException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
