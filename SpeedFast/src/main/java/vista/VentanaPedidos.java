package vista;

import dao.PedidoDAO;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentanaPedidos extends JFrame {

    private final PedidoDAO dao = new PedidoDAO();
    private final Runnable alCambiar;
    private final JLabel etiquetaId = new JLabel("ID: automático");
    private final JTextField campoDireccion = new JTextField();
    private final JTextField campoDistancia = new JTextField("0");
    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{"COMIDA", "ENCOMIENDA", "EXPRESS"});
    private final JComboBox<EstadoPedido> comboEstado = new JComboBox<>(EstadoPedido.values());
    private final JComboBox<String> filtroTipo = new JComboBox<>(new String[]{"TODOS", "COMIDA", "ENCOMIENDA", "EXPRESS"});
    private final JComboBox<String> filtroEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
    private final DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Km", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private List<Pedido> pedidos = new ArrayList<>();
    private final List<Pedido> visibles = new ArrayList<>();
    private int idSeleccionado;
    private boolean actualizando;

    public VentanaPedidos(Runnable alCambiar) {
        this.alCambiar = alCambiar;
        setTitle("SpeedFast - Pedidos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(760, 540));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel superior = new JPanel(new BorderLayout(10, 10));
        superior.add(etiquetaId, BorderLayout.NORTH);
        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 8));
        formulario.add(new JLabel("Dirección:")); formulario.add(campoDireccion);
        formulario.add(new JLabel("Tipo:")); formulario.add(comboTipo);
        formulario.add(new JLabel("Estado:")); formulario.add(comboEstado);
        formulario.add(new JLabel("Distancia (km enteros, desde 0):")); formulario.add(campoDistancia);
        superior.add(formulario, BorderLayout.CENTER);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Filtrar tipo:")); filtros.add(filtroTipo);
        filtros.add(new JLabel("Estado:")); filtros.add(filtroEstado);
        superior.add(filtros, BorderLayout.SOUTH);
        panel.add(superior, BorderLayout.NORTH);

        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton nuevo = new JButton("Nuevo");
        JButton registrar = new JButton("Registrar");
        JButton editar = new JButton("Guardar cambios");
        JButton eliminar = new JButton("Eliminar");
        JButton actualizar = new JButton("Actualizar");
        JButton cerrar = new JButton("Cerrar");
        JPanel botones = new JPanel(new GridLayout(2, 3, 8, 8));
        for (JButton boton : new JButton[]{nuevo, registrar, editar, eliminar, actualizar, cerrar}) botones.add(boton);
        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        nuevo.addActionListener(e -> limpiar());
        registrar.addActionListener(e -> guardar(false));
        editar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> eliminar());
        actualizar.addActionListener(e -> actualizarDatos());
        cerrar.addActionListener(e -> dispose());
        filtroTipo.addActionListener(e -> mostrarTabla());
        filtroEstado.addActionListener(e -> mostrarTabla());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizando) cargarSeleccion();
        });
    }

    public void actualizarDatos() {
        try {
            pedidos = dao.readAll();
            mostrarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    // Los filtros se aplican al listado leído desde la base de datos.
    private void mostrarTabla() {
        actualizando = true;
        modeloTabla.setRowCount(0);
        visibles.clear();
        int seleccion = -1;
        for (Pedido pedido : pedidos) {
            boolean tipoValido = filtroTipo.getSelectedIndex() == 0 || pedido.getTipo().equals(filtroTipo.getSelectedItem());
            boolean estadoValido = filtroEstado.getSelectedIndex() == 0 || pedido.getEstado().name().equals(filtroEstado.getSelectedItem());
            if (tipoValido && estadoValido) {
                visibles.add(pedido);
                modeloTabla.addRow(new Object[]{pedido.getIdPedido(), pedido.getDireccionEntrega(), pedido.getTipo(),
                        pedido.getDistanciaKm(), pedido.getEstado()});
                if (pedido.getIdPedido() == idSeleccionado) seleccion = visibles.size() - 1;
            }
        }
        if (seleccion >= 0) tabla.setRowSelectionInterval(seleccion, seleccion);
        else if (idSeleccionado != 0) limpiar();
        actualizando = false;
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Pedido pedido = visibles.get(fila);
        idSeleccionado = pedido.getIdPedido();
        etiquetaId.setText("ID: " + idSeleccionado);
        campoDireccion.setText(pedido.getDireccionEntrega());
        campoDistancia.setText(String.valueOf(pedido.getDistanciaKm()));
        comboTipo.setSelectedItem(pedido.getTipo());
        comboEstado.setSelectedItem(pedido.getEstado());
    }

    private void limpiar() {
        idSeleccionado = 0;
        tabla.clearSelection();
        etiquetaId.setText("ID: automático");
        campoDireccion.setText("");
        campoDistancia.setText("0");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedItem(EstadoPedido.PENDIENTE);
    }

    private void guardar(boolean editar) {
        if (editar && idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona el pedido que quieres editar."); return;
        }
        if (!editar && idSeleccionado != 0) {
            Mensajes.error(this, "Pulsa Nuevo para registrar otro pedido."); return;
        }
        String direccion = campoDireccion.getText().trim();
        if (direccion.isEmpty() || direccion.length() > 100) {
            Mensajes.error(this, "La dirección debe tener entre 1 y 100 caracteres."); return;
        }
        int distancia;
        try {
            distancia = Integer.parseInt(campoDistancia.getText().trim());
            if (distancia < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Mensajes.error(this, "Ingresa una distancia entera mayor o igual a cero."); return;
        }
        Pedido pedido;
        switch (comboTipo.getSelectedIndex()) {
            case 0: pedido = new PedidoComida(idSeleccionado, direccion, distancia); break;
            case 1: pedido = new PedidoEncomienda(idSeleccionado, direccion, distancia); break;
            default: pedido = new PedidoExpress(idSeleccionado, direccion, distancia);
        }
        pedido.setEstado(((EstadoPedido) comboEstado.getSelectedItem()).name());
        try {
            if (editar) dao.update(pedido);
            else dao.create(pedido);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, editar ? "Pedido actualizado." : "Pedido registrado. ID: " + pedido.getIdPedido());
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona el pedido que quieres eliminar."); return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el pedido #" + idSeleccionado + "?")) return;
        try {
            dao.delete(idSeleccionado);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, "Pedido eliminado.");
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }
}
