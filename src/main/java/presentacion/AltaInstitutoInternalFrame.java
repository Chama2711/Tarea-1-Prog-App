/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

/**
 *
 * @author elizeth
 */
import javax.swing.JOptionPane;
import persistencia.ControladorPersistencia;


public class AltaInstitutoInternalFrame extends javax.swing.JInternalFrame {
   private ControladorPersistencia controlPersistencia;
    
    
    public AltaInstitutoInternalFrame(ControladorPersistencia controlPersistencia) {
        initComponents();
       this.controlPersistencia = controlPersistencia;
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNombreInstituto = new javax.swing.JLabel();
        txtNombreInstituto = new java.awt.TextField();
        btnAlta = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Alta Instituto");

        lblNombreInstituto.setText("Nombre del Instituto:");

        btnAlta.setText("Aceptar");
        btnAlta.addActionListener(this::btnAltaActionPerformed);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNombreInstituto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAlta)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar))
                    .addComponent(txtNombreInstituto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNombreInstituto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreInstituto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAlta)
                    .addComponent(btnCancelar))
                .addGap(98, 98, 98))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
    String nombre = txtNombreInstituto.getText().trim();

    if (nombre.isEmpty()) {
    JOptionPane.showMessageDialog(this,"Debe ingresar el nombre del instituto.","Advertencia", JOptionPane.WARNING_MESSAGE);
    txtNombreInstituto.requestFocus();
    return;
       }
    try {
    controlPersistencia.altaInstituto(nombre);

    JOptionPane.showMessageDialog(this,"Instituto registrado correctamente.","Alta de Instituto",JOptionPane.INFORMATION_MESSAGE);

    txtNombreInstituto.setText("");
    
     } catch (Exception e) {
    JOptionPane.showMessageDialog(this,e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        
    }//GEN-LAST:event_btnAltaActionPerformed
}
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel lblNombreInstituto;
    private java.awt.TextField txtNombreInstituto;
    // End of variables declaration//GEN-END:variables
}
