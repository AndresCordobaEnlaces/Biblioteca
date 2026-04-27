package tests;

import models.Libro;
import models.Prestamo;
import models.Socio;
import regex.FuncionesRegex;

public class ManualTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("correo valido", () -> assertTrue(FuncionesRegex.correoBien("ana.lopez@example.com")));
        run("correo valido con plus", () -> assertTrue(FuncionesRegex.correoBien("ana+test@example.com")));
        run("correo invalido sin dominio", () -> assertFalse(FuncionesRegex.correoBien("ana.lopez@")));
        run("correo invalido con doble punto", () -> assertFalse(FuncionesRegex.correoBien("ana..lopez@example.com")));
        run("fecha valida", () -> assertTrue(FuncionesRegex.fechaBien("2026-04-27")));
        run("fecha invalida", () -> assertFalse(FuncionesRegex.fechaBien("2026-02-30")));
        run("anyo futuro invalido", () -> assertFalse(FuncionesRegex.anyoBien(3000)));
        run("isbn10 valido", () -> assertTrue(FuncionesRegex.isbnBien("0306406152")));
        run("isbn10 valido con x minuscula", () -> assertTrue(FuncionesRegex.isbnBien("097522980x")));
        run("isbn13 valido", () -> assertTrue(FuncionesRegex.isbnBien("9780306406157")));
        run("isbn13 con checksum invalido", () -> assertFalse(FuncionesRegex.isbnBien("9780306406158")));
        run("dni valido", () -> assertTrue(FuncionesRegex.dniBien("12345678Z")));
        run("dni invalido", () -> assertFalse(FuncionesRegex.dniBien("12345678A")));
        run("telefono valido", () -> assertTrue(FuncionesRegex.telefonoBien("612345678")));
        run("telefono invalido", () -> assertFalse(FuncionesRegex.telefonoBien("512345678")));
        run("libros con mismo isbn son iguales", ManualTestRunner::testLibroEqualsPorIsbn);
        run("libro toString sin texto corrupto", ManualTestRunner::testLibroToStringLegible);
        run("prestamo muestra pendiente si no hay devolucion", ManualTestRunner::testPrestamoToStringPendiente);
        run("socio toString sin texto corrupto", ManualTestRunner::testSocioToStringLegible);

        System.out.println();
        System.out.println("Pruebas superadas: " + passed);
        System.out.println("Pruebas fallidas: " + failed);

        if (failed > 0) {
            throw new AssertionError("Hay pruebas fallidas");
        }
    }

    private static void testLibroEqualsPorIsbn() {
        Libro primero = new Libro(1, "9780306406157", "Titulo A", "Autor A", 2001, 7.5f);
        Libro segundo = new Libro(2, "9780306406157", "Titulo B", "Autor B", 2005, 8.0f);

        assertTrue(primero.equals(segundo));
        assertEquals(primero.hashCode(), segundo.hashCode());
    }

    private static void testPrestamoToStringPendiente() {
        Prestamo prestamo = new Prestamo(10, 20, "2026-04-20", null);
        assertTrue(prestamo.toString().contains("Pendiente"));
    }

    private static void testLibroToStringLegible() {
        Libro libro = new Libro(1, "9780306406157", "Titulo", "Autor", 2001, 7.5f);
        String texto = libro.toString();

        assertTrue(texto.contains("Codigo"));
        assertTrue(texto.contains("Titulo"));
        assertFalse(texto.contains("Ã"));
    }

    private static void testSocioToStringLegible() {
        Socio socio = new Socio(1, "12345678Z", "Ana", "Madrid", "612345678", "ana@mail.com");
        String texto = socio.toString();

        assertTrue(texto.contains("Codigo"));
        assertTrue(texto.contains("Telefono"));
        assertFalse(texto.contains("Ã"));
    }

    private static void run(String nombre, Runnable prueba) {
        try {
            prueba.run();
            passed++;
            System.out.println("[OK] " + nombre);
        } catch (AssertionError e) {
            failed++;
            System.out.println("[ERROR] " + nombre + " -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[ERROR] " + nombre + " -> excepcion no controlada: " + e.getMessage());
        }
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

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }
}
