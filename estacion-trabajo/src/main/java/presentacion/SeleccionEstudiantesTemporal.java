package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import logica.*;

/** Pantalla auxiliar para probar la selección antes de construir la web. */
public class SeleccionEstudiantesTemporal extends JInternalFrame {
    private static final String SIN_CAMBIOS = "Sin cambios";
    private final IServidorCentral servidor;
    private final JComboBox<String> comboDocente = new JComboBox<>();
    private final JComboBox<EdicionCurso> comboEdicion = new JComboBox<>();
    private final JCheckBox soloAceptados = new JCheckBox("Solo aceptados");
    private final JButton guardar = new JButton("Guardar selección");
    private final JButton recargar = new JButton("Recargar");
    private final JButton resultados = new JButton("Ver resultados del estudiante");
    private final JLabel mensaje = new JLabel("Seleccione un docente.");
    private List<DTInscripcion> inscripciones = List.of();
    private boolean cargando;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Nickname", "Estudiante", "Fecha de inscripción", "Estado actual", "Decisión"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) {
            return columna == 4 && !soloAceptados.isSelected()
                    && inscripciones.get(fila).getEstado() == EstadoInscripcion.INSCRIPTO;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public SeleccionEstudiantesTemporal(IServidorCentral servidor) {
        super("Selección de estudiantes (temporal)", true, true, true, true);
        this.servidor = servidor;
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboDocente.setPreferredSize(new Dimension(150, 25));
        comboEdicion.setPreferredSize(new Dimension(280, 25));
        filtros.add(new JLabel("Docente:"));
        filtros.add(comboDocente);
        filtros.add(new JLabel("Edición vigente:"));
        filtros.add(comboEdicion);
        filtros.add(soloAceptados);

        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JComboBox<>(
                new Object[]{SIN_CAMBIOS, EstadoInscripcion.ACEPTADA, EstadoInscripcion.RECHAZADA})));
        JScrollPane listado = new JScrollPane(tabla);
        listado.setColumnHeaderView(tabla.getTableHeader());
        listado.setPreferredSize(new Dimension(900, 280));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cerrar = new JButton("Cerrar");
        acciones.add(recargar);
        acciones.add(resultados);
        acciones.add(guardar);
        acciones.add(cerrar);
        JPanel pie = new JPanel(new BorderLayout(8, 8));
        pie.add(mensaje, BorderLayout.NORTH);
        pie.add(new JLabel("Elegí Aceptada o Rechazada en Decisión y pulsá Guardar selección."), BorderLayout.CENTER);
        pie.add(acciones, BorderLayout.SOUTH);
        JPanel contenido = new JPanel(new BorderLayout(8, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(filtros, BorderLayout.NORTH);
        contenido.add(listado, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);

        comboDocente.addActionListener(evt -> { if (!cargando) cargarEdiciones(); });
        comboEdicion.addActionListener(evt -> { if (!cargando) cargarInscripciones(); });
        soloAceptados.addActionListener(evt -> cargarInscripciones());
        recargar.addActionListener(evt -> cargarInscripciones());
        resultados.addActionListener(evt -> verResultadosEstudiante());
        guardar.addActionListener(evt -> guardarSeleccion());
        cerrar.addActionListener(evt -> dispose());
        guardar.setEnabled(false);
        recargar.setEnabled(false);
        resultados.setEnabled(false);
        cargarDocentes();
        pack();
    }

    private void cargarDocentes() {
        cargando = true;
        try {
            for (String nick : servidor.listarNicknamesDocentes()) comboDocente.addItem(nick);
            comboDocente.setSelectedIndex(-1);
        } catch (RuntimeException e) {
            mostrarError(e);
        } finally {
            cargando = false;
        }
    }

    private void cargarEdiciones() {
        cargando = true;
        comboEdicion.removeAllItems();
        try {
            String nick = (String) comboDocente.getSelectedItem();
            if (nick != null) {
                List<EdicionCurso> ediciones = new ArrayList<>();
                for (Curso curso : servidor.listarCursos()) {
                    for (EdicionCurso edicion : servidor.listarEdicionesCurso(curso)) {
                        if (edicion.esVigente() && edicion.getDocentes().stream()
                                .anyMatch(docente -> nick.equals(docente.getNick()))) ediciones.add(edicion);
                    }
                }
                ediciones.sort(java.util.Comparator.comparing(EdicionCurso::getNombre));
                for (EdicionCurso edicion : ediciones) comboEdicion.addItem(edicion);
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        } finally {
            cargando = false;
        }
        cargarInscripciones();
    }

    private void cargarInscripciones() {
        if (tabla.isEditing()) tabla.getCellEditor().cancelCellEditing();
        modelo.setRowCount(0);
        inscripciones = List.of();
        guardar.setEnabled(false);
        recargar.setEnabled(false);
        resultados.setEnabled(false);
        EdicionCurso edicion = (EdicionCurso) comboEdicion.getSelectedItem();
        String docente = (String) comboDocente.getSelectedItem();
        if (edicion == null || docente == null) {
            mensaje.setText(docente == null ? "Seleccione un docente."
                    : "Este docente no tiene ediciones vigentes en las que participe.");
            return;
        }
        try {
            inscripciones = soloAceptados.isSelected()
                    ? servidor.listarAceptadosEdicion(docente, edicion.getNombre())
                    : servidor.listarInscripcionesEdicion(docente, edicion.getNombre());
            for (DTInscripcion inscripcion : inscripciones) {
                modelo.addRow(new Object[]{inscripcion.getNicknameEstudiante(), inscripcion.getNombreEstudiante(),
                    inscripcion.getFechaInscripcion(), inscripcion.getEstado(), SIN_CAMBIOS});
            }
            long aceptadas = inscripciones.stream().filter(i -> i.getEstado() == EstadoInscripcion.ACEPTADA).count();
            mensaje.setText("Cupo: " + (edicion.getCupo() == -1 ? "sin límite" : edicion.getCupo())
                    + " | Aceptados: " + aceptadas + " | Inscripciones mostradas: " + inscripciones.size());
            guardar.setEnabled(!soloAceptados.isSelected()
                    && inscripciones.stream().anyMatch(i -> i.getEstado() == EstadoInscripcion.INSCRIPTO));
            recargar.setEnabled(true);
            resultados.setEnabled(!inscripciones.isEmpty());
        } catch (RuntimeException e) {
            inscripciones = List.of();
            modelo.setRowCount(0);
            mensaje.setText("No se pudieron cargar las inscripciones. Cambie la selección para reintentar.");
            mostrarError(e);
        }
    }

    private void guardarSeleccion() {
        if (tabla.isEditing() && !tabla.getCellEditor().stopCellEditing()) return;
        EdicionCurso edicion = (EdicionCurso) comboEdicion.getSelectedItem();
        if (edicion == null || soloAceptados.isSelected()) return;
        Map<Long, EstadoInscripcion> decisiones = new LinkedHashMap<>();
        for (int fila = 0; fila < inscripciones.size(); fila++) {
            if (modelo.getValueAt(fila, 4) instanceof EstadoInscripcion estado) {
                decisiones.put(inscripciones.get(fila).getId(), estado);
            }
        }
        if (decisiones.isEmpty()) {
            mensaje.setText("Elegí al menos una decisión antes de guardar.");
            return;
        }
        try {
            servidor.seleccionarEstudiantes((String) comboDocente.getSelectedItem(), edicion.getNombre(), decisiones);
            cargarInscripciones();
            mensaje.setText("Selección guardada. " + mensaje.getText());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(RuntimeException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Selección de estudiantes", JOptionPane.WARNING_MESSAGE);
    }

    private void verResultadosEstudiante() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mensaje.setText("Seleccione una fila para ver los resultados de ese estudiante.");
            return;
        }
        if (getDesktopPane() == null) return;
        String nick = inscripciones.get(tabla.convertRowIndexToModel(fila)).getNicknameEstudiante();
        try {
            DefaultTableModel datos = new DefaultTableModel(new Object[]{"Curso", "Edición", "Fecha", "Estado"}, 0) {
                @Override public boolean isCellEditable(int fila, int columna) { return false; }
            };
            for (DTInscripcion inscripcion : servidor.listarResultadosInscripciones(nick)) {
                datos.addRow(new Object[]{inscripcion.getNombreCurso(), inscripcion.getNombreEdicion(),
                    inscripcion.getFechaInscripcion(), inscripcion.getEstado()});
            }
            JTable listado = new JTable(datos);
            listado.setAutoCreateRowSorter(true);
            listado.setRowHeight(26);
            JScrollPane scroll = new JScrollPane(listado);
            scroll.setColumnHeaderView(listado.getTableHeader());
            scroll.setPreferredSize(new Dimension(700, 250));
            JInternalFrame ventana = new JInternalFrame("Resultados de " + nick, true, true, true, true);
            ventana.add(scroll);
            ventana.pack();
            getDesktopPane().add(ventana);
            ventana.setVisible(true);
            ventana.toFront();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }
}
