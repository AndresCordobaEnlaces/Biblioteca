package models;

public class Socio {

    private int codigo;
    private String dni;
    private String nombre;
    private String domicilio;
    private String telefono;
    private String correo;

    public Socio(int codigo, String dni, String nombre, String domicilio, String telefono, String correo) {
        this.codigo = codigo;
        this.dni = dni;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return "Socio [Codigo = " + codigo + ", DNI = " + dni + ", Nombre = " + nombre
                + ", Domicilio = " + domicilio + ", Telefono = " + telefono + ", Correo = " + correo + "]";
    }
}
