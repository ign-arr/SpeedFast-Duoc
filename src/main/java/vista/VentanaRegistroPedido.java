package vista;

import dao.PedidoDAO;
import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaRegistroPedido extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final JTextField campoDireccion = new JTextField();
    private final JTextField campoDistancia = new JTextField();

    private final JComboBox<String> comboTipo = new JComboBox<>(
            new String[]{"Comida", "Encomienda", "Express"}
    );

    public VentanaRegistroPedido() {

        setTitle("SpeedFast - Registrar pedido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(510, 300);
        setMinimumSize(new Dimension(460, 280));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        panel.add(
                new JLabel("El ID del pedido se genera automáticamente."),
                BorderLayout.NORTH
        );

        JPanel formulario = new JPanel(
                new GridLayout(3, 2, 10, 12)
        );

        formulario.add(new JLabel("Dirección:"));
        formulario.add(campoDireccion);

        formulario.add(new JLabel("Tipo:"));
        formulario.add(comboTipo);

        formulario.add(new JLabel("Distancia (km enteros):"));
        formulario.add(campoDistancia);

        panel.add(formulario, BorderLayout.CENTER);

        JButton guardar = new JButton("Guardar");
        JButton cerrar = new JButton("Cerrar");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.add(guardar);
        botones.add(cerrar);

        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        getRootPane().setDefaultButton(guardar);

        guardar.addActionListener(e -> guardarPedido());
        cerrar.addActionListener(e -> dispose());
    }

    private void guardarPedido() {

        String direccion = campoDireccion.getText().trim();
        String textoDistancia = campoDistancia.getText().trim();

        if (direccion.isEmpty() || textoDistancia.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        if (direccion.length() > 150) {
            mostrarError("La dirección admite hasta 150 caracteres.");
            return;
        }

        try {
            int distancia = Integer.parseInt(textoDistancia);

            if (distancia <= 0) {
                mostrarError("La distancia debe ser mayor que cero.");
                return;
            }

            Pedido pedido;

            switch (comboTipo.getSelectedIndex()) {
                case 0:
                    pedido = new PedidoComida(0, direccion, distancia);
                    break;

                case 1:
                    pedido = new PedidoEncomienda(0, direccion, distancia);
                    break;

                default:
                    pedido = new PedidoExpress(0, direccion, distancia);
            }

            pedidoDAO.guardar(pedido);

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido registrado correctamente.\n"
                            + "ID: " + pedido.getIdPedido()
            );

            campoDireccion.setText("");
            campoDistancia.setText("");
            comboTipo.setSelectedIndex(0);
            campoDireccion.requestFocusInWindow();

        } catch (NumberFormatException e) {
            mostrarError("Ingresa una distancia válida en números enteros.");

        } catch (SQLException e) {
            mostrarError("No se pudo guardar el pedido: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Revisa los datos",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
