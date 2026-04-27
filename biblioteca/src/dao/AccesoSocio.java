package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import config.ConfigMySQL;
import exceptions.BDException;
import exceptions.SocioException;
import models.Socio;

public class AccesoSocio {

	/**
	 * Comprueba si un socio tiene préstamos.
	 */
	private static boolean tienePrestamos(int codigo) throws BDException {
		Connection conexion = null;
		PreparedStatement ps = null;
		boolean existe = false;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT * FROM prestamo WHERE codigo_socio = ?";
			ps = conexion.prepareStatement(query);
			ps.setInt(1, codigo);

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
	 * INSERTAR
	 */
	public static boolean insertarSocio(String dni, String nombre, String domicilio, String telefono, String correo)
			throws BDException {
		Connection conexion = null;
		PreparedStatement ps = null;
		int filas = 0;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";
			ps = conexion.prepareStatement(query);

			ps.setString(1, dni);
			ps.setString(2, nombre);
			ps.setString(3, domicilio);
			ps.setString(4, telefono);
			ps.setString(5, correo);

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
	 * ELIMINAR
	 */
	public static boolean eliminarSocio(int codigo) throws BDException, SocioException {
		Connection conexion = null;
		PreparedStatement ps = null;
		int filas = 0;

		try {
			if (tienePrestamos(codigo)) {
				throw new SocioException(SocioException.ERROR_SOCIO_PRESTAMO);
			}

			conexion = ConfigMySQL.abrirConexion();

			String query = "DELETE FROM socio WHERE codigo = ?";
			ps = conexion.prepareStatement(query);
			ps.setInt(1, codigo);

			filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SocioException(SocioException.ERROR_SOCIO_NOEXISTE);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return true;
	}

	/**
	 * CONSULTAR TODOS
	 */
	public static ArrayList<Socio> consultarSocios() throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT * FROM socio";
			PreparedStatement ps = conexion.prepareStatement(query);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_BDEmpty);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * POR LOCALIDAD
	 */
	public static ArrayList<Socio> consultarPorLocalidad(String localidad) throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT * FROM socio WHERE domicilio LIKE ? ORDER BY nombre";
			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setString(1, "%" + localidad + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_LOCALIDAD);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * SIN PRÉSTAMOS
	 */
	public static ArrayList<Socio> sociosSinPrestamos() throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT * FROM socio s WHERE NOT EXISTS "
					+ "(SELECT 1 FROM prestamo p WHERE p.codigo_socio = s.codigo)";

			PreparedStatement ps = conexion.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_SINPRESTAMOS);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * POR FECHA
	 */
	public static ArrayList<Socio> sociosPorFecha(String fecha) throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT DISTINCT s.* FROM socio s " + "JOIN prestamo p ON s.codigo = p.codigo_socio "
					+ "WHERE p.fecha_inicio = ?";

			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setString(1, fecha);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_FECHA);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * POR CÓDIGO
	 */
	public static Socio consultarSocioPorCodigo(int codigo) throws BDException {
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = "SELECT * FROM socio WHERE codigo = ?";
			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setInt(1, codigo);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo"));
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return null;
	}

	/**
	 * SOCIOS SOBRE LA MEDIA DE PRÉSTAMOS
	 */
	public static ArrayList<Socio> sociosSobreMediaPrestamos() throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = """
					SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo
					FROM socio s
					LEFT JOIN prestamo p ON p.codigo_socio = s.codigo
					GROUP BY s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo
					HAVING COUNT(p.codigo_socio) > (
					    SELECT AVG(conteo)
					    FROM (
					        SELECT COUNT(p2.codigo_socio) AS conteo
					        FROM socio s2
					        LEFT JOIN prestamo p2 ON p2.codigo_socio = s2.codigo
					        GROUP BY s2.codigo
					    ) AS subconsulta
					)
					""";

			PreparedStatement ps = conexion.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_SOBRE_MEDIA);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * SOCIOS CON MÁS PRÉSTAMOS
	 */
	public static ArrayList<Socio> sociosMasPrestamos() throws BDException, SocioException {
		ArrayList<Socio> lista = new ArrayList<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = """
					SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo
					FROM prestamo p
					JOIN socio s ON p.codigo_socio = s.codigo
					GROUP BY s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo
					HAVING COUNT(*) = (
					    SELECT MAX(conteo)
					    FROM (
					        SELECT COUNT(*) AS conteo
					        FROM prestamo
					        GROUP BY codigo_socio
					    ) AS subconsulta
					)
					""";

			PreparedStatement ps = conexion.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo")));
			}

			if (lista.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_NO_TIENEN_PRESTAMO);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return lista;
	}

	/**
	 * RANKING SOCIOS POR PRÉSTAMOS DESC
	 */
	public static LinkedHashMap<Socio, Integer> rankingSociosPrestamos() throws BDException, SocioException {
		LinkedHashMap<Socio, Integer> mapa = new LinkedHashMap<>();
		Connection conexion = null;

		try {
			conexion = ConfigMySQL.abrirConexion();

			String query = """
					SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo,
					       COUNT(*) AS total
					FROM prestamo p
					JOIN socio s ON p.codigo_socio = s.codigo
					GROUP BY s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo
					ORDER BY total DESC
					""";

			PreparedStatement ps = conexion.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Socio s = new Socio(rs.getInt("codigo"), rs.getString("dni"), rs.getString("nombre"),
						rs.getString("domicilio"), rs.getString("telefono"), rs.getString("correo"));
				mapa.put(s, rs.getInt("total"));
			}

			if (mapa.isEmpty()) {
				throw new SocioException(SocioException.ERROR_SOCIO_NO_TIENEN_PRESTAMO);
			}

		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_QUERY + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySQL.cerrarConexion(conexion);
			}
		}

		return mapa;
	}

}
