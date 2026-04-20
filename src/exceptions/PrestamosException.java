package exceptions;

public class PrestamosException extends Exception {

	public static final String ESTA_PRESTADO = "Se ha prestado ese libro a un socio y aún no lo ha devuelto.";
	public static final String TIENE_PRESTADO = "Ese socio ya tiene un libro prestado y aún no lo ha devuelto.";
	public static final String NO_EXISTE_LIBRO_SOCIO = "No existen libros o socios con esos datos identificativos.";
	public static final String NO_EXISTE_PRESTAMO = "No existe un préstamo con esos datos identificativos.";
	public static final String ERROR_PRESTAMOS_BD_VACIA = "No hay préstamos en la base de datos.";
	public static final String ERROR_PRESTAMOS_NO_DEVUELTOS = "No hay préstamos no devueltos en la base de datos.";
	public static final String ERROR_PRESTAMOS_FECHA = "No hay préstamos en esa fecha.";
	public static final String ERROR_FECHA_DEVOLUCION = "La fecha de devolución no puede ser menor que la fecha de inicio.";

	public PrestamosException(String mensaje) {
		super("Error: " + mensaje);
	}
}