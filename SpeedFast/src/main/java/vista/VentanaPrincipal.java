package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private VentanaPedidos ventanaPedidos;
    private VentanaRepartidores ventanaRepartidores;
    private VentanaEntregas ventanaEntregas;

    public VentanaPrincipal() {
        setTitle("SpeedFast - Semana 8");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 350);
        setMinimumSize(new Dimension(420, 320));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel titulo = new JLabel("SpeedFast", SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        panel.add(titulo, BorderLayout.NORTH);

        JButton pedidos = new JButton("Gestionar pedidos");
        JButton repartidores = new JButton("Gestionar repartidores");
        JButton entregas = new JButton("Gestionar entregas");
        JPanel botones = new JPanel(new GridLayout(3, 1, 10, 10));
        botones.add(pedidos); botones.add(repartidores); botones.add(entregas);
        panel.add(botones, BorderLayout.CENTER);
        panel.add(new JLabel("Datos guardados en MySQL", SwingConstants.CENTER), BorderLayout.SOUTH);
        setContentPane(panel);

        pedidos.addActionListener(e -> abrirPedidos());
        repartidores.addActionListener(e -> abrirRepartidores());
        entregas.addActionListener(e -> abrirEntregas());
    }

    private void abrirPedidos() {
        if (ventanaPedidos == null || !ventanaPedidos.isDisplayable()) {
            ventanaPedidos = new VentanaPedidos(this::actualizarVentanas);
        }
        ventanaPedidos.setVisible(true);
        ventanaPedidos.actualizarDatos();
        ventanaPedidos.toFront();
    }

    private void abrirRepartidores() {
        if (ventanaRepartidores == null || !ventanaRepartidores.isDisplayable()) {
            ventanaRepartidores = new VentanaRepartidores(this::actualizarVentanas);
        }
        ventanaRepartidores.setVisible(true);
        ventanaRepartidores.actualizarDatos();
        ventanaRepartidores.toFront();
    }

    private void abrirEntregas() {
        if (ventanaEntregas == null || !ventanaEntregas.isDisplayable()) {
            ventanaEntregas = new VentanaEntregas(this::actualizarVentanas);
        }
        ventanaEntregas.setVisible(true);
        ventanaEntregas.actualizarDatos();
        ventanaEntregas.toFront();
    }

    // Refresca tablas y combos de las ventanas abiertas después de cada cambio.
    private void actualizarVentanas() {
        if (ventanaPedidos != null && ventanaPedidos.isDisplayable()) ventanaPedidos.actualizarDatos();
        if (ventanaRepartidores != null && ventanaRepartidores.isDisplayable()) ventanaRepartidores.actualizarDatos();
        if (ventanaEntregas != null && ventanaEntregas.isDisplayable()) ventanaEntregas.actualizarDatos();
    }
}
