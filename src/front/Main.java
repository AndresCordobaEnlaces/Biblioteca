package front;

import java.util.ArrayList;

import dao.AccesoLibro;
import dao.AccesoPrestamo;
import dao.AccesoSocio;
import entrada.Teclado;
import exceptions.BDException;
import exceptions.LibroException;
import exceptions.PrestamosException;
import exceptions.SocioException;
import models.Libro;
import models.Prestamo;
import models.Socio;
import regex.FuncionesRegex;

public class Main {

	public static void main(String[] args) {
		int opcion = -1;

		do {
			System.out.println();
			System.out.println("Seleccione una opción:");
			System.out.println("1) Menú Libros");
			System.out.println("2) Menú Socios");
			System.out.println("3) Menú Préstamos");
			System.out.println("0) Salir");

			opcion = Teclado.leerEntero("Opción: ");

			switch (opcion) {
			case 1:
				menuLibros();
				break;
			case 2:
				menuSocios();
				break;
			case 3:
				menuPrestamos();
				break;
			case 0:
				System.out.println("Saliendo del programa...");
				break;
			default:
				System.out.println("Opción no válida. Intente de nuevo.");
			}
		} while (opcion != 0);
	}

	/**
	 * Muestra el menu de opciones de libros.
	 *
	 * @author Dan Bolocan
	 */
	public static void escribirMenuOpcionesLibro() {
		System.out.println();
		System.out.println("Elige una opción del menu de Libro");
		System.out.println("0) Volver al menu principal.");
		System.out.println("1) Insertar un libro en la base de datos");
		System.out.println("2) Eliminar un libro, por código, de la base de datos");
		System.out.println("3) Consultar todos los libros de la base de datos.");
		System.out.println(
				"4) Consultar varios libros, por escritor, de la base de datos, ordenados por puntuación decendente.");
		System.out.println("5) Consultar los libros no prestados de la base de datos.");
		System.out.println("6) Consultar los libros devueltos, en una fecha, de la base de datos.");
	}

	/**
	 * 
	 */
	public static void escribirMenuOpcionesSocio() {
		System.out.println();
		System.out.println("Elige una opción del menu de Socio");
		System.out.println("0) Volver al menu principal.");
		System.out.println("1) Insertar un socio en la base de datos");
		System.out.println("2) Eliminar un socio por código");
		System.out.println("3) Consultar todos los socios");
		System.out.println("4) Consultar socios por localidad");
		System.out.println("5) Consultar socios sin préstamos");
		System.out.println("6) Consultar socios con préstamos en una fecha");
	}

	/**
	 * Muestra el menu de opciones de prestamos
	 *
	 * @author Andrés Córdoba
	 */
	public static void escribirMenuOpcionesPrestamo() {
		System.out.println();
		System.out.println("Elige una opción del menu de Prestamo");
		System.out.println("0) Volver al menú principal.");
		System.out.println("1) Insertar un prestamo en la base de datos");
		System.out.println("2) Actualizar la fecha de devolución de un préstamo");
		System.out.println("3) Eliminar un préstamo por datos identificativos");
		System.out.println("4) Consultar todos los préstamos de la base de datos");
		System.out.println("5) Consultar los préstamos no devueltos de la base de datos");
		System.out.println("6) Consultar préstamos realizados en una fecha");
	}

