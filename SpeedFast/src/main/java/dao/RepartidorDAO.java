package dao;

import modelo.Repartidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    // Guarda el registro y recupera el ID generado por MySQL.
    public void create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, repartidor.getNombre());
            sentencia.executeUpdate();
            try (ResultSet resultado = sentencia.getGeneratedKeys()) {
                if (resultado.next()) {
                    repartidor.setId(resultado.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del repartidor.");
                }
            }
        }
    }

    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                repartidores.add(new Repartidor(resultado.getInt("id"), resultado.getString("nombre")));
            }
        }
        return repartidores;
    }

    public void update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, repartidor.getNombre());
            sentencia.setInt(2, repartidor.getId());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("El repartidor ya no existe. Actualiza la tabla.");
            }
        }
    }

    // Las claves foráneas impiden borrar un repartidor con entregas.
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("El repartidor ya no existe. Actualiza la tabla.");
            }
        }
    }
}
