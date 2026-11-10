package web;

import java.util.Locale;

/** Valores numéricos controlados para el componente de cinco estrellas. */
public final class Puntajes {
    private Puntajes() { }
    public static String porcentaje(double promedio) {
        return String.format(Locale.ROOT, "%.2f", Math.max(0, Math.min(5, promedio)) * 20);
    }
    public static String promedio(double promedio) {
        return String.format(Locale.forLanguageTag("es-UY"), "%.2f", promedio);
    }
}
