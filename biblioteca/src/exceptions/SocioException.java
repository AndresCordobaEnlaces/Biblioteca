package exceptions;

public class SocioException extends Exception {

    public static final String ERROR_SOCIO_NOEXISTE = "No existe ningun socio con ese codigo.";
    public static final String ERROR_SOCIO_BDEmpty = "No hay socios en la base de datos.";
    public static final String ERROR_SOCIO_LOCALIDAD = "No existe ningun socio con esa localidad.";
    public static final String ERROR_SOCIO_SINPRESTAMOS = "No existe ningun socio sin prestamos.";
    public static final String ERROR_SOCIO_FECHA = "No existe ningun socio con prestamos en esa fecha.";
    public static final String ERROR_SOCIO_PRESTAMO = "El socio esta referenciado en un prestamo.";
    public static final String ERROR_SOCIO_NO_TIENEN_PRESTAMO = "Este socio no tiene prestamos registrados";

    public SocioException(String mensaje) {
        super("Error: " + mensaje);
    }
}
