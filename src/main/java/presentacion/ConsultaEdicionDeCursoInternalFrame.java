/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;
import java.util.List;
import logica.Instituto;
import logica.Curso;
import logica.EdicionCurso;
import persistencia.ControladorPersistencia;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Nicolás
 */
public class ConsultaEdicionDeCursoInternalFrame extends javax.swing.JInternalFrame {

private ControladorPersistencia cp;

private void limpiarDetalleEdicion() {
    nombreTXT.setText("");
    cupoTXT.setText("");
    fechaInicioTXT.setText("");
    fechaFinTXT.setText("");
    fechaPublicacionTXT.setText("");
}

public void seleccionarEdicion(String nombre) {
    DefaultMutableTreeNode raiz =
            (DefaultMutableTreeNode) arbolEdiciones.getModel().getRoot();

    java.util.Enumeration<?> nodos = raiz.depthFirstEnumeration();

    while (nodos.hasMoreElements()) {
        DefaultMutableTreeNode nodo =
                (DefaultMutableTreeNode) nodos.nextElement();

        Object dato = nodo.getUserObject();

        if (dato instanceof EdicionCurso) {
            EdicionCurso edicion = (EdicionCurso) dato;

            if (edicion.getNombre().equals(nombre)) {
                TreePath ruta = new TreePath(nodo.getPath());

                arbolEdiciones.setSelectionPath(ruta);
                arbolEdiciones.scrollPathToVisible(ruta);
                mostrarEdicionSeleccionada();
                return;
            }
        }
    }

    throw new IllegalArgumentException(
            "La edición ya no está disponible."
    );
}
    
    public ConsultaEdicionDeCursoInternalFrame(ControladorPersistencia cp) {
        initComponents();
        this.cp = cp;

        arbolEdiciones.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION
        );

        arbolEdiciones.addTreeSelectionListener(evt -> mostrarEdicionSeleccionada());

        cargarArbolEdiciones();
        limpiarDetalleEdicion();

        setSize(750, 650);
    }
    
    private void cargarArbolEdiciones() {
    DefaultMutableTreeNode raiz =
            new DefaultMutableTreeNode("Institutos");

    try {
        for (Instituto instituto : cp.obtenerInstitutos()) {
            DefaultMutableTreeNode nodoInstituto =
                    new DefaultMutableTreeNode(instituto.getNombre());

            raiz.add(nodoInstituto);

            for (Curso curso : cp.obtenerCursosDeInstituto(instituto.getId())) {
                DefaultMutableTreeNode nodoCurso =
                        new DefaultMutableTreeNode(curso.getNombre());

                nodoInstituto.add(nodoCurso);

                for (EdicionCurso edicion : cp.listarEdicionesCurso(curso)) {
                    DefaultMutableTreeNode nodoEdicion =
                            new DefaultMutableTreeNode(edicion);

                    nodoCurso.add(nodoEdicion);
                }
            }
        }

        arbolEdiciones.setModel(new DefaultTreeModel(raiz));

        for (int i = arbolEdiciones.getRowCount() - 1; i >= 0; i--) {
            arbolEdiciones.collapseRow(i);
        }

    } catch (RuntimeException e) {
        arbolEdiciones.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("No se pudieron cargar las ediciones")
        ));

        JOptionPane.showMessageDialog(
                this,
                "No se pudieron cargar las ediciones: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void mostrarEdicionSeleccionada() {
        limpiarDetalleEdicion();

        DefaultMutableTreeNode nodo =
                (DefaultMutableTreeNode) arbolEdiciones.getLastSelectedPathComponent();

        if (nodo == null) {
            return;
        }

        Object dato = nodo.getUserObject();

        if (!(dato instanceof EdicionCurso)) {
            return;
        }

        EdicionCurso edicion = (EdicionCurso) dato;

        nombreTXT.setText(edicion.getNombre());

        cupoTXT.setText(
                edicion.getCupo() == -1
                        ? "Sin límite de cupo"
                        : String.valueOf(edicion.getCupo())
        );

        fechaInicioTXT.setText(
                edicion.getFechaInicio() == null
                        ? ""
                        : edicion.getFechaInicio().toString()
        );

        fechaFinTXT.setText(
                edicion.getFechaFin() == null
                        ? ""
                        : edicion.getFechaFin().toString()
        );

        fechaPublicacionTXT.setText(
                edicion.getFechaPublicacion() == null
                        ? ""
                        : edicion.getFechaPublicacion().toString()
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        list1 = new java.awt.List();
        jMenuItem1 = new javax.swing.JMenuItem();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        nombreTXT = new javax.swing.JTextField();
        cupoTXT = new javax.swing.JTextField();
        fechaInicioTXT = new javax.swing.JTextField();
        fechaFinTXT = new javax.swing.JTextField();
        fechaPublicacionTXT = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        arbolEdiciones = new javax.swing.JTree();

        list1.addActionListener(this::list1ActionPerformed);

        jMenuItem1.setText("jMenuItem1");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Consulta de Edicion de Curso");

        jLabel5.setText("Nombre: ");

        jLabel6.setText("Cupo: ");

        jLabel7.setText("Fecha Inicio:");

        jLabel8.setText("Fecha Fin:");

        jLabel9.setText("Fecha Publicacion:");

        nombreTXT.setEditable(false);
        nombreTXT.addActionListener(this::nombreTXTActionPerformed);

        cupoTXT.setEditable(false);

        fechaInicioTXT.setEditable(false);

        fechaFinTXT.setEditable(false);

        fechaPublicacionTXT.setEditable(false);

        jButton1.setText("Salir");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jScrollPane1.setViewportView(arbolEdiciones);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaPublicacionTXT, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaFinTXT))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaInicioTXT))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cupoTXT))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombreTXT))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(36, 36, 36))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nombreTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cupoTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(fechaInicioTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(fechaFinTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(fechaPublicacionTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(jButton1)
                .addGap(33, 33, 33))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    private void list1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_list1ActionPerformed

    private void nombreTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreTXTActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbolEdiciones;
    private javax.swing.JTextField cupoTXT;
    private javax.swing.JTextField fechaFinTXT;
    private javax.swing.JTextField fechaInicioTXT;
    private javax.swing.JTextField fechaPublicacionTXT;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.List list1;
    private javax.swing.JTextField nombreTXT;
    // End of variables declaration//GEN-END:variables
}
