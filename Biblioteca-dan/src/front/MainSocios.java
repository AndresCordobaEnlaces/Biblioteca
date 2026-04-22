package front;

import dao.AccesoSocio;
import entrada.Teclado;
import exceptions.BDException;
import exceptions.SocioException;
import models.Socio;

import java.util.ArrayList;

public class MainSocios {
	
	public static void main(String[] args) {
		menuSocios();
	}
	
	public static void escribirMenuOpcionesSocio() {
		System.out.println();
		System.out.println("Elige una opción del menu de Socio");
		System.out.println("0) Salir del programa.");
		System.out.println("1) Insertar un socio en la base de datos");
		System.out.println("2) Eliminar un socio por código");
		System.out.println("3) Consultar todos los socios");
		System.out.println("4) Consultar socios por localidad");
		System.out.println("5) Consultar socios sin préstamos");
		System.out.println("6) Consultar socios con préstamos en una fecha");
	}
	
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
}
