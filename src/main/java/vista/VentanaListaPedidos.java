package vista;

import dao.PedidoDAO;
import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaListaPedidos extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final DefaultTableModel modeloTabla;
    private final JLabel resumen = new JLabel("Pedidos registrados");

    public VentanaListaPedidos() {

        setTitle("SpeedFast - Listado de pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 420);
        setMinimumSize(new Dimension(700, 320));
        setLocationRelativeTo(null);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Dirección", "Tipo", "Km", "Estado"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        panel.add(resumen, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton refrescar = new JButton("Refrescar");
        JButton cerrar = new JButton("Cerrar");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.add(refrescar);
        botones.add(cerrar);

        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        refrescar.addActionListener(e -> refrescarTabla());
        cerrar.addActionListener(e -> dispose());
    }

    public void refrescarTabla() {

        try {
            List<Pedido> pedidos = pedidoDAO.listarTodos();

            modeloTabla.setRowCount(0);

            for (Pedido pedido : pedidos) {

                String tipo;

                if (pedido instanceof PedidoComida) {
                    tipo = "Comida";
                } else if (pedido instanceof PedidoEncomienda) {
                    tipo = "Encomienda";
                } else {
                    tipo = "Express";
                }

                modeloTabla.addRow(new Object[]{
                        pedido.getIdPedido(),
                        pedido.getDireccionEntrega(),
                        tipo,
                        pedido.getDistanciaKm(),
                        pedido.getEstado()
                });
            }

            resumen.setText(
                    "Pedidos registrados: " + pedidos.size()
            );

        } catch (SQLException e) {

            resumen.setText("No se pudo actualizar el listado");

            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar los pedidos: " + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
