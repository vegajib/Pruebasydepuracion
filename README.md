# API REST de inventario

El proyecto usa Java 17, Spring Boot y Maven. Los productos se guardan en un mapa en memoria que se vacía al reiniciar la aplicación.

- `01-con-defecto` conserva la falla intencional al actualizar el stock.
- `02-corregida` aplica el cambio. Ambas carpetas contienen la misma suite JUnit 5.

## Ejecutar en Windows

Configura un JDK 17 y abre una de las dos carpetas como proyecto en VS Code. Instala Extension Pack for Java cuando VS Code lo recomiende. Verifica con `java -version` que la terminal utilice Java 17.

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

El primer uso del Maven Wrapper requiere Internet. Si ya tienes Maven, también puedes usar `mvn test` y `mvn spring-boot:run`.

| Operación | Ruta | Éxito |
|---|---|---|
| Crear | `POST /api/productos` | 201 con encabezado Location |
| Listar | `GET /api/productos` | 200 |
| Consultar | `GET /api/productos/{id}` | 200 |
| Actualizar | `PUT /api/productos/{id}` | 200 |
| Eliminar | `DELETE /api/productos/{id}` | 204 |

Para crear y actualizar, envía JSON con `nombre` y `stock`. El nombre es obligatorio y el stock es un entero no negativo. Las entradas inválidas devuelven 400 y los recursos inexistentes, 404.

## Reproducir y depurar

1. En `01-con-defecto`, ejecuta las pruebas. En el escenario principal, el producto inicia con stock 10 y la solicitud trae 15. La aserción detecta que la respuesta conserva 10.
2. Abre `ProductoService.java` y pon un punto de interrupción en la línea `Producto actualizado = new Producto(...)`. Abre `ProductoServiceTest.java` y elige **Debug Test** sobre `actualizaElStockSolicitado`.
3. En **Variables** o **Watch**, observa `actual.stock()` y `solicitud.stock()`. Presiona F10 y examina `actualizado.stock()` antes de que el mapa guarde el resultado.
4. Ejecuta la misma suite en `02-corregida`. Comprueba que el cambio usa `solicitud.stock()` y que la actualización, la consulta posterior y los casos de regresión pasan.

## Resultados de las pruebas

La versión con el defecto registró tres fallos en siete pruebas. En la versión corregida pasaron las siete.

Si Surefire informa que no encuentra la clase de prueba al abrir el proyecto desde una unidad sincronizada, ejecuta `.\mvnw.cmd -Dsurefire.useManifestOnlyJar=false test`. Ese parámetro cambia cómo Surefire pasa el classpath a la JVM de prueba.
