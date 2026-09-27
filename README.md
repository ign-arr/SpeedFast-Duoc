# SpeedFast - Semana 7

Aplicación en Java para gestionar pedidos y repartidores.

## Funcionalidades

- Registrar pedidos de comida, encomienda y compras express.
- Registrar repartidores.
- Consultar pedidos en una tabla.
- Asignar repartidores e iniciar y finalizar entregas.
- Guardar pedidos, repartidores y entregas en MySQL.


## Ejecución

1. Abrir el proyecto en IntelliJ como proyecto Maven y seleccionar JDK 23.
2. Iniciar el servidor MySQL local y ejecutar `sql/speedfast_db.sql` en MySQL Workbench.
3. Comprobar en `dao.ConexionBD` la conexión a `localhost:3306`, la base `speedfast_db` y el usuario `root`.
4. En la configuración de ejecución de `main.Main`, agregar la variable de entorno `SPEEDFAST_DB_PASSWORD` con la contraseña de MySQL.
5. Cargar las dependencias de Maven y ejecutar `main.Main`.

Las entregas se inician y finalizan mediante los botones de la aplicación. `main.SimulacionConsola` conserva la simulación con hilos de las semanas anteriores y funciona en memoria.

## Autor

Ignacio Arriagada  
Analista Programador Computacional - Duoc UC  
Desarrollo Orientado a Objetos II
