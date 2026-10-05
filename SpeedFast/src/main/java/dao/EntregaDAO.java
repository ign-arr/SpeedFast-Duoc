package dao;

import modelo.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    public void create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            completarDatos(sentencia, entrega);
            sentencia.executeUpdate();
            try (ResultSet resultado = sentencia.getGeneratedKeys()) {
                if (resultado.next()) {
                    entrega.setId(resultado.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID de la entrega.");
                }
            }
        }
    }

    public List<Entrega> readAll() throws SQLException {
        List<Entrega> entregas = new ArrayList<>();
        String sql = "SELECT id, id_pedido, id_repartidor, fecha, hora FROM entregas ORDER BY id";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                Entrega entrega = new Entrega(resultado.getInt("id_pedido"), resultado.getInt("id_repartidor"),
                        resultado.getDate("fecha").toLocalDate(), resultado.getTime("hora").toLocalTime());
                entrega.setId(resultado.getInt("id"));
                entregas.add(entrega);
            }
        }
        return entregas;
    }

    public void update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            completarDatos(sentencia, entrega);
            sentencia.setInt(5, entrega.getId());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("La entrega ya no existe. Actualiza la tabla.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("La entrega ya no existe. Actualiza la tabla.");
            }
        }
    }

    private void completarDatos(PreparedStatement sentencia, Entrega entrega) throws SQLException {
        sentencia.setInt(1, entrega.getIdPedido());
        sentencia.setInt(2, entrega.getIdRepartidor());
        sentencia.setDate(3, Date.valueOf(entrega.getFecha()));
        sentencia.setTime(4, Time.valueOf(entrega.getHora()));
    }
}
