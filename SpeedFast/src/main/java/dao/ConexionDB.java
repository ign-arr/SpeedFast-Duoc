package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USUARIO = "root";

    public static Connection conectar() throws SQLException {
        String clave = System.getenv("SPEEDFAST_DB_PASSWORD");
        if (clave == null || clave.isBlank()) {
            throw new SQLException("Configura SPEEDFAST_DB_PASSWORD en la ejecución de Main.");
        }
        return DriverManager.getConnection(URL, USUARIO, clave);
    }
}
