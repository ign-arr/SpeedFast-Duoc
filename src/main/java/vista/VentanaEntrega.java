package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;

import modelo.Entrega;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentanaEntrega extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    private final JComboBox<String> comboPedidos = new JComboBox<>();
    private final JComboBox<String> comboRepartidores = new JComboBox<>();

    private List<Pedido> pedidos = new ArrayList<>();
    private List<Repartidor> repartidores = new ArrayList<>();

    public VentanaEntrega() {

        setTitle("SpeedFast - Gestionar entregas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 280);
        setMinimumSize(new Dimension(650, 260));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        panel.add(
                new JLabel("Selecciona un pedido pendiente o en reparto."),
                BorderLayout.NORTH
        );

        JPanel formulario = new JPanel(
                new GridLayout(2, 2, 10, 12)
        );

        formulario.add(new JLabel("Pedido:"));
        formulario.add(comboPedidos);

        formulario.add(new JLabel("Repartidor para iniciar:"));
        formulario.add(comboRepartidores);

        panel.add(formulario, BorderLayout.CENTER);

        JButton iniciar = new JButton("Iniciar entrega");
        JButton finalizar = new JButton("Finalizar entrega");
        JButton actualizar = new JButton("Actualizar");
        JButton cerrar = new JButton("Cerrar");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.add(iniciar);
        botones.add(finalizar);
        botones.add(actualizar);
        botones.add(cerrar);

        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        iniciar.addActionListener(e -> iniciarEntrega());
        finalizar.addActionListener(e -> finalizarEntrega());
        actualizar.addActionListener(e -> cargarDatos());
        cerrar.addActionListener(e -> dispose());
    }

    public void cargarDatos() {

        try {
            List<Pedido> todos = pedidoDAO.listarTodos();

            List<Repartidor> registrados =
                    repartidorDAO.listarTodos();

            pedidos.clear();
            comboPedidos.removeAllItems();

            for (Pedido pedido : todos) {

                if (pedido.getEstado() != EstadoPedido.ENTREGADO) {

                    pedidos.add(pedido);

                    comboPedidos.addItem(
                            pedido.getIdPedido() + " - "
                                    + pedido.getDireccionEntrega()
                                    + " [" + pedido.getEstado() + "]"
                    );
                }
            }

            repartidores = registrados;
            comboRepartidores.removeAllItems();

            for (Repartidor repartidor : repartidores) {
                comboRepartidores.addItem(
                        repartidor.getId() + " - "
                                + repartidor.getNombre()
                );
            }

        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void iniciarEntrega() {

        int indicePedido = comboPedidos.getSelectedIndex();
        int indiceRepartidor = comboRepartidores.getSelectedIndex();

        if (indicePedido == -1 || indiceRepartidor == -1) {
            mostrarError("Selecciona un pedido y un repartidor.");
            return;
        }

        Pedido pedido = pedidos.get(indicePedido);
        Repartidor repartidor = repartidores.get(indiceRepartidor);

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            mostrarError("Ese pedido ya está en reparto.");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();

        Entrega entrega = new Entrega(
                pedido.getIdPedido(),
                repartidor.getId(),
                ahora.toLocalDate(),
                ahora.toLocalTime()
        );

        try {
            entregaDAO.guardar(entrega);

            JOptionPane.showMessageDialog(
                    this,
                    "Entrega iniciada.\n"
                            + "Pedido: " + pedido.getIdPedido() + "\n"
                            + "Repartidor: " + repartidor.getNombre()
            );

            cargarDatos();

        } catch (SQLException e) {
            mostrarError("No se pudo iniciar la entrega: " + e.getMessage());
        }
    }

    private void finalizarEntrega() {

        int indicePedido = comboPedidos.getSelectedIndex();

        if (indicePedido == -1) {
            mostrarError("Selecciona un pedido.");
            return;
        }

        Pedido pedido = pedidos.get(indicePedido);

        if (pedido.getEstado() != EstadoPedido.EN_REPARTO) {
            mostrarError("Primero debes iniciar la entrega del pedido.");
            return;
        }

        try {
            entregaDAO.finalizar(pedido.getIdPedido());

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido " + pedido.getIdPedido()
                            + " entregado correctamente."
            );

            cargarDatos();

        } catch (SQLException e) {
            mostrarError("No se pudo finalizar la entrega: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Gestión de entregas",
                JOptionPane.WARNING_MESSAGE
        );
    }
}