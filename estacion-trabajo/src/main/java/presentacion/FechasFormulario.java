/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JTextField;

public class FechasFormulario {

    public static void configurar(JDateChooser selector) {
        selector.setLocale(Locale.forLanguageTag("es-UY"));
        selector.setDateFormatString("dd/MM/yyyy");

        // La fecha se elige desde el calendario.
        if (selector.getDateEditor().getUiComponent()
                instanceof JTextField campo) {
            campo.setEditable(false);
        }
    }

    public static LocalDate leer(
            JDateChooser selector, String nombreCampo) {

        Calendar calendario = selector.getCalendar();

        if (calendario == null) {
            throw new IllegalArgumentException(
                    "Seleccione " + nombreCampo + "."
            );
        }

        return LocalDate.of(
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH) + 1,
                calendario.get(Calendar.DAY_OF_MONTH)
        );
    }

    public static void mostrar(
            JDateChooser selector, LocalDate fecha) {

        if (fecha == null) {
            selector.setDate(null);
            return;
        }

        Calendar calendario = new GregorianCalendar(
                fecha.getYear(),
                fecha.getMonthValue() - 1,
                fecha.getDayOfMonth()
        );

        selector.setCalendar(calendario);
    }
}
