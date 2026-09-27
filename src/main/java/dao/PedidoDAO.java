package dao;

import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void guardar(Pedido pedido) throws SQLException {

        String tipo;

        if (pedido instanceof PedidoComida) {
            tipo = "COMIDA";
        } else if (pedido instanceof PedidoEncomienda) {
            tipo = "ENCOMIENDA";
        } else if (pedido instanceof PedidoExpress) {
            tipo = "EXPRESS";
        } else {
            throw new IllegalArgumentException(
                    "Tipo de pedido no válido."
            );
        }

        String sql = "INSERT INTO pedido "
                + "(direccion, tipo, estado, distancia_km) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, pedido.getDireccionEntrega());
            sentencia.setString(2, tipo);
            sentencia.setString(3, pedido.getEstado().name());
            sentencia.setInt(4, pedido.getDistanciaKm());

            sentencia.executeUpdate();

            try (ResultSet resultado = sentencia.getGeneratedKeys()) {

                if (resultado.next()) {
                    pedido.setIdPedido(resultado.getInt(1));
                }
            }
        }
    }

    public List<Pedido> listarTodos() throws SQLException {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT id, direccion, tipo, estado, distancia_km "
                + "FROM pedido ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String direccion = resultado.getString("direccion");
                String tipo = resultado.getString("tipo");
                int distancia = resultado.getInt("distancia_km");

                Pedido pedido;

                switch (tipo) {
                    case "COMIDA":
                        pedido = new PedidoComida(
                                id, direccion, distancia
                        );
                        break;

                    case "ENCOMIENDA":
                        pedido = new PedidoEncomienda(
                                id, direccion, distancia
                        );
                        break;

                    case "EXPRESS":
                        pedido = new PedidoExpress(
                                id, direccion, distancia
                        );
                        break;

                    default:
                        throw new SQLException(
                                "Tipo de pedido desconocido: " + tipo
                        );
                }

                pedido.setEstado(resultado.getString("estado"));
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }
}