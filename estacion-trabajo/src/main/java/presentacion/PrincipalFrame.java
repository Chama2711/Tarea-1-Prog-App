/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package presentacion;


import javax.swing.*;
import logica.IServidorCentral;
import logica.ServidorCentral;

/**
 *
 * @author elizeth
 */
public class PrincipalFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PrincipalFrame.class.getName());

    // 1. El contenedor virtual donde flotarán los JInternalFrame
    private void abrirInternalFrame(JInternalFrame frame) {

            // Evitar ventanas duplicadas
        for (JInternalFrame abierto : jDesktopPane1.getAllFrames()) {

            if (abierto.getClass().equals(frame.getClass())) {

                try {
                    abierto.setSelected(true);
                    abierto.toFront();
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }

                return;
            }
        }

        // Agregar la ventana
        jDesktopPane1.add(frame);
        
       

        // Ajustar tamaño
        frame.pack();

        // Centrar
        int x = (jDesktopPane1.getWidth() - frame.getWidth()) / 2;
        int y = (jDesktopPane1.getHeight() - frame.getHeight()) / 2;

        frame.setLocation(Math.max(x, 0), Math.max(y, 0));

        frame.setVisible(true);

        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }
    // 2. La instancia de la lógica/persistencia
    private IServidorCentral servidorCentral;
    private logica.ControladorUsuario controlUsuario;

    public PrincipalFrame() {
        initComponents();
        
    servidorCentral = new ServidorCentral();
    controlUsuario = new logica.ControladorUsuario(servidorCentral);
  

        setTitle("edEXT - Plataforma Educativa");
        setLocationRelativeTo(null);

        // Apariencia del escritorio
        jDesktopPane1.setBackground(new java.awt.Color(245, 247, 250));

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

 

    // Método Main para iniciar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrincipalFrame ventanaPrincipal = new PrincipalFrame();
            ventanaPrincipal.setVisible(true);
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSpinner1 = new javax.swing.JSpinner();
        jMenu2 = new javax.swing.JMenu();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuAltaUsuario = new javax.swing.JMenuItem();
        menuAgregarCursoProg = new javax.swing.JMenuItem();
        menuInscripcionEdicionCurso = new javax.swing.JMenuItem();
        menuAltaProgFormacion = new javax.swing.JMenuItem();
        menuAltaInstituto = new javax.swing.JMenuItem();
        MenuAltaCurso = new javax.swing.JMenuItem();
        menuAltaEdicionCurso = new javax.swing.JMenuItem();
        menuAltaCategoria = new javax.swing.JMenuItem();
        menuConsultaProg = new javax.swing.JMenu();
        menuConsultaUsuario = new javax.swing.JMenuItem();
        menuConsultaCurso = new javax.swing.JMenuItem();
        menuConsultaProgFormacion = new javax.swing.JMenuItem();
        menuConsultaEdicionCurso = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuModificarUsuario = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        jMenu2.setText("jMenu2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jMenu1.setText("Registros");

        menuAltaUsuario.setText("Alta de Usuario");
        menuAltaUsuario.addActionListener(this::menuAltaUsuarioActionPerformed);
        jMenu1.add(menuAltaUsuario);

        menuAgregarCursoProg.setText("Agregar Curso a Programa");
        menuAgregarCursoProg.addActionListener(this::menuAgregarCursoProgActionPerformed);
        jMenu1.add(menuAgregarCursoProg);

        menuInscripcionEdicionCurso.setText("Inscripcion a Edicion de Curso");
        menuInscripcionEdicionCurso.addActionListener(this::InscripcionAEdicionDeCursoActionPerformed);
        jMenu1.add(menuInscripcionEdicionCurso);

        menuAltaProgFormacion.setText("Alta de Programa de Formacion");
        menuAltaProgFormacion.addActionListener(this::menuAltaProgFormacionActionPerformed);
        jMenu1.add(menuAltaProgFormacion);

        menuAltaInstituto.setText("Alta Instituto");
        menuAltaInstituto.addActionListener(this::menuAltaInstitutoActionPerformed);
        jMenu1.add(menuAltaInstituto);

        MenuAltaCurso.setText("Alta Curso");
        MenuAltaCurso.addActionListener(this::MenuAltaCursoActionPerformed);
        jMenu1.add(MenuAltaCurso);

        menuAltaEdicionCurso.setText("Alta Edicion de Curso");
        menuAltaEdicionCurso.addActionListener(this::menuAltaEdicionCursoActionPerformed);
        jMenu1.add(menuAltaEdicionCurso);

        menuAltaCategoria.setText("Alta Categoria");
        menuAltaCategoria.addActionListener(this::menuAltaCategoriaActionPerformed);
        jMenu1.add(menuAltaCategoria);

        jMenuBar1.add(jMenu1);

        menuConsultaProg.setText("Consultas");

        menuConsultaUsuario.setText("Consulta de Usuario");
        menuConsultaUsuario.addActionListener(this::menuConsultaUsuarioActionPerformed);
        menuConsultaProg.add(menuConsultaUsuario);

        menuConsultaCurso.setText("Consulta Curso");
        menuConsultaCurso.addActionListener(this::menuConsultaCursoActionPerformed);
        menuConsultaProg.add(menuConsultaCurso);

        menuConsultaProgFormacion.setText("Consulta Programa de Formacion");
        menuConsultaProgFormacion.addActionListener(this::menuConsultaProgFormacionActionPerformed);
        menuConsultaProg.add(menuConsultaProgFormacion);

        menuConsultaEdicionCurso.setText("Consulta de Edicion de Curso");
        menuConsultaEdicionCurso.addActionListener(this::menuConsultaEdicionCursoActionPerformed);
        menuConsultaProg.add(menuConsultaEdicionCurso);

        jMenuBar1.add(menuConsultaProg);

        jMenu3.setText("Modificar");

        menuModificarUsuario.setText("Modificar Usuario");
        menuModificarUsuario.addActionListener(this::menuModificarUsuarioActionPerformed);
        jMenu3.add(menuModificarUsuario);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuAgregarCursoProgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAgregarCursoProgActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame(new AgregarCursoAProgramaInternalFrame(servidorCentral));
    }//GEN-LAST:event_menuAgregarCursoProgActionPerformed

    private void InscripcionAEdicionDeCursoActionPerformed(java.awt.event.ActionEvent evt) {
    // Lógica para abrir la ventana Inscripcion a Edicion de Curso
    abrirInternalFrame(new InscripcionAEdicionDeCursoInternalFrame(servidorCentral));
}

private void menuAltaProgFormacionActionPerformed(java.awt.event.ActionEvent evt) {
    // Lógica para abrir la ventana de Alta de Programa de Formación
    abrirInternalFrame(new CrearProgramaDeFormacionInternalFrame(servidorCentral));
}
    
    private void menuConsultaProgFormacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConsultaProgFormacionActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame(new ConsultaProgramaFormacionInternalFrame(servidorCentral));
        
    }//GEN-LAST:event_menuConsultaProgFormacionActionPerformed

    private void menuConsultaEdicionCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConsultaEdicionCursoActionPerformed
        // TODO add your handling code here:

        abrirInternalFrame(new ConsultaEdicionDeCursoInternalFrame(servidorCentral));

    }//GEN-LAST:event_menuConsultaEdicionCursoActionPerformed

    private void menuAltaUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAltaUsuarioActionPerformed
        presentacion.InternalAltaUsuario ventanaAlta = new presentacion.InternalAltaUsuario(controlUsuario);
        jDesktopPane1.add(ventanaAlta);
        ventanaAlta.setVisible(true);
    }//GEN-LAST:event_menuAltaUsuarioActionPerformed

    private void menuConsultaUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConsultaUsuarioActionPerformed
        presentacion.InternalConsultaUsuario ventanaConsulta = new presentacion.InternalConsultaUsuario(controlUsuario,servidorCentral);
        jDesktopPane1.add(ventanaConsulta);
        ventanaConsulta.setVisible(true);
    }//GEN-LAST:event_menuConsultaUsuarioActionPerformed

    private void menuModificarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuModificarUsuarioActionPerformed
        presentacion.InternalModificarUsuario ventanaModificar = new presentacion.InternalModificarUsuario(controlUsuario);
        jDesktopPane1.add(ventanaModificar);
        ventanaModificar.setVisible(true);
    }//GEN-LAST:event_menuModificarUsuarioActionPerformed

    private void menuConsultaCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConsultaCursoActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame(new ConsultaCurso(servidorCentral));
    }//GEN-LAST:event_menuConsultaCursoActionPerformed

    private void MenuAltaCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuAltaCursoActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame(new AltaCurso(servidorCentral));
    }//GEN-LAST:event_MenuAltaCursoActionPerformed

    private void menuAltaEdicionCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAltaEdicionCursoActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame(new AltaEdicionCurso(servidorCentral));
    }//GEN-LAST:event_menuAltaEdicionCursoActionPerformed

    private void menuAltaInstitutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAltaInstitutoActionPerformed
        // TODO add your handling code here:
        abrirInternalFrame (new AltaInstitutoInternalFrame(servidorCentral));
    }//GEN-LAST:event_menuAltaInstitutoActionPerformed

    private void menuAltaCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAltaCategoriaActionPerformed
        // TODO add your handling code here:
        AltaCategoriaInternalFrame ventana = new AltaCategoriaInternalFrame(servidorCentral);
        jDesktopPane1.add(ventana);
        ventana.setVisible(true);
    }//GEN-LAST:event_menuAltaCategoriaActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenuAltaCurso;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JMenuItem menuAgregarCursoProg;
    private javax.swing.JMenuItem menuAltaCategoria;
    private javax.swing.JMenuItem menuAltaEdicionCurso;
    private javax.swing.JMenuItem menuAltaInstituto;
    private javax.swing.JMenuItem menuAltaProgFormacion;
    private javax.swing.JMenuItem menuAltaUsuario;
    private javax.swing.JMenuItem menuConsultaCurso;
    private javax.swing.JMenuItem menuConsultaEdicionCurso;
    private javax.swing.JMenu menuConsultaProg;
    private javax.swing.JMenuItem menuConsultaProgFormacion;
    private javax.swing.JMenuItem menuConsultaUsuario;
    private javax.swing.JMenuItem menuInscripcionEdicionCurso;
    private javax.swing.JMenuItem menuModificarUsuario;
    // End of variables declaration//GEN-END:variables
}
