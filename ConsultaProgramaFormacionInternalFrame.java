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
import logica.Curso;
import logica.ProgramaFormacion;
import persistencia.ControladorPersistencia;

/**
 *
 * @author elizeth
 */
public class ConsultaProgramaFormacionInternalFrame extends javax.swing.JInternalFrame {

private JComboBox<String> cbProgramas;
    private JTextArea txtDescripcion;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private DefaultListModel<String> modelCursos;
    private JList<String> listCursos;
    private JButton btnCerrar;

    private ControladorPersistencia controlPersistencia;

    public ConsultaProgramaFormacionInternalFrame(ControladorPersistencia controlPersistencia) {
        this.controlPersistencia = controlPersistencia;

        setTitle("Consulta de Programa de Formación");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(480, 420);

        initGUI();
        cargarProgramas();
    }

    private void initGUI() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Combo de selección superior
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Seleccione Programa:"));
        cbProgramas = new JComboBox<>();
        panelTop.add(cbProgramas);
        panelPrincipal.add(panelTop, BorderLayout.NORTH);

        // Panel central con datos del programa
        JPanel panelDatos = new JPanel(new GridLayout(4, 2, 5, 5));
        
        txtFechaInicio = new JTextField();
        txtFechaInicio.setEditable(false);
        txtFechaFin = new JTextField();
        txtFechaFin.setEditable(false);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setEditable(false);

        panelDatos.add(new JLabel("Fecha Inicio:"));
        panelDatos.add(txtFechaInicio);
        panelDatos.add(new JLabel("Fecha Fin:"));
        panelDatos.add(txtFechaFin);
        panelDatos.add(new JLabel("Descripción:"));
        panelDatos.add(new JScrollPane(txtDescripcion));

        // Lista de Cursos
        modelCursos = new DefaultListModel<>();
        listCursos = new JList<>(modelCursos);
        
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.add(panelDatos, BorderLayout.NORTH);
        panelCentro.add(new JLabel("Cursos que integran el programa:"), BorderLayout.CENTER);
        panelCentro.add(new JScrollPane(listCursos), BorderLayout.SOUTH);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Botón inferior
        btnCerrar = new JButton("Cerrar");
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.add(btnCerrar);
        panelPrincipal.add(panelBottom, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Eventos
        cbProgramas.addActionListener((ActionEvent e) -> mostrarDetallesPrograma());
        btnCerrar.addActionListener((ActionEvent e) -> dispose());
    }

    private void cargarProgramas() {
        try {
            cbProgramas.removeAllItems();
            List<String> programas = controlPersistencia.listarNombresProgramas();
            for (String prog : programas) {
                cbProgramas.addItem(prog);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener programas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetallesPrograma() {
        String seleccionado = (String) cbProgramas.getSelectedItem();
        if (seleccionado == null) return;

        try {
            ProgramaFormacion pf = controlPersistencia.obtenerDetallePrograma(seleccionado);
            
            txtDescripcion.setText(pf.getDescripcion());
            txtFechaInicio.setText(pf.getFechaInicio() != null ? pf.getFechaInicio().toString() : "");
            txtFechaFin.setText(pf.getFechaFin() != null ? pf.getFechaFin().toString() : "");

            modelCursos.clear();
            if (pf.getCursos() != null) {
                for (Curso c : pf.getCursos().values()) {
                    modelCursos.addElement(c.getNombre());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
