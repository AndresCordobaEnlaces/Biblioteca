package config;

import exceptions.BDException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigMySQL {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URLBD = "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    /**
     * Abre una conexion con la base de datos MySQL.
     *
     * @author Dan Bolocan
     */
    public static Connection abrirConexion() throws BDException {
        Connection conexion = null;

        try {
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URLBD, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new BDException(BDException.ERROR_CARGAR_DRIVER + e.getMessage());
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
        }

        return conexion;
    }

    /**
     * Cierra una conexion abierta.
     *
     * @author Dan Bolocan
     */
    public static void cerrarConexion(Connection conexion) throws BDException {
        try {
            conexion.close();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_CERRAR_CONEXION + e.getMessage());
        }
    }
}
