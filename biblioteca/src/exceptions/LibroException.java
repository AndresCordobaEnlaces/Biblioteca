package exceptions;

public class LibroException extends Exception {

    public static final String ERROR_ABRIR_CONEXION = "Error al abrir conexion ";
    public static final String ERROR_QUERY = "Error en la consulta ";
    public static final String ERROR_CERRAR_CONEXION = "Error al cerrar conexion ";
    public static final String ERROR_CARGAR_DRIVER = "Error al cargar driver";
    public static final String ERROR_CANCELAR_SETCONTRASENA = "Se cancelo el inicio de sesion";
    public static final String ERROR_NO_EXISTE = "No existe la tupla buscada";
    public static final String ERROR_NOLIBRO = "No existe ningun libro con ese codigo en la base de datos.";
    public static final String ERROR_LIBRO_ISBNEXISTE = "Ya existe un libro con ese ISBN en la base de datos.";
    public static final String ERROR_LIBRO_BDEmpty = "No se ha encontrado ningun libro en la base de datos.";
    public static final String ERROR_LIBRO_PRESTAMO = "El libro esta referenciado en un prestamo de la base de datos.";
    public static final String ERROR_LIBRO_NOESCRITOR = "No existe ningun libro con ese escritor en la base de datos.";
    public static final String ERROR_LIBRO_NOPRESTADO = "No existe ningun libro no prestado en la base de datos.";
    public static final String ERROR_LIBRO_NODEVUELTO =
            "No existe ningun libro devuelto en esa fecha en la base de datos.";

    /**
     * Crea una excepcion de libro con mensaje personalizado.
     *
     * @author Dan Bolocan
     */
    public LibroException(String mensaje) {
        super("Error: " + mensaje);
    }
}
