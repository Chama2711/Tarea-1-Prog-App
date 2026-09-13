/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import logica.Curso;
import logica.ProgramaFormacion;
import persistencia.ControladorPersistencia;

/**
 *
 * @author elizeth
 */
public class ConsultaProgramaFormacionInternalFrame extends javax.swing.JInternalFrame {

    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode; //Nodo raiz

    private ControladorPersistencia controlPersistencia;

    public ConsultaProgramaFormacionInternalFrame(ControladorPersistencia controlPersistencia) {
    initComponents();
    this.controlPersistencia = controlPersistencia;

    // Inicializar árbol
    rootNode = new DefaultMutableTreeNode("Seleccione un Programa");
    treeModel = new DefaultTreeModel(rootNode);
    jtreeProgramasCursos.setModel(treeModel);

    // ESCUCHADOR MANUAL PARA EL COMBOBOX
    cbProgramas.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            mostrarDetallesPrograma();
        }
    });

    cargarProgramas();
    }



    private void cargarProgramas() {
        if (cbProgramas == null) return; // Validación anti-NullPointerException

    cbProgramas.removeAllItems();
    List<ProgramaFormacion> programas = controlPersistencia.obtenerProgramas();
    
    if (programas != null) {
        for (ProgramaFormacion p : programas) {
            cbProgramas.addItem(p.getNombre());
        }
    }
        /*} catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener programas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }*/
    }
    private void cargarArbolProgramas(ProgramaFormacion programa){
        if (programa == null) {
        rootNode.setUserObject("Programa no encontrado");
        treeModel.reload();
        return;
     }   
        // 1. Nodo Raíz con el Nombre del Programa
        rootNode = new DefaultMutableTreeNode("Programa: " + programa.getNombre());
        
        // 2. Rama de Información General
        DefaultMutableTreeNode nodoInfo = new DefaultMutableTreeNode("Información General");
        nodoInfo.add(new DefaultMutableTreeNode("Fecha Inicio: " + programa.getFechaInicio()));
        nodoInfo.add(new DefaultMutableTreeNode("Fecha Fin: " + programa.getFechaFin()));
        nodoInfo.add(new DefaultMutableTreeNode("Descripción: " + programa.getDescripcion()));
        rootNode.add(nodoInfo);
        
        // 3. Rama de Cursos Integrantes
        DefaultMutableTreeNode nodoCursos = new DefaultMutableTreeNode("Cursos Integrantes (" + programa.getCursos().size() + ")");
        
        if (programa.getCursos() != null && !programa.getCursos().isEmpty()) {
            
       for (Curso c : programa.getCursos().values()) {
       // Se agrega cada curso como hoja/nodo del árbol
        nodoCursos.add(new DefaultMutableTreeNode(c.getNombre()));
        }
       
        } else {
        nodoCursos.add(new DefaultMutableTreeNode("Sin cursos asignados"));
        }
    
        rootNode.add(nodoCursos);

       // 4. Actualizar el modelo del JTree y expandir todas las ramas
       treeModel.setRoot(rootNode);
       treeModel.reload();

      // Expandir las ramas por defecto para que no aparezcan colapsadas
       for (int i = 0; i < jtreeProgramasCursos.getRowCount(); i++) {
        jtreeProgramasCursos.expandRow(i);
       }
        
    }

    private void mostrarDetallesPrograma() {
String seleccionado = (String) cbProgramas.getSelectedItem();
    if (seleccionado == null || seleccionado.isEmpty()) return;

    try {
        ProgramaFormacion pf = controlPersistencia.obtenerDetallePrograma(seleccionado);

        if (pf != null) {
            // Reemplaza por el nombre exacto de tus campos (ej. jTextField1 / txtFechaInicio)
            txtFechaInicio.setText(pf.getFechaInicio() != null ? pf.getFechaInicio().toString() : "");
            txtFechaFin.setText(pf.getFechaFin() != null ? pf.getFechaFin().toString() : "");
            txtAreaDescripcion.setText(pf.getDescripcion() != null ? pf.getDescripcion() : "");

            // Crear los nodos del JTree
            rootNode = new DefaultMutableTreeNode("Programa: " + pf.getNombre());
            DefaultMutableTreeNode nodoCursos = new DefaultMutableTreeNode("Cursos Integrantes (" + 
                (pf.getCursos() != null ? pf.getCursos().size() : 0) + ")");

            if (pf.getCursos() != null && !pf.getCursos().isEmpty()) {
                for (Curso c : pf.getCursos().values()) {
                    nodoCursos.add(new DefaultMutableTreeNode(c.getNombre()));
                }
            } else {
                nodoCursos.add(new DefaultMutableTreeNode("Sin cursos asignados"));
            }

            rootNode.add(nodoCursos);

            // Refrescar y expandir
            treeModel.setRoot(rootNode);
            treeModel.reload();

            for (int i = 0; i < jtreeProgramasCursos.getRowCount(); i++) {
                jtreeProgramasCursos.expandRow(i);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbProgramas = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaDescripcion = new javax.swing.JTextArea();
        txtFechaInicio = new javax.swing.JTextField();
        txtFechaFin = new javax.swing.JTextField();
        btCerrar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtreeProgramasCursos = new javax.swing.JTree();

        jLabel1.setText("Seleccione Programa:");

        jLabel2.setText("Fecha Inicio:");

        jLabel4.setText("Fecha FIn:");

        cbProgramas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Descripcion:");

        jLabel6.setText("Cursos:");

        txtAreaDescripcion.setColumns(20);
        txtAreaDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtAreaDescripcion);

        jScrollPane1.setViewportView(jScrollPane2);

        txtFechaInicio.setText("jTextField1");

        txtFechaFin.setText("jTextField2");

        btCerrar.setText("Cerrar");
        btCerrar.addActionListener(this::btCerrarActionPerformed);

        jScrollPane3.setViewportView(jtreeProgramasCursos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cbProgramas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btCerrar)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addGap(32, 32, 32)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1)
                                .addComponent(jScrollPane3)))))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbProgramas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(64, 64, 64)
                .addComponent(btCerrar)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCerrarActionPerformed
        this.dispose(); 
    }//GEN-LAST:event_btCerrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCerrar;
    private javax.swing.JComboBox<String> cbProgramas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTree jtreeProgramasCursos;
    private javax.swing.JTextArea txtAreaDescripcion;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    // End of variables declaration//GEN-END:variables
}
