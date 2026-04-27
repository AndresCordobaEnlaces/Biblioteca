package exceptions;

public class BDException extends Exception {

    public static final String ERROR_ABRIR_CONEXION = "Error al abrir conexion ";
    public static final String ERROR_QUERY = "Error en la consulta ";
    public static final String ERROR_CERRAR_CONEXION = "Error al cerrar conexion ";
    public static final String ERROR_CARGAR_DRIVER = "Error al cargar driver";
    public static final String ERROR_CANCELAR_SETCONTRASENA = "Se cancelo el inicio de sesion";
    public static final String ERROR_NO_EXISTE = "No existe la tupla buscada";

    /**
     * Crea una excepcion de base de datos con mensaje personalizado.
     *
     * @author Dan Bolocan
     */
    public BDException(String mensaje) {
        super("Error: " + mensaje);
    }
}
