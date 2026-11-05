package logica;

/** Permite conservar el resultado 404 al registrar una inscripción por SOAP. */
public class RecursoNoEncontradoException extends IllegalArgumentException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}
