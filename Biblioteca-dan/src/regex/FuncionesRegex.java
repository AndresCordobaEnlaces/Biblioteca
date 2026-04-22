package regex;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncionesRegex {

    /**
     * Valida si el dia existe para el mes y anyo dados.
     *
     * @author Dan Bolocan
     */
    private static boolean esDiaValido(int anyo, int mes, int dia) {
        int[] diasPorMes = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        if (esAnyoBisiesto(anyo)) {
            diasPorMes[1] = 29;
        }

        return dia >= 1 && dia <= diasPorMes[mes - 1];
    }

    /**
     * Devuelve true si el anyo es bisiesto.
     *
     * @author Dan Bolocan
     */
    private static boolean esAnyoBisiesto(int anyo) {
        return (anyo % 4 == 0 && (anyo % 100 != 0 || anyo % 400 == 0));
    }

    /**
     * Valida el formato de un correo electronico.
     *
     * @author Dan Bolocan
     */
    public static boolean correoBien(String correo) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    /**
     * Valida una fecha con formato yyyy-mm-dd.
     *
     * @author Dan Bolocan
     */
    public static boolean fechaBien(String fecha) {
        String regex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fecha);
        if (!matcher.matches()) {
            return false;
        }

        String[] partes = fecha.split("-");
        int anyo = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int dia = Integer.parseInt(partes[2]);

        if (!esDiaValido(anyo, mes, dia)) {
            return false;
        }

        return true;
    }

    /**
     * Valida que el anyo no sea futuro y sea mayor que cero.
     *
     * @author Dan Bolocan
     */
    public static boolean anyoBien(int anyo) {
        int anyoActual = LocalDate.now().getYear();

        return (anyo >= 1 && anyo <= anyoActual);
    }

    /**
     * Valida un ISBN-10 o ISBN-13.
     *
     * @author Dan Bolocan
     */
    public static boolean isbnBien(String isbn) {
        isbn = isbn.replace("-", "");

        if (isbn.length() == 13 && isbn.matches("\\d{13}")) {
            return isbn.startsWith("978") || isbn.startsWith("979");
        }

        if (isbn.length() == 10) {
            if (isbn.matches("\\d{9}[0-9X]")) {
                int suma = 0;
                for (int i = 0; i < 9; i++) {
                    suma += (isbn.charAt(i) - '0') * (10 - i);
                }
                char ultimoCaracter = isbn.charAt(9);
                int ultimoDigito = (ultimoCaracter == 'X') ? 10 : (ultimoCaracter - '0');
                suma += ultimoDigito;

                return suma % 11 == 0;
            }
        }

        return false;
    }

    /**
     * Valida DNI o NIE espanyol por numero y letra.
     *
     * @author Dan Bolocan
     */
    public static boolean dniBien(String dni) {
        if (dni == null) {
            return false;
        }

        String documento = dni.trim().toUpperCase();
        if (documento.length() != 9) {
            return false;
        }

        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        char letraDocumento = documento.charAt(8);
        String parteNumerica;
        char primerCaracter = documento.charAt(0);

        if (Character.isDigit(primerCaracter)) {
            if (!documento.substring(0, 8).matches("\\d{8}")) {
                return false;
            }
            parteNumerica = documento.substring(0, 8);
        } else if (primerCaracter == 'X' || primerCaracter == 'Y' || primerCaracter == 'Z') {
            if (!documento.substring(1, 8).matches("\\d{7}")) {
                return false;
            }

            char prefijo;
            if (primerCaracter == 'X') {
                prefijo = '0';
            } else if (primerCaracter == 'Y') {
                prefijo = '1';
            } else {
                prefijo = '2';
            }
            parteNumerica = prefijo + documento.substring(1, 8);
        } else {
            return false;
        }

        int numero = Integer.parseInt(parteNumerica);
        int resto = numero % 23;

        return letraDocumento == letras.charAt(resto);
    }

    /**
     * Valida un numero de telefono espanyol.
     *
     * @author Dan Bolocan
     */
    public static boolean telefonoBien(String telefono) {
        if (telefono.length() != 9) {
            return false;
        }

        if (telefono.matches("^([6-7]\\d{8}|91\\d{7}|93\\d{7}|95\\d{7}|96\\d{7}|97\\d{7})$")) {
            return true;
        }

        return false;
    }
}