	/**
	 * Ejecuta el menu de libros y gestiona cada opcion.
	 *
	 * @author Dan Bolocan
	 */
	public static void menuLibros() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesLibro();
				System.out.println();
				opcion = Teclado.leerEntero("Opción: ");
				switch (opcion) {
				case 1:
					System.out.println("Insertar libro...");
					// Leer datos del libro
					String isbn = Teclado.leerCadena("Introduce el ISBN: ");
					while (!FuncionesRegex.isbnBien(isbn)) {
						isbn = Teclado.leerCadena("Introduce un ISBN valido (isbn-10 o isbn-13): ");
					}
					String titulo = Teclado.leerCadena("Introduce el título: ");
					String escritor = Teclado.leerCadena("Introduce el escritor: ");
					int anyoPublicacion = Teclado.leerEntero("Introduce el año de publicación: ");
					while (!FuncionesRegex.anyoBien(anyoPublicacion)) {
						anyoPublicacion = Teclado.leerEntero("Año válido por favor: ");
					}
					float puntuacion = (float) Teclado.leerReal("Introduce la puntuación: ");

					boolean anadiLibro = AccesoLibro.anadirLibro(isbn, titulo, escritor, anyoPublicacion, puntuacion);

					if (anadiLibro) {
						System.out.println("Libro añadido correctamente.");
					} else {
						System.out.println("No se pudo añadir el libro.");
					}

					break;
				case 2:
					System.out.println("Eliminar libro...");
					// Leer codigo del libro
					int codigo = Teclado.leerEntero("Introduce el código del libro: ");

					boolean borrarLibroPorCodigo = AccesoLibro.borrarLibroPorCodigo(codigo);

					if (borrarLibroPorCodigo) {
						System.out.println("Libro eliminado correctamente.");
					} else {
						System.out.println("No se pudo eliminar el libro.");
					}

					break;
				case 3:
					System.out.println("Consultar todos los libros...");

					ArrayList<Libro> consultarLibros = AccesoLibro.consultarLibros();

					if (consultarLibros.isEmpty()) {
						System.out.println("No hay ningun libro en la coleccion");
					} else {
						System.out.println("Lista de libros");
						for (Libro libro : consultarLibros) {
							System.out.println("- " + libro);
						}
					}

					break;
				case 4:
					System.out.println("Consultar libros por escritor...");
					// Leer escritor
					String nombreEscritor = Teclado.leerCadena("Introduce el nombre del escritor: ");

					ArrayList<Libro> consultarLibrosOrdenados = AccesoLibro.consultarLibrosOrdenados(nombreEscritor);

					if (consultarLibrosOrdenados.isEmpty()) {
						System.out.println("No se encontro ningún libro");
					} else {
						System.out.println("Lista de libros: ");
						for (Libro libro : consultarLibrosOrdenados) {
							System.out.println("- " + libro);
						}
					}
					break;
				case 5:
					System.out.println("Consultar libros no prestados...");

					ArrayList<Libro> consultarLibrosNoPrestados = AccesoLibro.consultarLibrosNoPrestados();

					if (consultarLibrosNoPrestados.isEmpty()) {
						System.out.println("No se encontro ningún libro");
					} else {
						System.out.println("Lista de libros no prestados: ");
						for (Libro libro : consultarLibrosNoPrestados) {
							System.out.println("- " + libro);
						}
					}
					break;
				case 6:
					System.out.println("Consultar libros devueltos en una fecha...");
					// Leer fecha de devolucion
					String fechaDevolucion = Teclado.leerCadena("Introduce una fecha: ");
					while (!FuncionesRegex.fechaBien(fechaDevolucion)) {
						fechaDevolucion = Teclado.leerCadena("Introduce una fecha valida (yyyy-mm-dd): ");
					}

					ArrayList<Libro> consultarLibrosDevueltos = AccesoLibro.consultarLibrosDevueltos(fechaDevolucion);

					if (consultarLibrosDevueltos.isEmpty()) {
						System.out.println("No se encontro ningún libro");
					} else {
						System.out.println("Lista de libros devueltos en la fehca: " + fechaDevolucion);
						for (Libro libro : consultarLibrosDevueltos) {
							System.out.println("- " + libro);
						}
					}
					break;
				case 0:
					System.out.println("Regresando al menú principal...");
					break;
				default:
					System.out.println("Opción no válida. Intente de nuevo.");
				}
			} catch (BDException e) {
				System.out.println("Error en la consulta, mensaje de error: " + e.getMessage());
			} catch (LibroException e) {
				System.out.println(e.getMessage());
			}
		} while (opcion != 0);
	}

	/**
	 * 
	 */
	public static void menuSocios() {

		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesSocio();
				System.out.println();

				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {

				case 1:
					System.out.println("Insertar socio...");

					String dni = Teclado.leerCadena("DNI: ");
					String nombre = Teclado.leerCadena("Nombre: ");
					String domicilio = Teclado.leerCadena("Domicilio: ");
					String telefono = Teclado.leerCadena("Teléfono: ");
					String correo = Teclado.leerCadena("Correo: ");

					boolean insertado = AccesoSocio.insertarSocio(dni, nombre, domicilio, telefono, correo);

					if (insertado) {
						System.out.println("Se ha insertado un socio en la base de datos.");
					} else {
						System.out.println("No se pudo insertar el socio.");
					}

					break;

				case 2:
					System.out.println("Eliminar socio...");

					int codigo = Teclado.leerEntero("Código: ");

					boolean eliminado = AccesoSocio.eliminarSocio(codigo);

					if (eliminado) {
						System.out.println("Se ha eliminado un socio de la base de datos.");
					}

					break;

				case 3:
					System.out.println("Consultar todos los socios...");

					ArrayList<Socio> listaSocios = AccesoSocio.consultarSocios();

					System.out.println("Lista de socios:");
					for (Socio s : listaSocios) {
						System.out.println("- " + s);
					}

					break;

				case 4:
					System.out.println("Consultar socios por localidad...");

					String localidad = Teclado.leerCadena("Localidad: ");

					ArrayList<Socio> sociosLocalidad = AccesoSocio.consultarPorLocalidad(localidad);

					System.out.println("Lista de socios:");
					for (Socio s : sociosLocalidad) {
						System.out.println("- " + s);
					}

					break;

				case 5:
					System.out.println("Consultar socios sin préstamos...");

					ArrayList<Socio> sociosSinPrestamos = AccesoSocio.sociosSinPrestamos();

					System.out.println("Lista de socios:");
					for (Socio s : sociosSinPrestamos) {
						System.out.println("- " + s);
					}

					break;

				case 6:
					System.out.println("Consultar socios por fecha...");

					String fecha = Teclado.leerCadena("Introduce la fecha (yyyy-mm-dd): ");

					ArrayList<Socio> sociosFecha = AccesoSocio.sociosPorFecha(fecha);

					System.out.println("Lista de socios:");
					for (Socio s : sociosFecha) {
						System.out.println("- " + s);
					}

					break;

				case 0:
					System.out.println("Saliendo...");
					break;

				default:
					System.out.println("Opción no válida.");
				}

			} catch (BDException e) {
				System.out.println("Error en BD: " + e.getMessage());
			} catch (SocioException e) {
				System.out.println(e.getMessage());
			}

		} while (opcion != 0);
	}

	/**
	 * Ejecuta el menu de Prestamos y gestiona cada opcion.
	 *
	 * @author Andrés Córdoba
	 */
	public static void menuPrestamos() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesPrestamo();
				System.out.println();
				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {
				case 1:
					System.out.println("Insertar préstamo...");

					int codigoLibro = Teclado.leerEntero("Introduce el código del libro: ");
					int codigoSocio = Teclado.leerEntero("Introduce el código del socio: ");
					String fechaInicio = Teclado.leerCadena("Introduce la fecha de inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fechaInicio)) {
						fechaInicio = Teclado.leerCadena("Introduce una fecha válida (yyyy-mm-dd): ");
					}
					String fechaFin = Teclado.leerCadena("Fecha fin: ");

					boolean insertarPrestamo = AccesoPrestamo.insertarPrestamo(codigoLibro, codigoSocio, fechaInicio,
							fechaFin);

					if (insertarPrestamo) {
						System.out.println("Préstamo insertado correctamente.");
					} else {
						System.out.println("No se pudo insertar el préstamo.");
					}
					break;
				case 2:
					System.out.println("Actualizar préstamo...");

					codigoLibro = Teclado.leerEntero("Introduce el código del libro: ");
					codigoSocio = Teclado.leerEntero("Introduce el código del socio: ");
					fechaInicio = Teclado.leerCadena("Introduce la fecha de inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fechaInicio)) {
						fechaInicio = Teclado.leerCadena("Introduce una fecha válida (yyyy-mm-dd): ");
					}

					String fechaDevolucion = Teclado.leerCadena("Introduce la fecha de devolución (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fechaDevolucion)) {
						fechaDevolucion = Teclado.leerCadena("Introduce una fecha válida (yyyy-mm-dd): ");
					}

					boolean actualizarPrestamo = AccesoPrestamo.actualizarPrestamo(codigoLibro, codigoSocio,
							fechaInicio, fechaDevolucion);

					if (actualizarPrestamo) {
						System.out.println("Préstamo actualizado correctamente.");
					} else {
						System.out.println("No se pudo actualizar el préstamo.");
					}
					break;

				case 3:
					System.out.println("Eliminar préstamo...");

					codigoLibro = Teclado.leerEntero("Introduce el código del libro: ");
					codigoSocio = Teclado.leerEntero("Introduce el código del socio: ");
					fechaInicio = Teclado.leerCadena("Introduce la fecha de inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fechaInicio)) {
						fechaInicio = Teclado.leerCadena("Introduce una fecha válida (yyyy-mm-dd): ");
					}

					boolean eliminarPrestamo = AccesoPrestamo.eliminarPrestamo(codigoLibro, codigoSocio, fechaInicio);

					if (eliminarPrestamo) {
						System.out.println("Préstamo eliminado correctamente.");
					} else {
						System.out.println("No se pudo eliminar el préstamo.");
					}
					break;

				case 4:
					System.out.println("Consultar todos los préstamos...");

					ArrayList<Prestamo> consultarTodosPrestamos = AccesoPrestamo.consultarTodosPrestamos();

					System.out.println("Lista de préstamos:");
					for (Prestamo prestamo : consultarTodosPrestamos) {
						System.out.println("- " + prestamo);
					}
					break;

				case 5:
					System.out.println("Consultar préstamos no devueltos...");

					ArrayList<Prestamo> consultarNoDevueltos = AccesoPrestamo.consultarLosPrestamosNoDevueltos();

					System.out.println("Lista de préstamos no devueltos:");
					for (Prestamo prestamo : consultarNoDevueltos) {
						System.out.println("- " + prestamo);
					}
					break;

				case 6:
					System.out.println("Consultar préstamos realizados en una fecha...");

					fechaInicio = Teclado.leerCadena("Introduce la fecha de inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fechaInicio)) {
						fechaInicio = Teclado.leerCadena("Introduce una fecha válida (yyyy-mm-dd): ");
					}

					ArrayList<String> listaPrestamos = AccesoPrestamo.consultarPrestamosConFechaDevolucion(fechaInicio);

					System.out.println("Préstamos encontrados:");
					for (String linea : listaPrestamos) {
						System.out.println("- " + linea);
					}
					break;

				case 0:
					System.out.println("Volviendo al menú principal...");
					break;

				default:
					System.out.println("Opción no válida. Intente de nuevo.");
				}

			} catch (BDException e) {
				System.out.println("Error en la consulta, mensaje de error: " + e.getMessage());
			} catch (PrestamosException e) {
				System.out.println(e.getMessage());
			}
		} while (opcion != 0);
	}
}
