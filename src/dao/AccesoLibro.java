package dao;

import config.ConfigMySQL;
import exceptions.BDException;
import exceptions.LibroException;
import models.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccesoLibro {

    /**
     * Limpia el ISBN para compararlo y guardarlo siempre igual.
     *
     * @author Dan Bolocan
     */
    private static String normalizarIsbn(String isbn) {
        return isbn == null ? null : isbn.replace("-", "").replace(" ", "").trim().toUpperCase();
    }

    /**
     * Abre conexión a la base de datos.
     *
     * @author Dan Bolocan
     */
    private static Connection conexion() throws BDException {
        return ConfigMySQL.abrirConexion();
    }

    /**
     * Comprueba si un ISBN ya existe.
     *
     * @author Dan Bolocan
     */
    private static boolean existeISBN(String isbn) throws BDException {

        String sql = "select 1 from libro where isbn = ?";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, normalizarIsbn(isbn));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Comprueba si un libro tiene préstamos.
     *
     * @author Dan Bolocan
     */
    private static boolean tienePrestamos(int codigo) throws BDException {

        String sql = "select 1 from prestamo where codigo_libro = ?";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Inserta un libro en la base de datos.
     *
     * @author Dan Bolocan
     */
    public static boolean anadirLibro(String isbn, String titulo, String escritor,
                                      int anyo_publicacion, float puntuacion)
            throws BDException, LibroException {

        if (existeISBN(isbn)) {
            throw new LibroException(LibroException.ERROR_LIBRO_ISBNEXISTE);
        }

        String sql =
                "insert into libro (isbn, titulo, escritor, anio_publicacion, puntuacion) values (?, ?, ?, ?, ?)";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, normalizarIsbn(isbn));
            ps.setString(2, titulo);
            ps.setString(3, escritor);
            ps.setInt(4, anyo_publicacion);
            ps.setFloat(5, puntuacion);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Borra un libro por código.
     *
     * @author Dan Bolocan
     */
    public static boolean borrarLibroPorCodigo(int codigo) throws BDException, LibroException {

        if (tienePrestamos(codigo)) {
            throw new LibroException(LibroException.ERROR_LIBRO_PRESTAMO);
        }

        String sql = "delete from libro where codigo = ?";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new LibroException(LibroException.ERROR_NOLIBRO);
            }

            return true;

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Consulta todos los libros.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarLibros() throws BDException, LibroException {

        ArrayList<Libro> lista = new ArrayList<>();

        String sql = "select * from libro";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapLibro(rs));
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        if (lista.isEmpty()) {
            throw new LibroException(LibroException.ERROR_LIBRO_BDEmpty);
        }

        return lista;
    }

    /**
     * Consulta libros por escritor ordenados por puntuación.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarPorEscritor(String escritor)
            throws BDException, LibroException {

        ArrayList<Libro> lista = new ArrayList<>();

        String sql = "select * from libro where lower(escritor) like ? order by puntuacion desc";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + escritor.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapLibro(rs));
                }
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        if (lista.isEmpty()) {
            throw new LibroException(LibroException.ERROR_LIBRO_NOESCRITOR);
        }

        return lista;
    }

    /**
     * Consulta libros sin préstamos.
     *
     * @author Dan Bolocan
     */
    public static ArrayList<Libro> consultarSinPrestamos()
            throws BDException, LibroException {

        ArrayList<Libro> lista = new ArrayList<>();

        String sql =
                "select l.* from libro l " +
                        "left join prestamo p on l.codigo = p.codigo_libro " +
                        "where p.codigo_libro is null";

        try (Connection con = conexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapLibro(rs));
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        if (lista.isEmpty()) {
            throw new LibroException(LibroException.ERROR_LIBRO_NOPRESTADO);
        }

        return lista;
    }

    /**
     * Mapea ResultSet a Libro.
     *
     * @author Dan Bolocan
     */
    private static Libro mapLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("codigo"),
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("escritor"),
                rs.getInt("anio_publicacion"),
                rs.getFloat("puntuacion")
        );
    }
}