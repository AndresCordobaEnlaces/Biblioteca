package front;

import dao.AccesoLibro;
import entrada.Teclado;
import exceptions.BDException;
import exceptions.LibroException;
import models.Libro;
import regex.FuncionesRegex;

public class Main {

    public static void main(String[] args) {
        menuLibros();
    }

    /**
     * Muestra el menu de opciones de libros.
     *
     * @author Dan Bolocan
     */
    public static void escribirMenuOpcionesLibro() {
        System.out.println();
        System.out.println("Elige una opción del menu de Libro");
        System.out.println("1) Insertar un libro en la base de datos");
        System.out.println("2) Eliminar un libro, por código, de la base de datos");
        System.out.println("3) Consultar todos los libros de la base de datos.");
        System.out.println(
        System.out.println("5) Consultar los libros no prestados de la base de datos.");
        System.out.println("6) Consultar los libros devueltos, en una fecha, de la base de datos.");
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
}
