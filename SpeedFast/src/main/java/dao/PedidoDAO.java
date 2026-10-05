package dao;

import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado, distancia_km) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            completarDatos(sentencia, pedido);
            sentencia.executeUpdate();
            try (ResultSet resultado = sentencia.getGeneratedKeys()) {
                if (resultado.next()) {
                    pedido.setIdPedido(resultado.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del pedido.");
                }
            }
        }
    }

    // Crea la subclase correspondiente al tipo guardado.
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id, direccion, tipo, estado, distancia_km FROM pedidos ORDER BY id";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                int id = resultado.getInt("id");
                String direccion = resultado.getString("direccion");
                int distancia = resultado.getInt("distancia_km");
                Pedido pedido;
                switch (resultado.getString("tipo")) {
                    case "COMIDA": pedido = new PedidoComida(id, direccion, distancia); break;
                    case "ENCOMIENDA": pedido = new PedidoEncomienda(id, direccion, distancia); break;
                    case "EXPRESS": pedido = new PedidoExpress(id, direccion, distancia); break;
                    default: throw new SQLException("El pedido tiene un tipo desconocido.");
                }
                try {
                    pedido.setEstado(resultado.getString("estado"));
                } catch (IllegalArgumentException | NullPointerException e) {
                    throw new SQLException("El pedido tiene un estado inválido.", e);
                }
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    public void update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ?, distancia_km = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            completarDatos(sentencia, pedido);
            sentencia.setInt(5, pedido.getIdPedido());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("El pedido ya no existe. Actualiza la tabla.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("El pedido ya no existe. Actualiza la tabla.");
            }
        }
    }

    // Cambia el estado solamente si el pedido está en el estado esperado.
    public void cambiarEstado(int id, EstadoPedido anterior, EstadoPedido nuevo) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ? AND estado = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nuevo.name());
            sentencia.setInt(2, id);
            sentencia.setString(3, anterior.name());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("El pedido no existe o ya no está " + anterior + ". Actualiza los datos.");
            }
        }
    }

    private void completarDatos(PreparedStatement sentencia, Pedido pedido) throws SQLException {
        sentencia.setString(1, pedido.getDireccionEntrega());
        sentencia.setString(2, pedido.getTipo());
        sentencia.setString(3, pedido.getEstado().name());
        sentencia.setInt(4, pedido.getDistanciaKm());
    }
}
