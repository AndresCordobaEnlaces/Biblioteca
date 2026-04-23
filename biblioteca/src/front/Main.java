package front;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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

	/**
	 * Metodo main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int opcion = -1;

		do {
			System.out.println();
			System.out.println("Seleccione una opción:");
			System.out.println("1) Menú Libros");
			System.out.println("2) Menú Socios");
			System.out.println("3) Menú Préstamos");
			System.out.println("4) Menú Ampliado");
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
			case 4:
				menuAmpliado();
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
	 * Saca por pantalla las opciones del menu libro
	 * 
	 * @author Dan Bolocan
	 */
	public static void escribirMenuOpcionesLibro() {
		System.out.println();
		System.out.println("Menú Libros");
		System.out.println("0) Volver");
		System.out.println("1) Insertar libro");
		System.out.println("2) Eliminar libro");
		System.out.println("3) Consultar todos");
		System.out.println("4) Consultar por escritor");
		System.out.println("5) Libros no prestados");
		System.out.println("6) Libros devueltos por fecha");
	}

	/**
	 * Metodo del menu del libro
	 * 
	 * @author Dan Bolocan
	 */
	public static void menuLibros() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesLibro();
				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {

				case 1:
					String isbn = Teclado.leerCadena("ISBN: ");
					while (!FuncionesRegex.isbnBien(isbn)) {
						isbn = Teclado.leerCadena("ISBN válido: ");
					}

					String titulo = Teclado.leerCadena("Título: ");
					String escritor = Teclado.leerCadena("Escritor: ");

					int anyo = Teclado.leerEntero("Año: ");
					while (!FuncionesRegex.anyoBien(anyo)) {
						anyo = Teclado.leerEntero("Año válido: ");
					}

					float puntuacion = (float) Teclado.leerReal("Puntuación: ");

					System.out.println(AccesoLibro.anadirLibro(isbn, titulo, escritor, anyo, puntuacion)
							? "Libro insertado correctamente."
							: "No se pudo insertar el libro.");
					break;

				case 2:
					int cod = Teclado.leerEntero("Código: ");
					System.out.println(AccesoLibro.borrarLibroPorCodigo(cod) ? "Libro eliminado correctamente."
							: "No se pudo eliminar el libro.");
					break;

				case 3:
					ArrayList<Libro> libros = AccesoLibro.consultarLibros();
					for (Libro l : libros) {
						System.out.println("- " + l);
					}
					break;

				case 4:
					String esc = Teclado.leerCadena("Escritor: ");
					for (Libro l : AccesoLibro.consultarLibrosOrdenados(esc)) {
						System.out.println("- " + l);
					}
					break;

				case 5:
					for (Libro l : AccesoLibro.consultarLibrosNoPrestados()) {
						System.out.println("- " + l);
					}
					break;

				case 6:
					String fecha = Teclado.leerCadena("Fecha yyyy-mm-dd: ");
					while (!FuncionesRegex.fechaBien(fecha)) {
						fecha = Teclado.leerCadena("Fecha válida: ");
					}

					for (Libro l : AccesoLibro.consultarLibrosDevueltos(fecha)) {
						System.out.println("- " + l);
					}
					break;

				case 0:
					System.out.println("Volviendo...");
					break;
				default:
					System.out.println("Opción no válida.");
				}

			} catch (BDException | LibroException e) {
				System.out.println(e.getMessage());
			}

		} while (opcion != 0);
	}

	/**
	 * Saca por pantalla las opciones del menu de socios
	 */
	public static void escribirMenuOpcionesSocio() {
		System.out.println();
		System.out.println("Menú Socios");
		System.out.println("0) Volver");
		System.out.println("1) Insertar");
		System.out.println("2) Eliminar");
		System.out.println("3) Consultar todos");
		System.out.println("4) Por localidad");
		System.out.println("5) Sin préstamos");
		System.out.println("6) Por fecha");
	}

	/**
	 * Metodo del menu del libro
	 */
	public static void menuSocios() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesSocio();
				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {

				case 1:
					String dni = Teclado.leerCadena("DNI: ");
					while (!FuncionesRegex.dniBien(dni)) {
						dni = Teclado.leerCadena("DNI válido: ");
					}

					String nombre = Teclado.leerCadena("Nombre: ");
					String dom = Teclado.leerCadena("Domicilio: ");

					String tel = Teclado.leerCadena("Teléfono: ");
					while (!FuncionesRegex.telefonoBien(tel)) {
						tel = Teclado.leerCadena("Teléfono válido: ");
					}

					String mail = Teclado.leerCadena("Correo: ");
					while (!FuncionesRegex.correoBien(mail)) {
						mail = Teclado.leerCadena("Correo válido: ");
					}

					System.out.println(
							AccesoSocio.insertarSocio(dni, nombre, dom, tel, mail) ? "Socio insertado correctamente."
									: "No se pudo insertar el socio.");
					break;

				case 2:
					int cod = Teclado.leerEntero("Código: ");
					System.out.println(AccesoSocio.eliminarSocio(cod) ? "Socio eliminado correctamente."
							: "No se pudo eliminar el socio.");
					break;

				case 3:
					for (Socio s : AccesoSocio.consultarSocios()) {
						System.out.println("- " + s);
					}
					break;

				case 4:
					String loc = Teclado.leerCadena("Localidad: ");
					for (Socio s : AccesoSocio.consultarPorLocalidad(loc)) {
						System.out.println("- " + s);
					}
					break;

				case 5:
					for (Socio s : AccesoSocio.sociosSinPrestamos()) {
						System.out.println("- " + s);
					}
					break;

				case 6:
					String fecha = Teclado.leerCadena("Fecha yyyy-mm-dd: ");
					while (!FuncionesRegex.fechaBien(fecha)) {
						fecha = Teclado.leerCadena("Fecha válida: ");
					}

					for (Socio s : AccesoSocio.sociosPorFecha(fecha)) {
						System.out.println("- " + s);
					}
					break;

				case 0:
					System.out.println("Volviendo...");
					break;
				default:
					System.out.println("Opción no válida.");
				}

			} catch (BDException | SocioException e) {
				System.out.println(e.getMessage());
			}

		} while (opcion != 0);
	}

	/**
	 * Saca por pantalla las opciones del menu de prestamos
	 * 
	 * @author AndresCordoba
	 */
	public static void escribirMenuOpcionesPrestamo() {
		System.out.println();
		System.out.println("Menú Préstamos");
		System.out.println("0) Volver");
		System.out.println("1) Insertar");
		System.out.println("2) Actualizar devolución");
		System.out.println("3) Eliminar");
		System.out.println("4) Consultar todos");
		System.out.println("5) No devueltos");
		System.out.println("6) Por fecha");
	}

	/**
	 * Metodo del menu de prestamos
	 * 
	 * @author AndresCordoba
	 */
	public static void menuPrestamos() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesPrestamo();
				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {

				case 1:
					int libro = Teclado.leerEntero("Código libro: ");
					int socio = Teclado.leerEntero("Código socio: ");

					String inicio = Teclado.leerCadena("Fecha inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(inicio)) {
						inicio = Teclado.leerCadena("Fecha válida: ");
					}

					String fin = Teclado.leerCadena("Fecha fin (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(fin)) {
						fin = Teclado.leerCadena("Fecha válida: ");
					}

					System.out.println(AccesoPrestamo.insertarPrestamo(libro, socio, inicio, fin)
							? "Préstamo insertado correctamente."
							: "No se pudo insertar el préstamo.");
					break;

				case 2:
					libro = Teclado.leerEntero("Código libro: ");
					socio = Teclado.leerEntero("Código socio: ");

					inicio = Teclado.leerCadena("Fecha inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(inicio)) {
						inicio = Teclado.leerCadena("Fecha válida: ");
					}

					String devolucion = Teclado.leerCadena("Fecha devolución (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(devolucion)) {
						devolucion = Teclado.leerCadena("Fecha válida: ");
					}

					System.out.println(AccesoPrestamo.actualizarPrestamo(libro, socio, inicio, devolucion)
							? "Préstamo actualizado correctamente."
							: "No se pudo actualizar el préstamo.");
					break;

				case 3:
					libro = Teclado.leerEntero("Código libro: ");
					socio = Teclado.leerEntero("Código socio: ");

					inicio = Teclado.leerCadena("Fecha inicio (yyyy-mm-dd): ");
					while (!FuncionesRegex.fechaBien(inicio)) {
						inicio = Teclado.leerCadena("Fecha válida: ");
					}

					System.out.println(
							AccesoPrestamo.eliminarPrestamo(libro, socio, inicio) ? "Préstamo eliminado correctamente."
									: "No se pudo eliminar el préstamo.");
					break;

				case 4:
					for (Prestamo p : AccesoPrestamo.consultarTodosPrestamos()) {
						System.out.println("- " + p);
					}
					break;

				case 5:
					for (Prestamo p : AccesoPrestamo.consultarLosPrestamosNoDevueltos()) {
						System.out.println("- " + p);
					}
					break;

				case 6:
					String fecha = Teclado.leerCadena("Fecha yyyy-mm-dd: ");
					while (!FuncionesRegex.fechaBien(fecha)) {
						fecha = Teclado.leerCadena("Fecha válida: ");
					}

					for (String p : AccesoPrestamo.consultarPrestamosConFechaDevolucion(fecha)) {
						System.out.println("- " + p);
					}
					break;

				case 0:
					System.out.println("Volviendo...");
					break;
				default:
					System.out.println("Opción no válida.");
				}

			} catch (BDException | PrestamosException e) {
				System.out.println(e.getMessage());
			}

		} while (opcion != 0);
	}

	/**
	 * 
	 */
	public static void escribirMenuOpcionesAmpliado() {
		System.out.println();
		System.out.println("Menú Ampliado");
		System.out.println("0) Volver");
		System.out.println("1) Libros menos prestados");
		System.out.println("2) Socios con más préstamos");
		System.out.println("3) Libros bajo media");
		System.out.println("4) Socios sobre media");
		System.out.println("5) Ranking libros");
		System.out.println("6) Ranking socios");
	}

	/**
	 * 
	 */
	public static void menuAmpliado() {
		int opcion = -1;

		do {
			try {
				escribirMenuOpcionesAmpliado();
				opcion = Teclado.leerEntero("Opción: ");

				switch (opcion) {

				case 1:
					LinkedHashMap<Libro, Integer> menorPrestado = AccesoLibro.consultarMenorLibroPrestado();
					for (Libro l : menorPrestado.keySet()) {
						System.out.println("- " + l + " | veces prestado: " + menorPrestado.get(l));
					}
					break;

				case 2:
					for (Socio s : AccesoSocio.sociosMasPrestamos()) {
						System.out.println("- " + s);
					}
					break;

				case 3:
					LinkedHashMap<Libro, Integer> bajoMedia = AccesoLibro.consultarLibroPrestadoInferiorMedia();
					for (Libro l : bajoMedia.keySet()) {
						System.out.println("- " + l + " | veces prestado: " + bajoMedia.get(l));
					}
					break;

				case 4:
					for (Socio s : AccesoSocio.sociosSobreMediaPrestamos()) {
						System.out.println("- " + s);
					}
					break;

				case 5:
					LinkedHashMap<Libro, Integer> rankingLibros = AccesoPrestamo
							.consultarNumeroDeVecesLibrosPrestados();
					for (Libro l : rankingLibros.keySet()) {
						System.out.println("ISBN: " + l.getIsbn() + ", Título: " + l.getTitulo()
								+ ", número de veces prestado: " + rankingLibros.get(l));
					}
					break;

				case 6:
					LinkedHashMap<Socio, Integer> rankingSocios = AccesoPrestamo
							.consultarNumeroDeVecesPrestamosDeSocios();
					for (Socio s : rankingSocios.keySet()) {
						System.out.println("DNI: " + s.getDni() + ", Nombre: " + s.getNombre()
								+ ", número de veces prestado: " + rankingSocios.get(s));
					}
					break;

				case 0:
					System.out.println("Volviendo...");
					break;
				default:
					System.out.println("Opción no válida.");
				}

			} catch (BDException e) {
				System.out.println(e.getMessage());
			} catch (LibroException e) {
				System.out.println(e.getMessage());
			} catch (SocioException e) {
				System.out.println(e.getMessage());
			}

		} while (opcion != 0);
	}
}