package vista;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

public class Mensajes {

    public static void informar(Component ventana, String texto) {
        JOptionPane.showMessageDialog(ventana, texto, "SpeedFast", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component ventana, String texto) {
        JOptionPane.showMessageDialog(ventana, texto, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component ventana, String texto) {
        return JOptionPane.showConfirmDialog(ventana, texto, "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // Traduce los errores habituales de MySQL a mensajes comprensibles.
    public static void errorSQL(Component ventana, SQLException e) {
        String texto;
        switch (e.getErrorCode()) {
            case 1451: texto = "Este registro tiene entregas asociadas. Elimina primero esas entregas."; break;
            case 1452: texto = "El pedido o repartidor ya no existe. Actualiza los datos y vuelve a seleccionarlo."; break;
            case 1146: texto = "Faltan las tablas de la semana 8. Ejecuta el script SQL indicado en el README."; break;
            case 1045: texto = "No se pudo ingresar a MySQL. Revisa el usuario y SPEEDFAST_DB_PASSWORD."; break;
            default:
                if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
                    texto = "No se pudo conectar a MySQL. Comprueba que el servicio esté iniciado.";
                } else {
                    texto = "No se pudo completar la operación: " + e.getMessage();
                }
        }
        JOptionPane.showMessageDialog(ventana, texto, "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
