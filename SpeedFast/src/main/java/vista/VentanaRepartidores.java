package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentanaRepartidores extends JFrame {

    private final RepartidorDAO dao = new RepartidorDAO();
    private final Runnable alCambiar;
    private final JTextField campoNombre = new JTextField();
    private final JLabel etiquetaId = new JLabel("ID: automático");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private List<Repartidor> repartidores = new ArrayList<>();
    private int idSeleccionado;
    private boolean actualizando;

    public VentanaRepartidores(Runnable alCambiar) {
        this.alCambiar = alCambiar;
        setTitle("SpeedFast - Repartidores");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 460);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.add(etiquetaId, BorderLayout.NORTH);
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        formulario.add(campoNombre, BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.NORTH);

        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton nuevo = new JButton("Nuevo");
        JButton registrar = new JButton("Registrar");
        JButton editar = new JButton("Guardar cambios");
        JButton eliminar = new JButton("Eliminar");
        JButton actualizar = new JButton("Actualizar");
        JButton cerrar = new JButton("Cerrar");
        JPanel botones = new JPanel(new GridLayout(2, 3, 8, 8));
        for (JButton boton : new JButton[]{nuevo, registrar, editar, eliminar, actualizar, cerrar}) {
            botones.add(boton);
        }
        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        nuevo.addActionListener(e -> limpiar());
        registrar.addActionListener(e -> guardar(false));
        editar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> eliminar());
        actualizar.addActionListener(e -> actualizarDatos());
        cerrar.addActionListener(e -> dispose());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizando) cargarSeleccion();
        });
    }

    public void actualizarDatos() {
        try {
            List<Repartidor> nuevos = dao.readAll();
            actualizando = true;
            repartidores = nuevos;
            modeloTabla.setRowCount(0);
            int seleccion = -1;
            for (int i = 0; i < repartidores.size(); i++) {
                Repartidor repartidor = repartidores.get(i);
                modeloTabla.addRow(new Object[]{repartidor.getId(), repartidor.getNombre()});
                if (repartidor.getId() == idSeleccionado) seleccion = i;
            }
            if (seleccion >= 0) tabla.setRowSelectionInterval(seleccion, seleccion);
            else if (idSeleccionado != 0) limpiar();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        } finally {
            actualizando = false;
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Repartidor repartidor = repartidores.get(fila);
        idSeleccionado = repartidor.getId();
        etiquetaId.setText("ID: " + idSeleccionado);
        campoNombre.setText(repartidor.getNombre());
    }

    private void limpiar() {
        idSeleccionado = 0;
        tabla.clearSelection();
        etiquetaId.setText("ID: automático");
        campoNombre.setText("");
        campoNombre.requestFocusInWindow();
    }

    private void guardar(boolean editar) {
        if (editar && idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona el repartidor que quieres editar.");
            return;
        }
        if (!editar && idSeleccionado != 0) {
            Mensajes.error(this, "Pulsa Nuevo para registrar otro repartidor.");
            return;
        }
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty() || nombre.length() > 100) {
            Mensajes.error(this, "El nombre debe tener entre 1 y 100 caracteres.");
            return;
        }
        Repartidor repartidor = new Repartidor(idSeleccionado, nombre);
        try {
            if (editar) dao.update(repartidor);
            else dao.create(repartidor);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, editar ? "Repartidor actualizado." : "Repartidor registrado. ID: " + repartidor.getId());
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona el repartidor que quieres eliminar.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar al repartidor #" + idSeleccionado + "?")) return;
        try {
            dao.delete(idSeleccionado);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, "Repartidor eliminado.");
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }
}
