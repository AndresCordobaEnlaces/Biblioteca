package exceptions;

/**
 * 
 * PrestamosException
 * 
 * @author AndresCordoba
 */
public class PrestamosException extends Exception {

	public static final String ESTA_PRESTADO = "Se ha prestado ese libro a un socio y no lo ha devuelto";
	public static final String TIENE_PRESTADO = "Ese socio ya tiene un libro prestado y no lo ha devuelto";
	public static final String NO_EXISTE_LIBRO_SOCIO = "No existen libros o socios con esos datos";
	public static final String NO_EXISTE_PRESTAMO = "No existe un prestamo con esos datos";
	public static final String ERROR_PRESTAMOS_BD_VACIA = "No hay prestamos en la base de datos";
	public static final String ERROR_PRESTAMOS_NO_DEVUELTOS = "No hay prestamos no devueltos en la base de datos";
	public static final String ERROR_PRESTAMOS_FECHA = "No hay prestamos en esa fecha";
	public static final String ERROR_FECHA_INVALIDA = "La fecha indicada no tiene un formato valido";
	public static final String ERROR_FECHA_DEVOLUCION = "La fecha de devolucion no puede ser menor que la fecha de inicio";

	/**
	 * 
	 * @param mensaje
	 */
	public PrestamosException(String mensaje) {
		super("Error: " + mensaje);
	}
}
