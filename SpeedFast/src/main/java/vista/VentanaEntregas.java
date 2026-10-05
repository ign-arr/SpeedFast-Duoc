package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.Repartidor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VentanaEntregas extends JFrame {

    private final EntregaDAO dao = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final Runnable alCambiar;
    private final JLabel etiquetaId = new JLabel("ID: automático");
    private final JComboBox<String> comboPedidos = new JComboBox<>();
    private final JComboBox<String> comboRepartidores = new JComboBox<>();
    private final JTextField campoFecha = new JTextField();
    private final JTextField campoHora = new JTextField();
    private final JComboBox<String> filtroPedido = new JComboBox<>();
    private final JComboBox<String> filtroRepartidor = new JComboBox<>();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Repartidor> repartidores = new ArrayList<>();
    private List<Entrega> entregas = new ArrayList<>();
    private final List<Entrega> visibles = new ArrayList<>();
    private int idSeleccionado;
    private boolean actualizando;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public VentanaEntregas(Runnable alCambiar) {
        this.alCambiar = alCambiar;
        setTitle("SpeedFast - Entregas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(840, 590));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel superior = new JPanel(new BorderLayout(10, 10));
        superior.add(etiquetaId, BorderLayout.NORTH);
        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 8));
        formulario.add(new JLabel("Pedido:")); formulario.add(comboPedidos);
        formulario.add(new JLabel("Repartidor:")); formulario.add(comboRepartidores);
        formulario.add(new JLabel("Fecha (AAAA-MM-DD):")); formulario.add(campoFecha);
        formulario.add(new JLabel("Hora (HH:mm:ss, de 00 a 23):")); formulario.add(campoHora);
        superior.add(formulario, BorderLayout.CENTER);
        JPanel filtros = new JPanel(new GridLayout(2, 2, 10, 8));
        filtros.add(new JLabel("Filtrar por pedido:")); filtros.add(filtroPedido);
        filtros.add(new JLabel("Filtrar por repartidor:")); filtros.add(filtroRepartidor);
        superior.add(filtros, BorderLayout.SOUTH);
        panel.add(superior, BorderLayout.NORTH);

        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(320);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton nuevo = new JButton("Nuevo");
        JButton registrar = new JButton("Registrar");
        JButton editar = new JButton("Guardar cambios");
        JButton eliminar = new JButton("Eliminar");
        JButton iniciar = new JButton("Iniciar reparto");
        JButton finalizar = new JButton("Finalizar pedido");
        JButton actualizar = new JButton("Actualizar");
        JButton cerrar = new JButton("Cerrar");
        JPanel botones = new JPanel(new GridLayout(2, 4, 8, 8));
        for (JButton boton : new JButton[]{nuevo, registrar, editar, eliminar, iniciar, finalizar, actualizar, cerrar}) botones.add(boton);
        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        nuevo.addActionListener(e -> limpiar());
        registrar.addActionListener(e -> guardar(false));
        editar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> eliminar());
        iniciar.addActionListener(e -> cambiarEstado(false));
        finalizar.addActionListener(e -> cambiarEstado(true));
        actualizar.addActionListener(e -> actualizarDatos());
        cerrar.addActionListener(e -> dispose());
        filtroPedido.addActionListener(e -> { if (!actualizando) mostrarTabla(); });
        filtroRepartidor.addActionListener(e -> { if (!actualizando) mostrarTabla(); });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizando) cargarSeleccion();
        });
        limpiar();
    }

    // Actualiza los combos conservando los IDs seleccionados.
    public void actualizarDatos() {
        int pedidoElegido = idPedido(comboPedidos.getSelectedIndex());
        int repartidorElegido = idRepartidor(comboRepartidores.getSelectedIndex());
        int pedidoFiltrado = idPedido(filtroPedido.getSelectedIndex() - 1);
        int repartidorFiltrado = idRepartidor(filtroRepartidor.getSelectedIndex() - 1);
        try {
            List<Pedido> nuevosPedidos = pedidoDAO.readAll();
            List<Repartidor> nuevosRepartidores = repartidorDAO.readAll();
            List<Entrega> nuevasEntregas = dao.readAll();
            actualizando = true;
            pedidos = nuevosPedidos;
            repartidores = nuevosRepartidores;
            entregas = nuevasEntregas;
            comboPedidos.removeAllItems(); comboRepartidores.removeAllItems();
            filtroPedido.removeAllItems(); filtroRepartidor.removeAllItems();
            filtroPedido.addItem("Todos"); filtroRepartidor.addItem("Todos");
            for (Pedido pedido : pedidos) {
                String texto = pedido.getIdPedido() + " - " + pedido.getDireccionEntrega() + " [" + pedido.getEstado() + "]";
                comboPedidos.addItem(texto);
                filtroPedido.addItem(texto);
            }
            for (Repartidor repartidor : repartidores) {
                String texto = repartidor.getId() + " - " + repartidor.getNombre();
                comboRepartidores.addItem(texto);
                filtroRepartidor.addItem(texto);
            }
            comboPedidos.setSelectedIndex(indicePedido(pedidoElegido));
            comboRepartidores.setSelectedIndex(indiceRepartidor(repartidorElegido));
            filtroPedido.setSelectedIndex(indicePedido(pedidoFiltrado) + 1);
            filtroRepartidor.setSelectedIndex(indiceRepartidor(repartidorFiltrado) + 1);
            mostrarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        } finally {
            actualizando = false;
        }
    }

    private void mostrarTabla() {
        actualizando = true;
        modeloTabla.setRowCount(0);
        visibles.clear();
        int pedidoFiltrado = idPedido(filtroPedido.getSelectedIndex() - 1);
        int repartidorFiltrado = idRepartidor(filtroRepartidor.getSelectedIndex() - 1);
        int seleccion = -1;
        for (Entrega entrega : entregas) {
            if ((pedidoFiltrado == 0 || entrega.getIdPedido() == pedidoFiltrado)
                    && (repartidorFiltrado == 0 || entrega.getIdRepartidor() == repartidorFiltrado)) {
                visibles.add(entrega);
                int p = indicePedido(entrega.getIdPedido());
                int r = indiceRepartidor(entrega.getIdRepartidor());
                String textoPedido = p >= 0 ? comboPedidos.getItemAt(p) : String.valueOf(entrega.getIdPedido());
                String textoRepartidor = r >= 0 ? comboRepartidores.getItemAt(r) : String.valueOf(entrega.getIdRepartidor());
                modeloTabla.addRow(new Object[]{entrega.getId(), textoPedido, textoRepartidor,
                        entrega.getFecha(), entrega.getHora().format(FORMATO_HORA)});
                if (entrega.getId() == idSeleccionado) seleccion = visibles.size() - 1;
            }
        }
        if (seleccion >= 0) tabla.setRowSelectionInterval(seleccion, seleccion);
        else if (idSeleccionado != 0) limpiar();
        actualizando = false;
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Entrega entrega = visibles.get(fila);
        idSeleccionado = entrega.getId();
        etiquetaId.setText("ID: " + idSeleccionado);
        comboPedidos.setSelectedIndex(indicePedido(entrega.getIdPedido()));
        comboRepartidores.setSelectedIndex(indiceRepartidor(entrega.getIdRepartidor()));
        campoFecha.setText(entrega.getFecha().toString());
        campoHora.setText(entrega.getHora().format(FORMATO_HORA));
    }

    private void limpiar() {
        idSeleccionado = 0;
        tabla.clearSelection();
        etiquetaId.setText("ID: automático");
        comboPedidos.setSelectedIndex(-1);
        comboRepartidores.setSelectedIndex(-1);
        campoFecha.setText(LocalDate.now().toString());
        campoHora.setText(LocalTime.now().format(FORMATO_HORA));
    }

    private void guardar(boolean editar) {
        if (editar && idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona la entrega que quieres editar."); return;
        }
        if (!editar && idSeleccionado != 0) {
            Mensajes.error(this, "Pulsa Nuevo para registrar otra entrega."); return;
        }
        int idPedido = idPedido(comboPedidos.getSelectedIndex());
        int idRepartidor = idRepartidor(comboRepartidores.getSelectedIndex());
        if (idPedido == 0 || idRepartidor == 0) {
            Mensajes.error(this, "Selecciona un pedido y un repartidor."); return;
        }
        String textoFecha = campoFecha.getText().trim();
        String textoHora = campoHora.getText().trim();
        LocalDate fecha;
        LocalTime hora;
        try {
            if (!textoFecha.matches("\\d{4}-\\d{2}-\\d{2}") || !textoHora.matches("\\d{2}:\\d{2}:\\d{2}")) {
                Mensajes.error(this, "Usa fecha AAAA-MM-DD y hora HH:mm:ss. Ejemplo: 2026-10-04 y 16:30:00."); return;
            }
            fecha = LocalDate.parse(textoFecha);
            hora = LocalTime.parse(textoHora);
            if (fecha.getYear() < 1000) {
                Mensajes.error(this, "La fecha debe estar entre los años 1000 y 9999."); return;
            }
        } catch (DateTimeParseException e) {
            Mensajes.error(this, "La fecha o la hora no es válida. Revisa el día, mes y hora."); return;
        }
        Entrega entrega = new Entrega(idPedido, idRepartidor, fecha, hora);
        entrega.setId(idSeleccionado);
        try {
            if (editar) dao.update(entrega);
            else dao.create(entrega);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, editar ? "Entrega actualizada." : "Entrega registrada. ID: " + entrega.getId());
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.error(this, "Selecciona la entrega que quieres eliminar."); return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar la entrega #" + idSeleccionado + "?")) return;
        try {
            dao.delete(idSeleccionado);
            limpiar();
            alCambiar.run();
            Mensajes.informar(this, "Entrega eliminada.");
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    // El estado pertenece al pedido; se cambia mediante acciones explícitas.
    private void cambiarEstado(boolean finalizar) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.error(this, "Selecciona una entrega de la tabla."); return;
        }
        int idPedido = visibles.get(fila).getIdPedido();
        try {
            pedidoDAO.cambiarEstado(idPedido,
                    finalizar ? EstadoPedido.EN_REPARTO : EstadoPedido.PENDIENTE,
                    finalizar ? EstadoPedido.ENTREGADO : EstadoPedido.EN_REPARTO);
            alCambiar.run();
            Mensajes.informar(this, finalizar ? "Pedido entregado." : "Pedido en reparto.");
        } catch (SQLException e) {
            Mensajes.errorSQL(this, e);
        }
    }

    private int idPedido(int indice) {
        return indice >= 0 && indice < pedidos.size() ? pedidos.get(indice).getIdPedido() : 0;
    }

    private int idRepartidor(int indice) {
        return indice >= 0 && indice < repartidores.size() ? repartidores.get(indice).getId() : 0;
    }

    private int indicePedido(int id) {
        for (int i = 0; i < pedidos.size(); i++) if (pedidos.get(i).getIdPedido() == id) return i;
        return -1;
    }

    private int indiceRepartidor(int id) {
        for (int i = 0; i < repartidores.size(); i++) if (repartidores.get(i).getId() == id) return i;
        return -1;
    }
}
