/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;
import persistencia.ControladorPersistencia;
import javax.swing.DefaultListModel;
import java.util.*;
import java.time.LocalDate;
import java.time.Month;
import javax.swing.JOptionPane;
import logica.*;

public class AltaEdicionCurso extends javax.swing.JInternalFrame {
    
    private ControladorPersistencia cp;
    private DefaultListModel<String> modeloDocentesDispo;
    private DefaultListModel<String> modeloDocentesSelec;
    private ArrayList<Curso> cursosActuales = new ArrayList<>();
    
    /**
     * Creates new form AltaEdicionCurso
     */
    public AltaEdicionCurso(ControladorPersistencia cp) {
        
        this.cp = cp;
        initComponents();
        FechasFormulario.configurar(dcInicio);
        FechasFormulario.configurar(dcFin);
        
        modeloDocentesDispo = new DefaultListModel<>();
        modeloDocentesSelec = new DefaultListModel<>();
        
        ArrayList<Docente> docentes = cp.listarDocentes();
        
        for(int i = 0; i < docentes.size(); i++)
        {
            Docente docente = docentes.get(i);
            modeloDocentesDispo.addElement(docente.getNick());
        }
        
        ListaDocentesDisponibles.setModel(modeloDocentesDispo);
        ListaDocentesSeleccionados.setModel(modeloDocentesSelec);
        
        
        
        ArrayList<Instituto> institutos = cp.listarInstitutos();
        
        for(int i = 0; i < institutos.size(); i++)
        {
            Instituto instituto = institutos.get(i);
            ComboInstituto.addItem(instituto.getNombre());
        }
        
  
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ComboInstituto = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        ComboCurso = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        ListaDocentesDisponibles = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListaDocentesSeleccionados = new javax.swing.JList<>();
        btnAgregarDocente = new javax.swing.JButton();
        btnQuitarDocente = new javax.swing.JButton();
        txtNombreEdicion = new javax.swing.JTextField();
        txtCupos = new javax.swing.JTextField();
        btnAceptarEdicion = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        dcInicio = new com.toedter.calendar.JDateChooser();
        dcFin = new com.toedter.calendar.JDateChooser();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ALTA EDICION CURSO");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Nombre de la edición:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Fecha de inicio:");

        jLabel4.setText("Fecha de finalización:");

        jLabel5.setText("Cupos:");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Instituto:");

        ComboInstituto.setMaximumSize(new java.awt.Dimension(250, 250));
        ComboInstituto.addActionListener(this::ComboInstitutoActionPerformed);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Curso:");

        ComboCurso.setMaximumSize(new java.awt.Dimension(250, 250));
        ComboCurso.addActionListener(this::ComboCursoActionPerformed);

        jScrollPane1.setViewportView(ListaDocentesDisponibles);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Docentes disponibles:");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Docentes Seleccionados:");

        jScrollPane2.setViewportView(ListaDocentesSeleccionados);

        btnAgregarDocente.setText("Agregar");
        btnAgregarDocente.addActionListener(this::btnAgregarDocenteActionPerformed);

        btnQuitarDocente.setText("Quitar");
        btnQuitarDocente.addActionListener(this::btnQuitarDocenteActionPerformed);

        txtCupos.addActionListener(this::txtCuposActionPerformed);

        btnAceptarEdicion.setText("Aceptar");
        btnAceptarEdicion.addActionListener(this::btnAceptarEdicionActionPerformed);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(ComboInstituto, 0, 200, Short.MAX_VALUE)
                                            .addComponent(ComboCurso, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtNombreEdicion, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                            .addComponent(txtCupos)
                                            .addComponent(dcInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(dcFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                                .addGap(93, 93, 93)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(113, 113, 113)
                                .addComponent(btnAceptarEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(135, 135, 135)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 124, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(btnAgregarDocente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQuitarDocente)
                .addGap(167, 167, 167))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(ComboInstituto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ComboCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombreEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(dcInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(dcFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarDocente))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnQuitarDocente)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptarEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void ComboInstitutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboInstitutoActionPerformed
      String institutoSeleccionado =
            (String) ComboInstituto.getSelectedItem();

    if (institutoSeleccionado == null) {
        return;
    }

    ComboCurso.removeAllItems();

    cursosActuales =
            cp.listarCursosPorInstituto(institutoSeleccionado);

    for (Curso curso : cursosActuales) {

        String nombre = curso.getNombre();
        String nombreVisible = nombre;

        if (nombre.length() > 35) {
            nombreVisible = nombre.substring(0, 32) + "...";
        }

        ComboCurso.addItem(nombreVisible);
    }
    
    }//GEN-LAST:event_ComboInstitutoActionPerformed

    private void ComboCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboCursoActionPerformed
     
    }//GEN-LAST:event_ComboCursoActionPerformed

    private void btnAgregarDocenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarDocenteActionPerformed
String seleccionado = ListaDocentesDisponibles.getSelectedValue();
if (seleccionado == null) return;
if (!modeloDocentesSelec.contains(seleccionado)) modeloDocentesSelec.addElement(seleccionado);
modeloDocentesDispo.removeElement(seleccionado);
    }//GEN-LAST:event_btnAgregarDocenteActionPerformed

    private void txtCuposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCuposActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCuposActionPerformed

    private void btnAceptarEdicionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarEdicionActionPerformed
