package regex;

import java.time.LocalDate;

public class FuncionesRegex {

	public static boolean correoBien(String correo) {
		return correo != null && correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
	}

	public static boolean fechaBien(String fecha) {
		if (fecha == null)
			return false;

		try {
			LocalDate.parse(fecha);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean anyoBien(int anyo) {
		int anyoActual = LocalDate.now().getYear();
		return anyo >= 1 && anyo <= anyoActual;
	}

	public static boolean isbnBien(String isbn) {
		if (isbn == null)
			return false;

		isbn = isbn.replace("-", "");

		if (isbn.matches("\\d{13}")) {
			return isbn.startsWith("978") || isbn.startsWith("979");
		}

		if (isbn.matches("\\d{9}[0-9X]")) {
			int suma = 0;

			for (int i = 0; i < 9; i++) {
				suma += (isbn.charAt(i) - '0') * (10 - i);
			}

			char ultimo = isbn.charAt(9);
			suma += (ultimo == 'X') ? 10 : (ultimo - '0');

			return suma % 11 == 0;
		}

		return false;
	}

	public static boolean dniBien(String dni) {
		if (dni == null)
			return false;

		String d = dni.trim().toUpperCase();
		if (d.length() != 9)
			return false;

		String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
		char letra = d.charAt(8);

		String numeros;

		if (Character.isDigit(d.charAt(0))) {
			if (!d.substring(0, 8).matches("\\d{8}"))
				return false;
			numeros = d.substring(0, 8);
		} else {
			if (!d.substring(1, 8).matches("\\d{7}"))
				return false;

			char pref = d.charAt(0);
			int valor = (pref == 'X') ? 0 : (pref == 'Y') ? 1 : (pref == 'Z') ? 2 : -1;
			if (valor == -1)
				return false;

			numeros = valor + d.substring(1, 8);
		}

		int num = Integer.parseInt(numeros);
		return letra == letras.charAt(num % 23);
	}

	public static boolean telefonoBien(String telefono) {
		return telefono != null && telefono.matches("^[6-9]\\d{8}$");
	}
}