/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
        
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import persistencia.ControladorPersistencia;
import java.util.ArrayList;

/**
 *
 * @author Nicolás
 */
public class ControladorCurso implements IControladorCurso {

    private final ControladorPersistencia persistencia;

    public ControladorCurso(ControladorPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public void altaCurso(
            String nombreInstituto,
            String nombre,
            String descripcion,
            String duracion,
            int horas,
            int creditos,
            String url,
            List<String> nombresPrevias) {

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

        Instituto instituto = persistencia.buscarInstituto(nombreInstituto);

        if (instituto == null) {
            throw new IllegalArgumentException("El instituto ya no existe.");
        }

        if (persistencia.existeCurso(nombre.trim())) {
            throw new IllegalArgumentException(
                    "Ya existe ese nombre de curso en la plataforma.");
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

        Curso curso = new Curso(
                nombre.trim(),
                duracion.trim(),
                horas,
                creditos,
                LocalDate.now(),
                descripcion.trim(),
                url.trim()
        );

        curso.setInstituto(instituto);
        curso.setPrevias(previas);

        persistencia.altaCurso(curso);
    }
    
    @Override
    public List<String> listarNombresInstitutos() {
        List<String> nombres = new ArrayList<>();

        for (Instituto instituto : persistencia.listarInstitutos()) {
            nombres.add(instituto.getNombre());
        }

        return nombres;
    }

    @Override
    public List<String> listarNombresCursosPorInstituto(String nombreInstituto) {
        List<String> nombres = new ArrayList<>();

        if (nombreInstituto == null || nombreInstituto.isBlank()) {
            return nombres;
        }

        for (Curso curso :
                persistencia.listarCursosPorInstituto(nombreInstituto)) {
            nombres.add(curso.getNombre());
        }

        return nombres;
    }
    
    
}
