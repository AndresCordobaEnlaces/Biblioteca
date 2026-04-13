# Biblioteca
Lo que tiene que hacer cada uno

🔵 DAN — Actividad 2x01 (Libros)
Primero crea la base de datos (ya que es la rama base):

Crear el archivo biblioteca.db en SQLite con las 3 tablas: libro, socio y préstamo

Luego la clase Actividad_2x01 con menú de 0 a 6:

0 → Salir

1 → Insertar libro (leer ISBN, título, escritor, año, puntuación — código autoincremental)

2 → Eliminar libro por código (controlar si no existe o si tiene préstamos)

3 → Consultar todos los libros

4 → Consultar libros por escritor, ordenados por puntuación descendente

5 → Consultar libros no prestados (nunca han tenido préstamo)

6 → Consultar libros devueltos en una fecha concreta

🟢 AARON — Actividad 2x02 (Socios)
La clase Actividad_2x02 con menú de 0 a 6:

0 → Salir

1 → Insertar socio (leer DNI, nombre, domicilio, teléfono, correo — código autoincremental)

2 → Eliminar socio por código (controlar si no existe o si tiene préstamos)

3 → Consultar todos los socios

4 → Consultar socios por localidad, ordenados por nombre ascendente

5 → Consultar socios sin ningún préstamo

6 → Consultar socios con préstamos en una fecha concreta

🟡 ANDRES — Actividades 2x03 + 2x04 (Préstamos + Consultas avanzadas)
La clase Actividad_2x03 con menú de 0 a 6:

0 → Salir

1 → Insertar préstamo (validar que el libro esté disponible y el socio no tenga otro préstamo pendiente)

2 → Actualizar fecha de devolución de un préstamo

3 → Eliminar préstamo por datos identificativos

4 → Consultar todos los préstamos

5 → Consultar préstamos no devueltos

6 → Consultar DNI+nombre socio, ISBN+título libro y fecha devolución de préstamos en una fecha


🟡🟢🔵 DIVIDIMOS EL TRABAJO ENTRE LOS 3
La clase Actividad_2x04 con menú de 0 a 6:

0 → Salir

1 → Libro(s) prestado(s) menos veces

2 → Socio(s) con más préstamos

3 → Libros prestados menos veces que la media

4 → Socios con préstamos más veces que la media

5 → ISBN, título y nº préstamos ordenados por préstamos descendente

6 → DNI, nombre y nº préstamos de socios, ordenados por préstamos descendente

