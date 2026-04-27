package models;

import java.util.Objects;

public class Libro {

    private int codigo;
    private String isbn;
    private String titulo;
    private String escritor;
    private int anyo_publicacion;
    private float puntuacion;

    /**
     * Constructor completo de libro.
     *
     * @author Dan Bolocan
     */
    public Libro(int codigo, String isbn, String titulo, String escritor, int anyo_publicacion, float puntuacion) {
        super();
        this.codigo = codigo;
        this.isbn = isbn;
        this.titulo = titulo;
        this.escritor = escritor;
        this.anyo_publicacion = anyo_publicacion;
        this.puntuacion = puntuacion;
    }

    /**
     * Constructor basico de libro.
     *
     * @author Dan Bolocan
     */
    public Libro(String isbn, String titulo) {
        this.isbn = isbn;
        this.titulo = titulo;
    }

    /**
     * Devuelve una cadena con los datos del libro.
     *
     * @author Dan Bolocan
     */
    @Override
    public String toString() {
        return String.format(
                "Libro [Codigo: %d | ISBN: %s | Titulo: %s | Escritor: %s | Anyo: %d | Puntuacion: %.1f]",
                codigo,
                isbn,
                titulo,
                escritor,
                anyo_publicacion,
                puntuacion
        );
    }

    /**
     * Calcula el hash usando el ISBN.
     *
     * @author Dan Bolocan
     */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    /**
     * Compara dos libros por ISBN.
     *
     * @author Dan Bolocan
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Libro other = (Libro) obj;
        return Objects.equals(isbn, other.isbn);
    }

    /**
     * Devuelve el codigo del libro.
     *
     * @author Dan Bolocan
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Cambia el codigo del libro.
     *
     * @author Dan Bolocan
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Devuelve el ISBN del libro.
     *
     * @author Dan Bolocan
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Cambia el ISBN del libro.
     *
     * @author Dan Bolocan
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Devuelve el titulo del libro.
     *
     * @author Dan Bolocan
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Cambia el titulo del libro.
     *
     * @author Dan Bolocan
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Devuelve el escritor del libro.
     *
     * @author Dan Bolocan
     */
    public String getEscritor() {
        return escritor;
    }

    /**
     * Cambia el escritor del libro.
     *
     * @author Dan Bolocan
     */
    public void setEscritor(String escritor) {
        this.escritor = escritor;
    }

    /**
     * Devuelve el anyo de publicacion.
     *
     * @author Dan Bolocan
     */
    public int getAnyo_publicacion() {
        return anyo_publicacion;
    }

    /**
     * Cambia el anyo de publicacion.
     *
     * @author Dan Bolocan
     */
    public void setAnyo_publicacion(int anyo_publicacion) {
        this.anyo_publicacion = anyo_publicacion;
    }

    /**
     * Devuelve la puntuacion del libro.
     *
     * @author Dan Bolocan
     */
    public double getPuntuacion() {
        return puntuacion;
    }

    /**
     * Cambia la puntuacion del libro.
     *
     * @author Dan Bolocan
     */
    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }
}
