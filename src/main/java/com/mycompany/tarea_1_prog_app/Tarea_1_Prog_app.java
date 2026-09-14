/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tarea_1_prog_app;
import javax.swing.SwingUtilities;
import presentacion.*;

/**
 *
 * @author Nicolás
 */
public class Tarea_1_Prog_app {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            PrincipalFrame ventanaPrincipal = new PrincipalFrame();
            ventanaPrincipal.setVisible(true);
        });
        
    }
}
