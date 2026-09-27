package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private VentanaRegistroPedido ventanaRegistro;
    private VentanaRegistroRepartidor ventanaRepartidor;
    private VentanaListaPedidos ventanaLista;
    private VentanaEntrega ventanaEntrega;

    public VentanaPrincipal() {

        setTitle("SpeedFast - Gestión de entregas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 400);
        setMinimumSize(new Dimension(500, 360));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel titulo = new JLabel(
                "SpeedFast", SwingConstants.CENTER
        );

        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton registrar = new JButton("Registrar pedido");
        JButton registrarRepartidor = new JButton("Registrar repartidor");
        JButton listar = new JButton("Listar pedidos");
        JButton entregas = new JButton("Asignar repartidor / Gestionar entregas");

        botones.add(registrar);
        botones.add(registrarRepartidor);
        botones.add(listar);
        botones.add(entregas);

        panel.add(botones, BorderLayout.CENTER);

        panel.add(
                new JLabel(
                        "Datos guardados en MySQL.",
                        SwingConstants.CENTER
                ),
                BorderLayout.SOUTH
        );

        setContentPane(panel);

        registrar.addActionListener(e -> abrirRegistro());
        registrarRepartidor.addActionListener(e -> abrirRepartidor());
        listar.addActionListener(e -> abrirLista());
        entregas.addActionListener(e -> abrirEntregas());
    }

    private void abrirRegistro() {

        if (ventanaRegistro == null || !ventanaRegistro.isDisplayable()) {
            ventanaRegistro = new VentanaRegistroPedido();
        }

        ventanaRegistro.setVisible(true);
        ventanaRegistro.toFront();
    }

    private void abrirRepartidor() {

        if (ventanaRepartidor == null
                || !ventanaRepartidor.isDisplayable()) {

            ventanaRepartidor = new VentanaRegistroRepartidor();
        }

        ventanaRepartidor.setVisible(true);
        ventanaRepartidor.toFront();
    }

    private void abrirLista() {

        if (ventanaLista == null || !ventanaLista.isDisplayable()) {
            ventanaLista = new VentanaListaPedidos();
        }

        ventanaLista.refrescarTabla();
        ventanaLista.setVisible(true);
        ventanaLista.toFront();
    }

    private void abrirEntregas() {

        if (ventanaEntrega == null || !ventanaEntrega.isDisplayable()) {
            ventanaEntrega = new VentanaEntrega();
        }

        ventanaEntrega.cargarDatos();
        ventanaEntrega.setVisible(true);
        ventanaEntrega.toFront();
    }
}
