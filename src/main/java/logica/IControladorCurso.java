/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logica;
import java.util.List;

/**
 *
 * @author Nicolás
 */

public interface IControladorCurso {

    void altaCurso(
            String nombreInstituto,
            String nombre,
            String descripcion,
            String duracion,
            int horas,
            int creditos,
            String url,
            List<String> nombresPrevias
    );
    
    List<String> listarNombresInstitutos();

    List<String> listarNombresCursosPorInstituto(String nombreInstituto);
    
}
