# edEXT — Ejecutar y probar en el CURE

En el CURE usen dos PC con Ubuntu. En una corren **el servidor central y MySQL** y en la otra **Tomcat 10.1**. Ambas necesitan Java 21 y deben poder comunicarse por red. La PC que compila también necesita Maven. El central abre Swing, así que ejecútenlo desde una sesión de escritorio.

## 1. Generar los paquetes

Para la demo con el anexo de la Tarea 3, abran una terminal en la carpeta del proyecto, donde está el `pom.xml`.

```bash
git fetch origin
git switch Main-datos_prueba
git pull --ff-only
sh compilar.sh
```

En Windows, el equivalente al último comando es `.\compilar.cmd` en PowerShell o `compilar.cmd` en CMD. Si usan el Maven de NetBeans y no está en PATH, definan `MVN` con la ruta de su `mvn.cmd`.

Los scripts dejan `servidor.jar`, `web.war`, `configuracion-ejemplo`, `migraciones` y una copia de esta guía como `LEEME.md` en **`target/distribucion`**. Si ya llevan esa carpeta compilada, pasen al paso 2.

## 2. Configurar y cargar la base en la PC del central

En Ubuntu, ejecuten `mkdir -p ~/.edEXT`. En Windows, abran **Win + R → %USERPROFILE%** y creen allí la carpeta `.edEXT`. Guarden los archivos `.properties` en UTF-8.

Copien `configuracion-ejemplo/central.properties.example` a `~/.edEXT/central.properties`. Cambien `soap.host` por la **IP de la PC del central**. En Ubuntu pueden verla con `hostname -I`. Ajusten `jdbc.usuario` y `jdbc.contrasena` según su MySQL. El ejemplo mantiene el puerto SOAP `18083` y la base local `edext_web`.

Desde la carpeta donde está `servidor.jar`, hagan la carga con Swing cerrado:

1. **Base nueva.** Creen `edext_web` en MySQL con `CREATE DATABASE edext_web;` si aún no existe. Con la base sin tablas, ejecuten una vez:

   ```bash
   java -cp servidor.jar persistencia.CargarDatosPruebaWeb TU_USUARIO_MYSQL 'TU_CONTRASEÑA'
   ```

   En Ubuntu, pasen `''` como último argumento si la contraseña está vacía. En CMD usen comillas dobles.

2. **Base existente.** Si ya tiene los datos iniciales, salteen la carga inicial. Si el esquema es anterior a la Tarea 3, apliquen únicamente las migraciones que falten. `2026-11-07-evaluaciones.sql` agrega el estado de cierre y las columnas de nota y fecha. `2026-11-09-valoraciones.sql` crea `VALORACION_CURSO`.

3. **Anexo de Tarea 3.** Tanto en una base nueva como en una base con los datos iniciales, ejecuten:

   ```bash
   java -cp servidor.jar persistencia.CargarDatosPruebaTarea3
   ```

   El comando usa la base indicada en `central.properties`. Esperen el mensaje **«Datos de prueba de la Tarea 3 cargados.»** Si el cargador informa que un estado, una nota o una valoración difiere del anexo, revisen esa fila antes de reintentar.

4. Arranquen el central con `java -jar servidor.jar` y dejen Swing abierto. Al cerrar Swing se detienen los servicios SOAP.

## 3. Desplegar en la PC de Tomcat

1. Desde la PC de Tomcat, abran ambos WSDL con la IP del central. En este ejemplo la IP es `192.168.1.50`:

   ```text
   http://192.168.1.50:18083/edext/catalogo?wsdl
   http://192.168.1.50:18083/edext/evaluaciones?wsdl
   ```

   Los dos deben devolver XML. Si fallan, revisen que Swing siga abierto y que la IP y el puerto sean accesibles desde la PC de Tomcat.

2. Copien `configuracion-ejemplo/web.properties.example` a `.edEXT/web.properties` en la **carpeta personal del usuario que ejecuta Tomcat**. Sustituyan `127.0.0.1` por la IP del central en las dos direcciones SOAP.
3. Desplieguen `web.war` en el **HTML App Manager** de Tomcat. En esa misma PC, abran `http://localhost:8080/web/inicio`. Desde otra PC, usen la IP de Tomcat. Si Tomcat usa otro puerto o contexto, ajusten la URL.

Para probar todo en una sola PC, usen `127.0.0.1` en ambos archivos de configuración. Después de editar `.properties`, reinicien la aplicación correspondiente para que vuelva a leerlos.

## 4. Probar la Tarea 3

- **Estrellas.** Busquen **Flor del Ceibo**. Debe mostrar **6 votos y un promedio de 3,67**. En el detalle, comprueben tres votos de 3, dos de 4 y uno de 5.
- **Mis notas.** Como estudiante `chechi`, entren a **Mi perfil → Mis notas**. Deben figurar **7 en Dalavuelta, 9 en Extensionismo Industrial y 11 en Inclusión Energética**.
- **Certificado.** Si la base ya tiene la inscripción de `chechi` a **EFI Ingeniería Mecánica** de la Tarea 2, descarguen su certificado desde **Mi perfil → Programas**. Si solo hicieron las cargas inicial y de Tarea 3, creen un programa que empiece hoy, agreguen **Dalavuelta** e inscriban allí a `chechi`. Con su nota de la edición cerrada ya puede obtener ese PDF.
- **Altas, notas y acta docente.** Creen un curso y una edición que empiece hoy. Asígnense como docente y comprueben que la edición aparezca enseguida en el detalle del curso. Inscriban a un estudiante, acéptenlo en **Seleccionar estudiantes**, guarden una nota en **Ingresar evaluaciones → Instituto → Curso** y cierren la edición. Abran **Ver resultados definitivos** para comprobar el acta. El estudiante también puede valorar ese curso desde **Valorar un curso**.
- **Curso asociado a un programa.** Agreguen el curso a un programa y comprueben que la asociación aparezca enseguida desde ambos detalles.

## Datos adicionales de Tarea 2

Si necesitan la carga completa de Tarea 2, ejecuten `java -cp servidor.jar persistencia.CargarDatosPruebaTarea2` después de la carga inicial. Ese cargador usa `root`, requiere la variable `EDEXT_DB_PASSWORD` definida y busca las imágenes originales con sus subcarpetas en `Descargas/edext-imagenes-tarea2`, dentro de la carpeta personal.
