package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaRegistroRepartidor extends JFrame {

    private final JTextField campoNombre = new JTextField();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    public VentanaRegistroRepartidor() {

        setTitle("SpeedFast - Registrar repartidor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 220);
        setMinimumSize(new Dimension(420, 200));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        panel.add(
                new JLabel("El ID se genera automáticamente."),
                BorderLayout.NORTH
        );

        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        formulario.add(campoNombre, BorderLayout.CENTER);

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

        guardar.addActionListener(e -> guardarRepartidor());
        cerrar.addActionListener(e -> dispose());
    }

    private void guardarRepartidor() {

        String nombre = campoNombre.getText().trim();

        if (nombre.isEmpty() || nombre.length() > 100) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa un nombre de entre 1 y 100 caracteres."
            );
            return;
        }

        Repartidor repartidor = new Repartidor(0, nombre);

        try {
            repartidorDAO.guardar(repartidor);

            JOptionPane.showMessageDialog(
                    this,
                    "Repartidor registrado correctamente.\n"
                            + "ID: " + repartidor.getId()
            );

            campoNombre.setText("");
            campoNombre.requestFocusInWindow();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el repartidor: " + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}