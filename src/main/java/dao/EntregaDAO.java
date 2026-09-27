package dao;

import modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class EntregaDAO {

    public void guardar(Entrega entrega) throws SQLException {

        String sqlEstado = "UPDATE pedido SET estado = ? "
                + "WHERE id = ? AND estado = ?";

        String sqlEntrega = "INSERT INTO entrega "
                + "(id_pedido, id_repartidor, fecha, hora) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(sqlEstado)) {

                    sentencia.setString(1, "EN_REPARTO");
                    sentencia.setInt(2, entrega.getIdPedido());
                    sentencia.setString(3, "PENDIENTE");

                    int filas = sentencia.executeUpdate();

                    if (filas == 0) {
                        throw new SQLException(
                                "El pedido no existe o ya no está pendiente."
                        );
                    }
                }

                int idGenerado;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(
                                     sqlEntrega,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    sentencia.setInt(1, entrega.getIdPedido());
                    sentencia.setInt(2, entrega.getIdRepartidor());

                    sentencia.setDate(
                            3, Date.valueOf(entrega.getFecha())
                    );

                    sentencia.setTime(
                            4, Time.valueOf(entrega.getHora())
                    );

                    sentencia.executeUpdate();

                    try (ResultSet resultado =
                                 sentencia.getGeneratedKeys()) {

                        if (resultado.next()) {
                            idGenerado = resultado.getInt(1);
                        } else {
                            throw new SQLException(
                                    "No se pudo obtener el ID de la entrega."
                            );
                        }
                    }
                }

                conexion.commit();
                entrega.setId(idGenerado);

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    public void finalizar(int idPedido) throws SQLException {

        String sql = "UPDATE pedido SET estado = ? "
                + "WHERE id = ? AND estado = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, "ENTREGADO");
            sentencia.setInt(2, idPedido);
            sentencia.setString(3, "EN_REPARTO");

            int filas = sentencia.executeUpdate();

            if (filas == 0) {
                throw new SQLException(
                        "El pedido no existe o no está en reparto."
                );
            }
        }
    }
}