package tests;

import config.ConfigMySQL;
import dao.AccesoLibro;
import dao.AccesoPrestamo;
import dao.AccesoSocio;
import exceptions.BDException;
import exceptions.LibroException;
import exceptions.PrestamosException;
import exceptions.SocioException;
import models.Libro;
import models.Prestamo;
import models.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DatabaseIntegrationTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    private static final String MARKER = "ITEST_" + System.currentTimeMillis();
    private static final String LOCALIDAD = "Madrid " + MARKER;
    private static final String WRITER_ORDEN = "Autor Orden " + MARKER;

    private static int libroTopId;
    private static int libroBajoId;
    private static int libroLibreId;
    private static int libroMedioId;

    private static String libroTopIsbn;
    private static String libroBajoIsbn;
    private static String libroLibreIsbn;
    private static String libroMedioIsbn;

    private static int socioTopId;
    private static int socioBajoId;
    private static int socioNuncaId;

    private static String socioTopDni;
    private static String socioBajoDni;
    private static String socioNuncaDni;

    public static void main(String[] args) throws Exception {
        cleanupByMarker();

        try {
            seedData();

            run("Libros 1 insertar", DatabaseIntegrationTestRunner::testLibrosInsertar);
            run("Libros no admite ISBN duplicado", DatabaseIntegrationTestRunner::testLibrosIsbnDuplicado);
            run("Libros no admite anyo invalido", DatabaseIntegrationTestRunner::testLibrosAnyoInvalido);
            run("Libros no admite puntuacion invalida", DatabaseIntegrationTestRunner::testLibrosPuntuacionInvalida);
            run("Libros no admite titulo vacio", DatabaseIntegrationTestRunner::testLibrosTituloVacio);
            run("Libros 2 eliminar", DatabaseIntegrationTestRunner::testLibrosEliminar);
            run("Libros 3 consultar todos", DatabaseIntegrationTestRunner::testLibrosConsultarTodos);
            run("Libros 4 consultar por escritor ordenado", DatabaseIntegrationTestRunner::testLibrosPorEscritorOrdenados);
            run("Libros 5 no prestados", DatabaseIntegrationTestRunner::testLibrosNoPrestados);
            run("Libros 6 devueltos por fecha", DatabaseIntegrationTestRunner::testLibrosDevueltosPorFecha);

            run("Socios 1 insertar", DatabaseIntegrationTestRunner::testSociosInsertar);
            run("Socios no admite DNI invalido", DatabaseIntegrationTestRunner::testSociosDniInvalido);
            run("Socios no admite telefono invalido", DatabaseIntegrationTestRunner::testSociosTelefonoInvalido);
            run("Socios no admite correo invalido", DatabaseIntegrationTestRunner::testSociosCorreoInvalido);
            run("Socios no admite nombre vacio", DatabaseIntegrationTestRunner::testSociosNombreVacio);
            run("Socios 2 eliminar", DatabaseIntegrationTestRunner::testSociosEliminar);
            run("Socios 3 consultar todos", DatabaseIntegrationTestRunner::testSociosConsultarTodos);
            run("Socios 4 por localidad", DatabaseIntegrationTestRunner::testSociosPorLocalidad);
            run("Socios 5 sin prestamos", DatabaseIntegrationTestRunner::testSociosSinPrestamos);
            run("Socios 6 por fecha", DatabaseIntegrationTestRunner::testSociosPorFecha);

            run("Prestamos 1-6 flujo completo", DatabaseIntegrationTestRunner::testPrestamosFlujoCompleto);
            run("Prestamos no admite formato de fecha invalido", DatabaseIntegrationTestRunner::testPrestamoFechaInvalida);
            run("Prestamos bloquea libro ya prestado", DatabaseIntegrationTestRunner::testPrestamoLibroYaPrestado);
            run("Prestamos bloquea socio con prestamo activo", DatabaseIntegrationTestRunner::testPrestamoSocioConPrestamoActivo);
            run("Prestamos valida fecha fin", DatabaseIntegrationTestRunner::testPrestamoFechaFinAnterior);
            run("Prestamos detecta libro o socio inexistente", DatabaseIntegrationTestRunner::testPrestamoLibroOSocioInexistente);

            run("Ampliado 1 libros menos prestados", DatabaseIntegrationTestRunner::testAmpliadoLibrosMenosPrestados);
            run("Ampliado 2 socios con mas prestamos", DatabaseIntegrationTestRunner::testAmpliadoSociosMasPrestamos);
            run("Ampliado 3 libros bajo media", DatabaseIntegrationTestRunner::testAmpliadoLibrosBajoMedia);
            run("Ampliado 4 socios sobre media", DatabaseIntegrationTestRunner::testAmpliadoSociosSobreMedia);
            run("Ampliado 5 ranking libros", DatabaseIntegrationTestRunner::testAmpliadoRankingLibros);
            run("Ampliado 6 ranking socios", DatabaseIntegrationTestRunner::testAmpliadoRankingSocios);
        } finally {
            cleanupByMarker();
        }

        System.out.println();
        System.out.println("Pruebas BD superadas: " + passed);
        System.out.println("Pruebas BD fallidas: " + failed);

        if (failed > 0) {
            throw new AssertionError("Hay pruebas de integracion fallidas");
        }
    }

    private static void seedData() throws Exception {
        libroTopIsbn = buildValidIsbn13(101);
        libroBajoIsbn = buildValidIsbn13(102);
        libroLibreIsbn = buildValidIsbn13(103);
        libroMedioIsbn = buildValidIsbn13(104);

        libroTopId = insertBookDirect(libroTopIsbn, "Libro Top " + MARKER, WRITER_ORDEN, 2020, 9.0f);
        libroBajoId = insertBookDirect(libroBajoIsbn, "Libro Bajo " + MARKER, WRITER_ORDEN, 2021, 4.0f);
        libroLibreId = insertBookDirect(libroLibreIsbn, "Libro Libre " + MARKER, "Autor Libre " + MARKER, 2022, 7.0f);
        libroMedioId = insertBookDirect(libroMedioIsbn, "Libro Medio " + MARKER, "Autor Medio " + MARKER, 2023, 8.0f);

        socioTopDni = buildValidDni(10000001);
        socioBajoDni = buildValidDni(10000002);
        socioNuncaDni = buildValidDni(10000003);

        socioTopId = insertSocioDirect(
                socioTopDni,
                "Ana " + MARKER,
                LOCALIDAD,
                "612345671",
                "ana." + MARKER.toLowerCase() + "@mail.com"
        );
        socioBajoId = insertSocioDirect(
                socioBajoDni,
                "Bea " + MARKER,
                LOCALIDAD,
                "612345672",
                "bea." + MARKER.toLowerCase() + "@mail.com"
        );
        socioNuncaId = insertSocioDirect(
                socioNuncaDni,
                "Zoe " + MARKER,
                LOCALIDAD,
                "612345673",
                "zoe." + MARKER.toLowerCase() + "@mail.com"
        );

        insertPrestamoDirect(libroTopId, socioTopId, "2026-01-01", "2026-01-10", "2026-01-08");
        insertPrestamoDirect(libroTopId, socioTopId, "2026-02-01", "2026-02-10", "2026-02-08");
        insertPrestamoDirect(libroBajoId, socioBajoId, "2026-03-01", "2026-03-10", "2026-03-08");
        insertPrestamoDirect(libroMedioId, socioTopId, "2026-04-01", "2026-04-10", "2026-04-09");
    }

    private static void testLibrosInsertar() throws Exception {
        String isbn = buildValidIsbn13(201);
        boolean inserted = AccesoLibro.anadirLibro(isbn, "Libro Insertado " + MARKER, "Autor Insertado " + MARKER, 2024, 6.5f);
        assertTrue(inserted);

        int codigo = findBookCodeByIsbn(isbn);
        assertTrue(codigo > 0);
    }

    private static void testLibrosIsbnDuplicado() throws Exception {
        try {
            AccesoLibro.anadirLibro(libroTopIsbn, "Duplicado " + MARKER, "Autor Duplicado " + MARKER, 2024, 5.0f);
            throw new AssertionError("se esperaba LibroException");
        } catch (LibroException e) {
            assertEquals(LibroException.ERROR_LIBRO_ISBNEXISTE, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testLibrosAnyoInvalido() throws Exception {
        String isbn = buildValidIsbn13(205);

        try {
            AccesoLibro.anadirLibro(isbn, "Libro Anyo " + MARKER, "Autor Anyo " + MARKER, 3000, 5.0f);
            throw new AssertionError("se esperaba LibroException");
        } catch (LibroException e) {
            assertEquals(LibroException.ERROR_LIBRO_ANYOINVALIDO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testLibrosPuntuacionInvalida() throws Exception {
        String isbn = buildValidIsbn13(206);

        try {
            AccesoLibro.anadirLibro(isbn, "Libro Puntuacion " + MARKER, "Autor Puntuacion " + MARKER, 2024, 11.0f);
            throw new AssertionError("se esperaba LibroException");
        } catch (LibroException e) {
            assertEquals(LibroException.ERROR_LIBRO_PUNTUACIONINVALIDA, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testLibrosTituloVacio() throws Exception {
        String isbn = buildValidIsbn13(207);

        try {
            AccesoLibro.anadirLibro(isbn, "   ", "Autor Vacio " + MARKER, 2024, 5.0f);
            throw new AssertionError("se esperaba LibroException");
        } catch (LibroException e) {
            assertEquals(LibroException.ERROR_LIBRO_TITULOVACIO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testLibrosEliminar() throws Exception {
        String isbn = buildValidIsbn13(202);
        int codigo = insertBookDirect(isbn, "Libro Borrable " + MARKER, "Autor Borrable " + MARKER, 2024, 5.0f);

        boolean borrado = AccesoLibro.borrarLibroPorCodigo(codigo);
        assertTrue(borrado);
        assertEquals(-1, findBookCodeByIsbn(isbn));
    }

    private static void testLibrosConsultarTodos() throws Exception {
        ArrayList<Libro> libros = AccesoLibro.consultarLibros();
        List<Libro> propios = filterLibrosByMarker(libros);

        assertTrue(propios.size() >= 4);
        assertContainsBook(propios, libroTopId);
        assertContainsBook(propios, libroLibreId);
    }

    private static void testLibrosPorEscritorOrdenados() throws Exception {
        ArrayList<Libro> libros = AccesoLibro.consultarLibrosOrdenados(WRITER_ORDEN.toLowerCase());
        List<Libro> propios = filterLibrosByMarker(libros);

        assertEquals(2, propios.size());
        assertEquals(libroTopId, propios.get(0).getCodigo());
        assertEquals(libroBajoId, propios.get(1).getCodigo());
    }

    private static void testLibrosNoPrestados() throws Exception {
        ArrayList<Libro> libros = AccesoLibro.consultarLibrosNoPrestados();
        assertContainsBook(libros, libroLibreId);
    }

    private static void testLibrosDevueltosPorFecha() throws Exception {
        ArrayList<Libro> libros = AccesoLibro.consultarLibrosDevueltos("2026-03-08");
        assertContainsBook(libros, libroBajoId);
    }

    private static void testSociosInsertar() throws Exception {
        String dni = buildValidDni(10000004);
        boolean inserted = AccesoSocio.insertarSocio(
                dni,
                "Socio Insertado " + MARKER,
                "Sevilla " + MARKER,
                "612345674",
                "insertado." + MARKER.toLowerCase() + "@mail.com"
        );
        assertTrue(inserted);
        assertTrue(findSocioCodeByDni(dni) > 0);
    }

    private static void testSociosDniInvalido() throws Exception {
        try {
            AccesoSocio.insertarSocio(
                    "12345678A",
                    "Socio DNI " + MARKER,
                    "Cordoba " + MARKER,
                    "612345678",
                    "dni." + MARKER.toLowerCase() + "@mail.com"
            );
            throw new AssertionError("se esperaba SocioException");
        } catch (SocioException e) {
            assertEquals(SocioException.ERROR_SOCIO_DNIINVALIDO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testSociosTelefonoInvalido() throws Exception {
        try {
            AccesoSocio.insertarSocio(
                    buildValidDni(10000008),
                    "Socio Telefono " + MARKER,
                    "Cordoba " + MARKER,
                    "512345678",
                    "telefono." + MARKER.toLowerCase() + "@mail.com"
            );
            throw new AssertionError("se esperaba SocioException");
        } catch (SocioException e) {
            assertEquals(SocioException.ERROR_SOCIO_TELEFONOINVALIDO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testSociosCorreoInvalido() throws Exception {
        try {
            AccesoSocio.insertarSocio(
                    buildValidDni(10000009),
                    "Socio Correo " + MARKER,
                    "Cordoba " + MARKER,
                    "612345679",
                    "correo..malo@mail.com"
            );
            throw new AssertionError("se esperaba SocioException");
        } catch (SocioException e) {
            assertEquals(SocioException.ERROR_SOCIO_CORREOINVALIDO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testSociosNombreVacio() throws Exception {
        try {
            AccesoSocio.insertarSocio(
                    buildValidDni(10000010),
                    "   ",
                    "Cordoba " + MARKER,
                    "612345680",
                    "nombre." + MARKER.toLowerCase() + "@mail.com"
            );
            throw new AssertionError("se esperaba SocioException");
        } catch (SocioException e) {
            assertEquals(SocioException.ERROR_SOCIO_NOMBREVACIO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testSociosEliminar() throws Exception {
        String dni = buildValidDni(10000005);
        int codigo = insertSocioDirect(
                dni,
                "Socio Borrable " + MARKER,
                "Bilbao " + MARKER,
                "612345675",
                "borrable." + MARKER.toLowerCase() + "@mail.com"
        );

        boolean borrado = AccesoSocio.eliminarSocio(codigo);
        assertTrue(borrado);
        assertEquals(-1, findSocioCodeByDni(dni));
    }

    private static void testSociosConsultarTodos() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.consultarSocios();
        List<Socio> propios = filterSociosByMarker(socios);

        assertTrue(propios.size() >= 3);
        assertContainsSocio(propios, socioTopDni);
        assertContainsSocio(propios, socioNuncaDni);
    }

    private static void testSociosPorLocalidad() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.consultarPorLocalidad(LOCALIDAD);
        List<Socio> propios = filterSociosByMarker(socios);

        assertEquals(3, propios.size());
        assertEquals("Ana " + MARKER, propios.get(0).getNombre());
        assertEquals("Bea " + MARKER, propios.get(1).getNombre());
        assertEquals("Zoe " + MARKER, propios.get(2).getNombre());
    }

    private static void testSociosSinPrestamos() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.sociosSinPrestamos();
        assertContainsSocio(socios, socioNuncaDni);
    }

    private static void testSociosPorFecha() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.sociosPorFecha("2026-01-01");
        assertContainsSocio(socios, socioTopDni);
    }

    private static void testPrestamosFlujoCompleto() throws Exception {
        String isbn = buildValidIsbn13(203);
        String dni = buildValidDni(10000006);

        int libroId = insertBookDirect(isbn, "Libro Prestamo " + MARKER, "Autor Prestamo " + MARKER, 2024, 8.5f);
        int socioId = insertSocioDirect(
                dni,
                "Carla " + MARKER,
                "Valencia " + MARKER,
                "612345676",
                "carla." + MARKER.toLowerCase() + "@mail.com"
        );

        boolean inserted = AccesoPrestamo.insertarPrestamo(libroId, socioId, "2026-05-01", "2026-05-10");
        assertTrue(inserted);

        ArrayList<Prestamo> prestamos = AccesoPrestamo.consultarTodosPrestamos();
        assertContainsPrestamo(prestamos, libroId, socioId, "2026-05-01");

        ArrayList<Prestamo> noDevueltos = AccesoPrestamo.consultarLosPrestamosNoDevueltos();
        assertContainsPrestamo(noDevueltos, libroId, socioId, "2026-05-01");

        ArrayList<String> prestamosPorFecha = AccesoPrestamo.consultarPrestamosConFechaDevolucion("2026-05-01");
        assertTrue(anyLineContains(prestamosPorFecha, isbn));
        assertTrue(anyLineContains(prestamosPorFecha, dni));

        ArrayList<Socio> sociosFecha = AccesoSocio.sociosPorFecha("2026-05-01");
        assertContainsSocio(sociosFecha, dni);

        boolean updated = AccesoPrestamo.actualizarPrestamo(libroId, socioId, "2026-05-01", "2026-05-09");
        assertTrue(updated);

        ArrayList<Libro> devueltos = AccesoLibro.consultarLibrosDevueltos("2026-05-09");
        assertContainsBook(devueltos, libroId);

        boolean deleted = AccesoPrestamo.eliminarPrestamo(libroId, socioId, "2026-05-01");
        assertTrue(deleted);

        ArrayList<Prestamo> trasBorrado = AccesoPrestamo.consultarTodosPrestamos();
        assertNotContainsPrestamo(trasBorrado, libroId, socioId, "2026-05-01");
    }

    private static void testPrestamoFechaInvalida() throws Exception {
        try {
            AccesoPrestamo.insertarPrestamo(libroLibreId, socioNuncaId, "2026-13-01", "2026-13-10");
            throw new AssertionError("se esperaba PrestamosException");
        } catch (PrestamosException e) {
            assertEquals(PrestamosException.ERROR_FECHA_INVALIDA, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testPrestamoLibroYaPrestado() throws Exception {
        String isbn = buildValidIsbn13(208);
        String dni1 = buildValidDni(10000011);
        String dni2 = buildValidDni(10000012);

        int libroId = insertBookDirect(isbn, "Libro Activo " + MARKER, "Autor Activo " + MARKER, 2024, 5.5f);
        int socio1Id = insertSocioDirect(
                dni1,
                "Eva " + MARKER,
                "Leon " + MARKER,
                "612345681",
                "eva." + MARKER.toLowerCase() + "@mail.com"
        );
        int socio2Id = insertSocioDirect(
                dni2,
                "Iris " + MARKER,
                "Leon " + MARKER,
                "612345682",
                "iris." + MARKER.toLowerCase() + "@mail.com"
        );

        insertPrestamoDirect(libroId, socio1Id, "2026-07-01", "2026-07-10", null);

        try {
            AccesoPrestamo.insertarPrestamo(libroId, socio2Id, "2026-07-02", "2026-07-11");
            throw new AssertionError("se esperaba PrestamosException");
        } catch (PrestamosException e) {
            assertEquals(PrestamosException.ESTA_PRESTADO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testPrestamoSocioConPrestamoActivo() throws Exception {
        String isbn1 = buildValidIsbn13(209);
        String isbn2 = buildValidIsbn13(210);
        String dni = buildValidDni(10000013);

        int libro1Id = insertBookDirect(isbn1, "Libro Activo Socio 1 " + MARKER, "Autor Activo " + MARKER, 2024, 5.5f);
        int libro2Id = insertBookDirect(isbn2, "Libro Activo Socio 2 " + MARKER, "Autor Activo " + MARKER, 2024, 5.5f);
        int socioId = insertSocioDirect(
                dni,
                "Nora " + MARKER,
                "Leon " + MARKER,
                "612345683",
                "nora." + MARKER.toLowerCase() + "@mail.com"
        );

        insertPrestamoDirect(libro1Id, socioId, "2026-08-01", "2026-08-10", null);

        try {
            AccesoPrestamo.insertarPrestamo(libro2Id, socioId, "2026-08-02", "2026-08-11");
            throw new AssertionError("se esperaba PrestamosException");
        } catch (PrestamosException e) {
            assertEquals(PrestamosException.TIENE_PRESTADO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testPrestamoFechaFinAnterior() throws Exception {
        String isbn = buildValidIsbn13(204);
        String dni = buildValidDni(10000007);

        int libroId = insertBookDirect(isbn, "Libro Fecha " + MARKER, "Autor Fecha " + MARKER, 2024, 8.0f);
        int socioId = insertSocioDirect(
                dni,
                "Diego " + MARKER,
                "Toledo " + MARKER,
                "612345677",
                "diego." + MARKER.toLowerCase() + "@mail.com"
        );

        try {
            AccesoPrestamo.insertarPrestamo(libroId, socioId, "2026-06-10", "2026-06-01");
            throw new AssertionError("se esperaba PrestamosException");
        } catch (PrestamosException e) {
            assertEquals(PrestamosException.ERROR_FECHA_DEVOLUCION, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testPrestamoLibroOSocioInexistente() throws Exception {
        try {
            AccesoPrestamo.insertarPrestamo(999999, 999999, "2026-06-10", "2026-06-20");
            throw new AssertionError("se esperaba PrestamosException");
        } catch (PrestamosException e) {
            assertEquals(PrestamosException.NO_EXISTE_LIBRO_SOCIO, e.getMessage().replace("Error: ", ""));
        }
    }

    private static void testAmpliadoLibrosMenosPrestados() throws Exception {
        LinkedHashMap<Libro, Integer> mapa = AccesoLibro.consultarMenorLibroPrestado();

        Integer veces = null;
        for (Libro libro : mapa.keySet()) {
            if (libro.getCodigo() == libroLibreId) {
                veces = mapa.get(libro);
                break;
            }
        }

        if (veces == null) {
            throw new AssertionError("el libro sin prestamos no aparece como menos prestado");
        }

        assertEquals(0, veces);
    }

    private static void testAmpliadoSociosMasPrestamos() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.sociosMasPrestamos();
        List<Socio> propios = filterSociosByMarker(socios);

        assertEquals(1, propios.size());
        assertEquals(socioTopDni, propios.get(0).getDni());
    }

    private static void testAmpliadoLibrosBajoMedia() throws Exception {
        LinkedHashMap<Libro, Integer> mapa = AccesoLibro.consultarLibroPrestadoInferiorMedia();
        boolean contieneLibre = false;
        boolean contieneTop = false;
        boolean contieneBajo = false;
        boolean contieneMedio = false;

        for (Libro libro : mapa.keySet()) {
            if (libro.getCodigo() == libroLibreId) {
                contieneLibre = true;
            }
            if (libro.getCodigo() == libroTopId) {
                contieneTop = true;
            }
            if (libro.getCodigo() == libroBajoId) {
                contieneBajo = true;
            }
            if (libro.getCodigo() == libroMedioId) {
                contieneMedio = true;
            }
        }

        assertTrue(contieneLibre);
        assertFalse(contieneTop);
        assertFalse(contieneBajo);
        assertFalse(contieneMedio);
    }

    private static void testAmpliadoSociosSobreMedia() throws Exception {
        ArrayList<Socio> socios = AccesoSocio.sociosSobreMediaPrestamos();
        List<Socio> propios = filterSociosByMarker(socios);
        boolean contieneTop = false;
        boolean contieneBajo = false;
        boolean contieneNunca = false;

        for (Socio socio : propios) {
            if (socioTopDni.equals(socio.getDni())) {
                contieneTop = true;
            }
            if (socioBajoDni.equals(socio.getDni())) {
                contieneBajo = true;
            }
            if (socioNuncaDni.equals(socio.getDni())) {
                contieneNunca = true;
            }
        }

        assertTrue(contieneTop);
        assertTrue(contieneBajo);
        assertFalse(contieneNunca);
    }

    private static void testAmpliadoRankingLibros() throws Exception {
        LinkedHashMap<Libro, Integer> ranking = AccesoPrestamo.consultarNumeroDeVecesLibrosPrestados();

        Integer top = findCountByBookTitle(ranking, "Libro Top " + MARKER);
        Integer bajo = findCountByBookTitle(ranking, "Libro Bajo " + MARKER);
        Integer medio = findCountByBookTitle(ranking, "Libro Medio " + MARKER);

        assertEquals(2, top);
        assertEquals(1, bajo);
        assertEquals(1, medio);
    }

    private static void testAmpliadoRankingSocios() throws Exception {
        LinkedHashMap<Socio, Integer> ranking = AccesoPrestamo.consultarNumeroDeVecesPrestamosDeSocios();

        Integer top = findCountBySocioDni(ranking, socioTopDni);
        Integer bajo = findCountBySocioDni(ranking, socioBajoDni);

        assertEquals(3, top);
        assertEquals(1, bajo);
    }

    private static void run(String name, ThrowingRunnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[OK] " + name);
        } catch (Throwable e) {
            failed++;
            System.out.println("[ERROR] " + name + " -> " + e.getMessage());
        }
    }

    private static void cleanupByMarker() throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();

            deleteMatchingPrestamos(connection);
            deleteByLike(connection, "DELETE FROM socio WHERE nombre LIKE ? OR correo LIKE ?", "%" + MARKER + "%", "%" + MARKER + "%");
            deleteByLike(connection, "DELETE FROM libro WHERE titulo LIKE ? OR escritor LIKE ?", "%" + MARKER + "%", "%" + MARKER + "%");
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static void deleteMatchingPrestamos(Connection connection) throws SQLException {
        String sql = """
                DELETE p
                FROM prestamo p
                LEFT JOIN libro l ON l.codigo = p.codigo_libro
                LEFT JOIN socio s ON s.codigo = p.codigo_socio
                WHERE l.titulo LIKE ? OR l.escritor LIKE ? OR s.nombre LIKE ? OR s.correo LIKE ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + MARKER + "%");
        ps.setString(2, "%" + MARKER + "%");
        ps.setString(3, "%" + MARKER + "%");
        ps.setString(4, "%" + MARKER + "%");
        ps.executeUpdate();
        ps.close();
    }

    private static void deleteByLike(Connection connection, String sql, String first, String second) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, first);
        ps.setString(2, second);
        ps.executeUpdate();
        ps.close();
    }

    private static int insertBookDirect(String isbn, String titulo, String escritor, int anio, float puntuacion)
            throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();
            String sql = "INSERT INTO libro (isbn, titulo, escritor, anio_publicacion, puntuacion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, isbn);
            ps.setString(2, titulo);
            ps.setString(3, escritor);
            ps.setInt(4, anio);
            ps.setFloat(5, puntuacion);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                keys.close();
                ps.close();
                return id;
            }

            keys.close();
            ps.close();
            throw new AssertionError("no se obtuvo codigo de libro");
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static int insertSocioDirect(String dni, String nombre, String domicilio, String telefono, String correo)
            throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();
            String sql = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, domicilio);
            ps.setString(4, telefono);
            ps.setString(5, correo);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                keys.close();
                ps.close();
                return id;
            }

            keys.close();
            ps.close();
            throw new AssertionError("no se obtuvo codigo de socio");
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static void insertPrestamoDirect(int codigoLibro, int codigoSocio, String inicio, String fin, String devolucion)
            throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();
            String sql = "INSERT INTO prestamo (codigo_libro, codigo_socio, fecha_inicio, fecha_fin, fecha_devolucion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, codigoLibro);
            ps.setInt(2, codigoSocio);
            ps.setString(3, inicio);
            ps.setString(4, fin);
            ps.setString(5, devolucion);
            ps.executeUpdate();
            ps.close();
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static int findBookCodeByIsbn(String isbn) throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();
            PreparedStatement ps = connection.prepareStatement("SELECT codigo FROM libro WHERE isbn = ?");
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            int result = rs.next() ? rs.getInt("codigo") : -1;
            rs.close();
            ps.close();
            return result;
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static int findSocioCodeByDni(String dni) throws BDException, SQLException {
        Connection connection = null;
        try {
            connection = ConfigMySQL.abrirConexion();
            PreparedStatement ps = connection.prepareStatement("SELECT codigo FROM socio WHERE dni = ?");
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            int result = rs.next() ? rs.getInt("codigo") : -1;
            rs.close();
            ps.close();
            return result;
        } finally {
            if (connection != null) {
                ConfigMySQL.cerrarConexion(connection);
            }
        }
    }

    private static List<Libro> filterLibrosByMarker(List<Libro> libros) {
        ArrayList<Libro> filtered = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getTitulo() != null && libro.getTitulo().contains(MARKER)) {
                filtered.add(libro);
            }
        }
        return filtered;
    }

    private static List<Socio> filterSociosByMarker(List<Socio> socios) {
        ArrayList<Socio> filtered = new ArrayList<>();
        for (Socio socio : socios) {
            if (socio.getNombre() != null && socio.getNombre().contains(MARKER)) {
                filtered.add(socio);
            }
        }
        return filtered;
    }

    private static void assertContainsBook(List<Libro> libros, int codigoLibro) {
        for (Libro libro : libros) {
            if (libro.getCodigo() == codigoLibro) {
                return;
            }
        }
        throw new AssertionError("no se encontro el libro con codigo " + codigoLibro);
    }

    private static void assertContainsSocio(List<Socio> socios, String dni) {
        for (Socio socio : socios) {
            if (dni.equals(socio.getDni())) {
                return;
            }
        }
        throw new AssertionError("no se encontro el socio con dni " + dni);
    }

    private static void assertContainsPrestamo(List<Prestamo> prestamos, int codigoLibro, int codigoSocio, String fechaInicio) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getCodigoLibro() == codigoLibro
                    && prestamo.getCodigoSocio() == codigoSocio
                    && fechaInicio.equals(prestamo.getFechaInicio())) {
                return;
            }
        }
        throw new AssertionError("no se encontro el prestamo esperado");
    }

    private static void assertNotContainsPrestamo(List<Prestamo> prestamos, int codigoLibro, int codigoSocio, String fechaInicio) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getCodigoLibro() == codigoLibro
                    && prestamo.getCodigoSocio() == codigoSocio
                    && fechaInicio.equals(prestamo.getFechaInicio())) {
                throw new AssertionError("el prestamo no deberia existir");
            }
        }
    }

    private static boolean anyLineContains(List<String> lines, String expected) {
        for (String line : lines) {
            if (line != null && line.contains(expected)) {
                return true;
            }
        }
        return false;
    }

    private static Integer findCountByBookTitle(LinkedHashMap<Libro, Integer> ranking, String title) {
        for (Libro libro : ranking.keySet()) {
            if (title.equals(libro.getTitulo())) {
                return ranking.get(libro);
            }
        }
        return null;
    }

    private static Integer findCountBySocioDni(LinkedHashMap<Socio, Integer> ranking, String dni) {
        for (Socio socio : ranking.keySet()) {
            if (dni.equals(socio.getDni())) {
                return ranking.get(socio);
            }
        }
        return null;
    }

    private static String buildValidIsbn13(int sequence) {
        String base = String.format("978000000%03d", sequence);
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            int digit = base.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = (10 - (sum % 10)) % 10;
        return base + checksum;
    }

    private static String buildValidDni(int numero) {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return String.format("%08d", numero) + letras.charAt(numero % 23);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("se esperaba true");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("se esperaba false");
        }
    }

    private static void assertEquals(int expected, Integer actual) {
        if (actual == null || expected != actual) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
