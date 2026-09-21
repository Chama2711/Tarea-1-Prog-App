/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import javax.swing.*;
import java.util.List;
import persistencia.ControladorPersistencia;

public class AgregarCursoAProgramaInternalFrame extends javax.swing.JInternalFrame {

    private ControladorPersistencia controlPersistencia;

    public AgregarCursoAProgramaInternalFrame(ControladorPersistencia controlPersistencia) {
      

    this.controlPersistencia = controlPersistencia;

    initComponents();

    setTitle("Agregar Curso a Programa de Formación");

    jComboBox1.setPrototypeDisplayValue(
            "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    );

    jComboBox2.setPrototypeDisplayValue(
            "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    );

    cargarCombos();

    pack();
}

 private void cargarCombos() {
    try {
        jComboBox1.removeAllItems();

        List<String> programas =
                controlPersistencia.listarNombresProgramas();

        for (String prog : programas) {
            jComboBox1.addItem(prog);
        }

        jComboBox2.removeAllItems();

        List<String> cursos =
                controlPersistencia.listarNombresCursos();

        for (String curso : cursos) {
            jComboBox2.addItem(curso);
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "Error al cargar datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        btAceptar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Agregar Curso a Programa de Formacion");

        jLabel1.setText("Programa de Formacion:");

        jLabel2.setText("Curso a agregar:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setMaximumSize(new java.awt.Dimension(350, 350));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(this::jComboBox2ActionPerformed);

        btAceptar.setText("Aceptar");
        btAceptar.addActionListener(this::btAceptarActionPerformed);

        btCancelar.setText("Cancelar");
        btCancelar.addActionListener(this::btCancelarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(71, 71, 71)
                        .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btAceptar)
                .addGap(18, 18, 18)
                .addComponent(btCancelar)
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btAceptar)
                    .addComponent(btCancelar))
                .addGap(28, 28, 28))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jComboBox2});

        getAccessibleContext().setAccessibleName("Agrugar Curso a Programa de Formacion");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarActionPerformed


    String prog =
            (String) jComboBox1.getSelectedItem();

    String curso =
            (String) jComboBox2.getSelectedItem();

    if (prog == null || curso == null) {
        JOptionPane.showMessageDialog(
                this,
                "Debe seleccionar un programa y un curso.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {

        controlPersistencia.agregarCursoAPrograma(
                prog,
                curso
        );

        JOptionPane.showMessageDialog(
                this,
                "Curso agregado con éxito.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    
    }//GEN-LAST:event_btAceptarActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
       this.dispose(); // Simplemente cierra la ventana
    }//GEN-LAST:event_btCancelarActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