try {
 String nombreInstituto =
        (String) ComboInstituto.getSelectedItem();

int indiceCurso = ComboCurso.getSelectedIndex();

if (nombreInstituto == null || indiceCurso < 0) {
    throw new IllegalArgumentException(
        "Seleccione instituto y curso."
    );
}

Instituto instituto =
        cp.buscarInstituto(nombreInstituto);

if (instituto == null) {
    throw new IllegalArgumentException(
        "El instituto ya no existe."
    );
}

if (indiceCurso >= cursosActuales.size()) {
    throw new IllegalArgumentException(
        "El curso ya no está disponible."
    );
}

Curso curso = cursosActuales.get(indiceCurso);
    String nombre = txtNombreEdicion.getText().trim();
    LocalDate inicio = FechasFormulario.leer(
        dcInicio, "la fecha de inicio"
    );
    LocalDate fin = FechasFormulario.leer(
        dcFin, "la fecha de finalización"
    );
    String textoCupo = txtCupos.getText().trim();
    int cupo = textoCupo.isEmpty() ? -1 : Integer.parseInt(textoCupo);
    Set<Docente> docentes = new HashSet<>();
    for (int i = 0; i < modeloDocentesSelec.size(); i++) {
        String nick = modeloDocentesSelec.getElementAt(i);
        Docente docente = cp.buscarDocentePorNick(nick);
        if (docente == null) throw new IllegalArgumentException("El docente " + nick + " ya no existe.");
        docentes.add(docente);
    }
    EdicionCurso edicion = new EdicionCurso(nombre, inicio, fin, cupo, LocalDate.now());
    edicion.setDocentes(docentes);
    cp.altaEdicionCurso(curso, edicion);
    JOptionPane.showMessageDialog(this, "Edición de curso creada correctamente.");
    dispose();
} catch (java.time.DateTimeException e) {
    JOptionPane.showMessageDialog(this, "Ingrese fechas reales de inicio y fin.");
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "El cupo debe ser entero; deje vacío para no limitarlo.");
} catch (RuntimeException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, e.getMessage(), "No se creó la edición", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnAceptarEdicionActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnQuitarDocenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarDocenteActionPerformed
String seleccionado = ListaDocentesSeleccionados.getSelectedValue();
if (seleccionado == null) return;
if (!modeloDocentesDispo.contains(seleccionado)) modeloDocentesDispo.addElement(seleccionado);
modeloDocentesSelec.removeElement(seleccionado);
    }//GEN-LAST:event_btnQuitarDocenteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboCurso;
    private javax.swing.JComboBox<String> ComboInstituto;
    private javax.swing.JList<String> ListaDocentesDisponibles;
    private javax.swing.JList<String> ListaDocentesSeleccionados;
    private javax.swing.JButton btnAceptarEdicion;
    private javax.swing.JButton btnAgregarDocente;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnQuitarDocente;
    private com.toedter.calendar.JDateChooser dcFin;
    private com.toedter.calendar.JDateChooser dcInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtCupos;
    private javax.swing.JTextField txtNombreEdicion;
    // End of variables declaration//GEN-END:variables
}
