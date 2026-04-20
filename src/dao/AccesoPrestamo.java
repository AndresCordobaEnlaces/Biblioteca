package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import config.ConfigMySQL;
import exceptions.BDException;
import exceptions.PrestamosException;
import models.Prestamo;

/**
 * 
 * AccesoPrestamo
 * 
 * @author AndresCordoba
 */
public class AccesoPrestamo {

	/**
	 * Metodo que omprueba si un libro tiene un prestamo activo
	 * 
	 * @param codigoLibro
	 * @return
	 * @throws BDException
	 */
	private static boolean estaLibroPrestado(int codigoLibro) throws BDException {
		Connection conexion = null;
		PreparedStatement ps = null;
		boolean existe = false;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "select * from prestamo where codigo_libro = ? and fecha_devolucion is null";
			ps = conexion.prepareStatement(query);
			ps.setInt(1, codigoLibro);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
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
	 * Comprueba si un socio tiene actualmente algun libro prestado
	 * 
	 * @param codigoSocio
	 * @return
	 * @throws BDException
	 */
	private static boolean tieneLibroPrestado(int codigoSocio) throws BDException {
		Connection conexion = null;
		PreparedStatement ps = null;
		boolean existe = false;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "select * from prestamo where codigo_socio = ? and fecha_devolucion is null";
			ps = conexion.prepareStatement(query);
			ps.setInt(1, codigoSocio);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
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
	 * Inserta un nuevo prestamo en la base de datos si el libro y el socio cumplen
	 * las condiciones
	 * 
	 * @param codigoLibro
	 * @param codigoSocio
	 * @param fechaInicio
	 * @param fechaFin
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static boolean insertarPrestamo(int codigoLibro, int codigoSocio, String fechaInicio, String fechaFin)
			throws BDException, PrestamosException {

		Connection conexion = null;
		PreparedStatement ps = null;
		int filas = 0;

		try {
			if (estaLibroPrestado(codigoLibro)) {
				throw new PrestamosException(PrestamosException.ESTA_PRESTADO);
			}

			if (tieneLibroPrestado(codigoSocio)) {
				throw new PrestamosException(PrestamosException.TIENE_PRESTADO);
			}

			conexion = ConfigMySQL.abrirConexion();

			String query = "INSERT INTO prestamo "
					+ "(codigo_libro, codigo_socio, fecha_inicio, fecha_fin, fecha_devolucion) "
					+ "VALUES (?, ?, ?, ?, null)";

			ps = conexion.prepareStatement(query);
			ps.setInt(1, codigoLibro);
			ps.setInt(2, codigoSocio);
			ps.setString(3, fechaInicio);
			ps.setString(4, fechaFin);

			filas = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PrestamosException(PrestamosException.NO_EXISTE_LIBRO_SOCIO);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return filas == 1;
	}

	/**
	 * Actualiza la fecha de devolución de un prestamo
	 * 
	 * @param codigoLibro
	 * @param codigoSocio
	 * @param fechaInicio
	 * @param fechaDevolucion
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static boolean actualizarPrestamo(int codigoLibro, int codigoSocio, String fechaInicio,
			String fechaDevolucion) throws BDException, PrestamosException {

		Connection conexion = null;
		PreparedStatement ps = null;
		int filas = 0;

		try {
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate inicio = LocalDate.parse(fechaInicio, formato);
			LocalDate devolucion = LocalDate.parse(fechaDevolucion, formato);

			if (devolucion.isBefore(inicio)) {
				throw new PrestamosException(PrestamosException.ERROR_FECHA_DEVOLUCION);
			}

			conexion = ConfigMySQL.abrirConexion();

			String query = "update prestamo set fecha_devolucion = ? where codigo_libro = ? and codigo_socio = ? and fecha_inicio = ?";
			ps = conexion.prepareStatement(query);

			ps.setString(1, fechaDevolucion);
			ps.setInt(2, codigoLibro);
			ps.setInt(3, codigoSocio);
			ps.setString(4, fechaInicio);

			filas = ps.executeUpdate();

			if (filas == 0) {
				throw new PrestamosException(PrestamosException.NO_EXISTE_PRESTAMO);
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
	 * Metodo para eliminar un prestamo
	 * 
	 * @param codigoLibro
	 * @param codigoSocio
	 * @param fechaInicio
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static boolean eliminarPrestamo(int codigoLibro, int codigoSocio, String fechaInicio)
			throws BDException, PrestamosException {

		Connection conexion = null;
		PreparedStatement ps = null;
		int filas = 0;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "delete from prestamo where codigo_libro = ? and codigo_socio = ? and fecha_inicio = ?";
			ps = conexion.prepareStatement(query);

			ps.setInt(1, codigoLibro);
			ps.setInt(2, codigoSocio);
			ps.setString(3, fechaInicio);

			filas = ps.executeUpdate();

			if (filas == 0) {
				throw new PrestamosException(PrestamosException.NO_EXISTE_PRESTAMO);
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
	 * Consulta y devuelve todos los prestamos almacenados en la base de datos
	 * 
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static ArrayList<Prestamo> consultarTodosPrestamos() throws BDException, PrestamosException {
		ArrayList<Prestamo> prestamos = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "select * from prestamo";
			PreparedStatement ps = conexion.prepareStatement(query);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Prestamo prestamo = new Prestamo(rs.getInt("codigo_libro"), rs.getInt("codigo_socio"),
						rs.getString("fecha_inicio"), rs.getString("fecha_devolucion"));
				prestamos.add(prestamo);
			}

			if (prestamos.isEmpty()) {
				throw new PrestamosException(PrestamosException.ERROR_PRESTAMOS_BD_VACIA);
			}
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return prestamos;
	}

	/**
	 * Consulta y devuelve todos los prestamos que todavia no han sido devueltos
	 * 
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static ArrayList<Prestamo> consultarLosPrestamosNoDevueltos() throws BDException, PrestamosException {
		ArrayList<Prestamo> prestamos = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "select * from prestamo where fecha_devolucion is null";
			PreparedStatement ps = conexion.prepareStatement(query);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Prestamo prestamo = new Prestamo(rs.getInt("codigo_libro"), rs.getInt("codigo_socio"),
						rs.getString("fecha_inicio"), rs.getString("fecha_devolucion"));
				prestamos.add(prestamo);
			}

			if (prestamos.isEmpty()) {
				throw new PrestamosException(PrestamosException.ERROR_PRESTAMOS_NO_DEVUELTOS);
			}
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return prestamos;
	}

	/**
	 * Consulta los prestamos realizados en una fecha concreta y muestra sus datos
	 * junto con la devolucion
	 * 
	 * @param fechaInicio
	 * @return
	 * @throws BDException
	 * @throws PrestamosException
	 */
	public static ArrayList<String> consultarPrestamosConFechaDevolucion(String fechaInicio)
			throws BDException, PrestamosException {

		ArrayList<String> prestamos = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "select s.dni, s.nombre, l.isbn, l.titulo, p.fecha_devolucion " + "from prestamo p "
					+ "join socio s on p.codigo_socio = s.codigo " + "join libro l on p.codigo_libro = l.codigo "
					+ "where p.fecha_inicio = ?";

			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setString(1, fechaInicio);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String linea = "DNI: " + rs.getString("dni") + ", Nombre: " + rs.getString("nombre") + ", ISBN: "
						+ rs.getString("isbn") + ", Título: " + rs.getString("titulo") + ", Fecha devolución: "
						+ rs.getString("fecha_devolucion");
				prestamos.add(linea);
			}

			if (prestamos.isEmpty()) {
				throw new PrestamosException(PrestamosException.ERROR_PRESTAMOS_FECHA);
			}
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return prestamos;
	}
}