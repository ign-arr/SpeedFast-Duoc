package dao;

import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    public void guardar(Repartidor repartidor) throws SQLException {

        String sql = "INSERT INTO repartidor (nombre) VALUES (?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, repartidor.getNombre());
            sentencia.executeUpdate();

            try (ResultSet resultado = sentencia.getGeneratedKeys()) {

                if (resultado.next()) {
                    repartidor.setId(resultado.getInt(1));
                }
            }
        }
    }

    public List<Repartidor> listarTodos() throws SQLException {

        List<Repartidor> repartidores = new ArrayList<>();

        String sql = "SELECT id, nombre FROM repartidor ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                Repartidor repartidor = new Repartidor(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                );

                repartidores.add(repartidor);
            }
        }

        return repartidores;
    }
}