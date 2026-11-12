package logica; 

public class ValoracionDuplicadaException extends Exception {
    
    public ValoracionDuplicadaException(String mensaje) {
        super(mensaje);
    }
    
    public ValoracionDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}