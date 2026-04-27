package dao;

import config.ConfigMySQL;
import exceptions.BDException;
import exceptions.LibroException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import models.Libro;
import regex.FuncionesRegex;

public class AccesoLibro {

    private static boolean textoConContenido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Limpia el ISBN para compararlo y guardarlo siempre igual.
     *
     * @author Dan Bolocan
     */
    private static String normalizarIsbn(String isbn) {
        return isbn == null ? null : isbn.replace("-", "").replace(" ", "").trim().toUpperCase();
    }

    /**
     * Devuelve true si el libro tiene prestamos asociados.
     *
     * @author Dan Bolocan
     */
    private static boolean esPrestatario(int codigo) throws BDException {
        PreparedStatement ps = null;
        Connection conexion = null;
        boolean existe = false;

        try {
            conexion = ConfigMySQL.abrirConexion();
            String query =
                    "select * from libro join prestamo on (libro.codigo = prestamo.codigo_libro) where libro.codigo = ?";

            ps = conexion.prepareStatement(query);
            ps.setInt(1, codigo);

            ResultSet resultados = ps.executeQuery();

            if (resultados.next()) {
                existe = true;
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return existe;
    }

    /**
     * Devuelve true si el ISBN ya existe.
     *
     * @author Dan Bolocan
     */
    private static boolean existeISBN(String isbn) throws BDException {
        PreparedStatement ps = null;
        Connection conexion = null;
        boolean existe = false;
        String isbnNormalizado = normalizarIsbn(isbn);

        try {
            conexion = ConfigMySQL.abrirConexion();
            String query = "select * from libro where isbn = ?;";

            ps = conexion.prepareStatement(query);
            ps.setString(1, isbnNormalizado);

            ResultSet resultados = ps.executeQuery();

            if (resultados.next()) {
                existe = true;
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return existe;
    }

    /**
     * Inserta un libro nuevo en la tabla libro.
     *
     * @author Dan Bolocan
     */
    public static boolean anadirLibro(
            String isbn,
            String titulo,
            String escritor,
            int anyo_publicacion,
            float puntuacion
    ) throws BDException, LibroException {
        PreparedStatement ps = null;
        Connection conexion = null;
        String isbnNormalizado = normalizarIsbn(isbn);

        int filas = 0;

        try {
            if (!FuncionesRegex.isbnBien(isbnNormalizado)) {
                throw new LibroException(LibroException.ERROR_LIBRO_ISBNINVALIDO);
            }

            if (!textoConContenido(titulo)) {
                throw new LibroException(LibroException.ERROR_LIBRO_TITULOVACIO);
            }

            if (!textoConContenido(escritor)) {
                throw new LibroException(LibroException.ERROR_LIBRO_ESCRITORVACIO);
            }

            if (!FuncionesRegex.anyoBien(anyo_publicacion)) {
                throw new LibroException(LibroException.ERROR_LIBRO_ANYOINVALIDO);
            }

            if (puntuacion < 0 || puntuacion > 10) {
                throw new LibroException(LibroException.ERROR_LIBRO_PUNTUACIONINVALIDA);
            }

            conexion = ConfigMySQL.abrirConexion();

            if (existeISBN(isbnNormalizado)) {
                throw new LibroException(LibroException.ERROR_LIBRO_ISBNEXISTE);
            }

            String query =
                    "insert into libro (isbn, titulo, escritor, anio_publicacion, puntuacion ) VALUES (?, ?, ?, ?, ?);";

            ps = conexion.prepareStatement(query);

            ps.setString(1, isbnNormalizado);
            ps.setString(2, titulo);
            ps.setString(3, escritor);
            ps.setInt(4, anyo_publicacion);
            ps.setFloat(5, puntuacion);

            filas = ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }

        return filas == 1;
    }

    /**
     * Elimina un libro por su codigo.
     *
     * @author Dan Bolocan
     */
    public static boolean borrarLibroPorCodigo(int codigo) throws BDException, LibroException {
        PreparedStatement ps = null;
        Connection conexion = null;

        int filas = 0;

        try {
            if (esPrestatario(codigo)) {
                throw new LibroException(LibroException.ERROR_LIBRO_PRESTAMO);
            }

            conexion = ConfigMySQL.abrirConexion();
            String query = "delete from libro where codigo = ?;";

            ps = conexion.prepareStatement(query);
            ps.setInt(1, codigo);

            filas = ps.executeUpdate();

            if (filas == 0) {
                throw new LibroException(LibroException.ERROR_NOLIBRO);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }

        return filas == 1;
    }

    /**
     * Consulta todos los libros guardados.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarLibros() throws BDException, LibroException {
        ArrayList<Libro> listaLibros = new ArrayList<Libro>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();
            String query = "select * from libro;";

            ps = conexion.prepareStatement(query);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor, anyo_publicacion, puntuacion);

                listaLibros.add(libro);
            }
            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_BDEmpty);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Busca libros por escritor y los ordena por puntuacion descendente.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarLibrosOrdenados(String escritor) throws BDException, LibroException {
        escritor = escritor.toLowerCase();

        ArrayList<Libro> listaLibros = new ArrayList<Libro>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();
            String query = "select * from libro where lower(escritor) like ? order by puntuacion desc;";

            ps = conexion.prepareStatement(query);
            ps.setString(1, "%" + escritor + "%");

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor_libro = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor_libro, anyo_publicacion, puntuacion);

                listaLibros.add(libro);
            }
            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_NOESCRITOR);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Consulta los libros que nunca se han prestado.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarLibrosNoPrestados() throws BDException, LibroException {
        ArrayList<Libro> listaLibros = new ArrayList<Libro>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();
            String query =
                    "select distinct l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion from libro l left join prestamo p on l.codigo = p.codigo_libro where p.codigo_libro is null;";

            ps = conexion.prepareStatement(query);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor_libro = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor_libro, anyo_publicacion, puntuacion);

                listaLibros.add(libro);
            }
            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_NOPRESTADO);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Consulta libros devueltos en una fecha concreta.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarLibrosDevueltos(String fecha_devolucion) throws BDException, LibroException {
        ArrayList<Libro> listaLibros = new ArrayList<Libro>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();

            String query =
                    "select l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion from libro l left join prestamo p on l.codigo = p.codigo_libro where p.fecha_devolucion = ?;";

            ps = conexion.prepareStatement(query);
            ps.setString(1, fecha_devolucion);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor_libro = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor_libro, anyo_publicacion, puntuacion);

                listaLibros.add(libro);
            }

            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_NODEVUELTO);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Consulta un libro por codigo.
     *
     * @author Dan Bolocan
     */
    public static Libro consultarLibroPorCodigo(int codigoLibro) throws BDException {
        Libro libro = null;

        Connection conexion = null;
        try {
            conexion = ConfigMySQL.abrirConexion();

            String query = "select * from libro where codigo = ?";
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setInt(1, codigoLibro);

            ResultSet resultados = ps.executeQuery();

            if (resultados.next()) {
                libro = new Libro(
                        codigoLibro,
                        resultados.getString("isbn"),
                        resultados.getString("titulo"),
                        resultados.getString("escritor"),
                        resultados.getInt("anio_publicacion"),
                        resultados.getFloat("puntuacion")
                );
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }

        return libro;
    }

    /**
     * Consulta el libro o libros menos prestados.
     *
     * @author Dan Bolocan
     */
    public static LinkedHashMap<Libro, Integer> consultarMenorLibroPrestado() throws BDException, LibroException {
        LinkedHashMap<Libro, Integer> listaLibros = new LinkedHashMap<Libro, Integer>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();

            String query =
                    "select l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion, count(p.codigo_libro) as veces_prestado " +
                            "from libro l left join prestamo p on l.codigo = p.codigo_libro " +
                            "group by l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion " +
                            "having count(p.codigo_libro) = (select min(cantidad) " +
                            "from (select count(p2.codigo_libro) as cantidad " +
                            "from libro l2 left join prestamo p2 on l2.codigo = p2.codigo_libro " +
                            "group by l2.codigo) as prestamo_count) " +
                            "order by veces_prestado ASC;";

            ps = conexion.prepareStatement(query);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor_libro = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor_libro, anyo_publicacion, puntuacion);
                int vecesPrestado = resultados.getInt("veces_prestado");

                listaLibros.put(libro, vecesPrestado);
            }

            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_BDEmpty);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Consulta libros prestados menos veces que la media.
     *
     * @author Dan Bolocan
     */
    public static LinkedHashMap<Libro, Integer> consultarLibroPrestadoInferiorMedia() throws BDException, LibroException {
        LinkedHashMap<Libro, Integer> listaLibros = new LinkedHashMap<Libro, Integer>();

        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySQL.abrirConexion();

            String query =
                    "select l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion, count(p.codigo_libro) as veces_prestado " +
                            "from libro l left join prestamo p on l.codigo = p.codigo_libro " +
                            "group by l.codigo, l.isbn, l.titulo, l.escritor, l.anio_publicacion, l.puntuacion " +
                            "having count(p.codigo_libro) < (select avg(cantidad) " +
                            "from (select count(p2.codigo_libro) as cantidad " +
                            "from libro l2 left join prestamo p2 on l2.codigo = p2.codigo_libro " +
                            "group by l2.codigo) as prestamo_count);";

            ps = conexion.prepareStatement(query);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int codigo = resultados.getInt("codigo");
                String isbn = resultados.getString("isbn");
                String titulo = resultados.getString("titulo");
                String escritor_libro = resultados.getString("escritor");
                int anyo_publicacion = resultados.getInt("anio_publicacion");
                float puntuacion = resultados.getFloat("puntuacion");

                Libro libro = new Libro(codigo, isbn, titulo, escritor_libro, anyo_publicacion, puntuacion);
                int vecesPrestado = resultados.getInt("veces_prestado");

                listaLibros.put(libro, vecesPrestado);
            }

            if (listaLibros.isEmpty()) {
                throw new LibroException(LibroException.ERROR_LIBRO_BAJO_MEDIA);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySQL.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }
}
