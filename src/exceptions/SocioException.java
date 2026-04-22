package exceptions;

	public class SocioException extends Exception {
		
		public static final String ERROR_SOCIO_NOEXISTE =
				"No existe ningún socio con ese código.";
		
		public static final String ERROR_SOCIO_BDEmpty =
				"No hay socios en la base de datos.";
		
		public static final String ERROR_SOCIO_LOCALIDAD =
				"No existe ningún socio con esa localidad.";
		
		public static final String ERROR_SOCIO_SINPRESTAMOS =
				"No existe ningún socio sin préstamos.";
		
		public static final String ERROR_SOCIO_FECHA =
				"No existe ningún socio con préstamos en esa fecha.";
		
		public static final String ERROR_SOCIO_PRESTAMO =
				"El socio está referenciado en un préstamo.";
		
		public static final String ERROR_SOCIO_NO_TIENEN_PRESTAMO =
				"Ningun socio tiene préstamos.";
		
		public SocioException(String mensaje) {
			super(mensaje);
		}
	}

