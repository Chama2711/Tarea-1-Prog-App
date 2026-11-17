# edEXT — Ejecutar y probar en el CURE

## Antes de empezar

Usen dos PC con Ubuntu en la misma red. Con una sola copia del proyecto alcanza.

| PC | Qué necesita |
| --- | --- |
| Servidor central | Java 21, MySQL iniciado, una sesión de escritorio para Swing, `servidor.jar` y `central.properties`. |
| Servidor web | Java 21, Tomcat 10.1, `web.war` y `web.properties`. |
| PC donde compilan | JDK 21, Git si van a clonar el repo e internet para la primera compilación. Puede ser la PC del central o la de ustedes. |

Ejecuten `java -version` en ambas PC y `javac -version` en la que compila. En Ubuntu, `hostname -I` muestra las direcciones de cada PC. Anoten la **IP del central** para SOAP y la **IP de Tomcat** para entrar a la web. En esta guía usamos `192.168.1.50` para el central y `192.168.1.60` para Tomcat. Reemplácenlas por las reales.

Si falta software y el encargado les permite instalarlo, en Ubuntu 24.04 pueden usar `sudo apt update` y luego `sudo apt install openjdk-21-jdk` en la PC que compila o `sudo apt install openjdk-21-jre` en las que solo ejecutan. El paquete del central es `mysql-server`. Para Tomcat pueden usar `tomcat10` y, para el panel, `tomcat10-admin`. Si descargan Tomcat de Apache, elijan la distribución **Core** de la serie **10.1** y descomprímanla en su carpeta personal. [Paquetes de Ubuntu](https://packages.ubuntu.com/noble/java/) · [Descargas de Apache Tomcat 10.1](https://tomcat.apache.org/download-10.cgi)

## 1. Generar los paquetes

Si llegan sin el proyecto, descarguen el ZIP de la rama **`Main-datos_prueba`** en GitHub o clonen esa rama con Git:

```bash
git clone --branch Main-datos_prueba https://github.com/Chama2711/Tarea-1-Prog-App.git
cd Tarea-1-Prog-App
```

Si ya tienen un clon, desde la raíz ejecuten `git fetch origin`, `git switch Main-datos_prueba` y `git pull --ff-only`. Si descargaron el ZIP, entren a la carpeta descomprimida que contiene el `pom.xml).

En Ubuntu ejecuten `sh compilar.sh`. El script usa Maven Wrapper con **Maven 3.10.0**. La primera compilación necesita internet para descargar Maven y las dependencias que todavía no estén en caché. Si ya tienen Maven instalado, también pueden ejecutar `MVN=mvn sh compilar.sh`.

En Windows, el equivalente al último comando es `.\compilar.cmd` en PowerShell o `compilar.cmd` en CMD. Si usan el Maven de NetBeans y no está en PATH, definan `MVN` con la ruta de su `mvn.cmd`.

Los scripts dejan `servidor.jar`, `web.war`, `configuracion-ejemplo`, `migraciones` y una copia de esta guía como `LEEME.md` en **`target/distribucion`**. Si ya llevan esa carpeta compilada, pasen al paso 2.

Copien el JAR y la plantilla del central a la PC con MySQL. Para pasar el WAR por web, suban `web.war` a Drive, descárguenlo en la PC de Tomcat y conserven el nombre exacto `web.war`. Lleven también la plantilla `web.properties.example`. Si el Manager acepta conexiones desde su PC, pueden subir el WAR directamente desde el navegador en el paso 4.

## 2. Configurar y cargar la base en la PC del central

En Ubuntu, ejecuten `mkdir -p ~/.edEXT`. En Windows, abran **Win + R → %USERPROFILE%** y creen allí la carpeta `.edEXT`. Guarden los archivos `.properties` en UTF-8.

Desde `target/distribucion` o la carpeta donde copiaron el JAR y las plantillas, copien `configuracion-ejemplo/central.properties.example` a `~/.edEXT/central.properties`. Si llevaron la plantilla suelta, cópienla a ese mismo destino. Déjenla así, con la IP real y las credenciales de MySQL de la PC:

```properties
soap.host=192.168.1.50
soap.port=18083
soap.path=/edext/catalogo
jdbc.url=jdbc:mysql://localhost:3306/edext_web?zeroDateTimeBehavior=CONVERT_TO_NULL
jdbc.usuario=TU_USUARIO_MYSQL
jdbc.contrasena=TU_CONTRASEÑA_MYSQL
```

En una instalación como servicio, `systemctl status mysql` muestra si MySQL está activo. Si está detenido, el encargado puede iniciarlo con `sudo systemctl start mysql`. Comprueben que pueden entrar con `mysql -h 127.0.0.1 -P 3306 -u TU_USUARIO_MYSQL -p`. La web se conecta al central por SOAP. MySQL queda en la PC del central.

Si es una instalación nueva y `root` local solo entra con `sudo mysql`, el encargado puede ejecutar lo siguiente en la consola de MySQL para crear una cuenta propia del proyecto. Reemplacen la contraseña del ejemplo y usen esa cuenta en `central.properties` y en el cargador inicial. [Configuración de MySQL en Ubuntu](https://ubuntu.com/server/docs/how-to/databases/install-mysql/)

```sql
CREATE DATABASE IF NOT EXISTS edext_web;
CREATE USER 'edext'@'localhost' IDENTIFIED BY 'CAMBIAR_POR_UNA_CONTRASEÑA';
GRANT ALL PRIVILEGES ON edext_web.* TO 'edext'@'localhost';
```

Desde la carpeta donde está `servidor.jar`, hagan la carga con Swing cerrado:

1. **Base nueva.** Creen `edext_web` en MySQL con `CREATE DATABASE edext_web;` si aún no existe. Con la base sin tablas, ejecuten una vez:

   ```bash
   java -cp servidor.jar persistencia.CargarDatosPruebaWeb TU_USUARIO_MYSQL 'TU_CONTRASEÑA'
   ```

   En Ubuntu, pasen `''` como último argumento si la contraseña está vacía. En CMD usen comillas dobles.

2. **Base existente.** Si ya tiene los datos iniciales, salteen la carga inicial. Comprueben el esquema con `USE edext_web;`, `SHOW TABLES;` y `SHOW CREATE TABLE nombre_de_la_tabla;`. Usen los nombres con la capitalización que devuelve MySQL. Si faltan todas las columnas de cierre y evaluación, el script `migraciones/2026-11-07-evaluaciones.sql` las agrega. Si falta `VALORACION_CURSO`, usen `migraciones/2026-11-09-valoraciones.sql`. Revisen los nombres de tablas dentro de los SQL si la instalación distingue mayúsculas de minúsculas.

   Para aplicar un archivo que corresponda, desde la distribución pueden usar, por ejemplo:

   ```bash
   mysql -h 127.0.0.1 -P 3306 -u TU_USUARIO_MYSQL -p edext_web < migraciones/2026-11-07-evaluaciones.sql
   ```

   Si solo falta el campo del video, agreguen `VIDEO_MOTIVACION_URL VARCHAR(2048) NULL` a la tabla de inscripciones. Por ejemplo, si `SHOW TABLES` devuelve `INSCRIPCION`:

   ```sql
   ALTER TABLE INSCRIPCION ADD COLUMN VIDEO_MOTIVACION_URL VARCHAR(2048) NULL;
   ```

   Para probar altas de cursos, comprueben también que `cursos.ID` tenga `AUTO_INCREMENT`. Si falta o el esquema ya está migrado parcialmente, pidan al encargado ajustar ese esquema con su DDL real. Una carga inicial con el JAR de esta rama crea el esquema completo.

3. **Anexo de Tarea 3.** Tanto en una base nueva como en una base con los datos iniciales, ejecuten:

   ```bash
   java -cp servidor.jar persistencia.CargarDatosPruebaTarea3
   ```

   El comando usa la base indicada en `central.properties`. Esperen el mensaje **«Datos de prueba de la Tarea 3 cargados.»** Si el cargador informa que un estado, una nota o una valoración difiere del anexo, revisen esa fila antes de reintentar.

4. Arranquen el central con `java -jar servidor.jar` y dejen Swing abierto. Al cerrar Swing se detienen los servicios SOAP.

## 3. Preparar la PC de Tomcat

Si Tomcat está instalado como servicio, comprueben que esté activo con `systemctl status tomcat10`, si ese es el nombre de la unidad. Si está detenido, el encargado puede iniciarlo con `sudo systemctl start tomcat10`. Usen `systemctl cat tomcat10` para ver el usuario y la configuración del servicio. Si el CURE administra la instalación, pidan al encargado la ruta de la instancia y el usuario que la ejecuta.

Si descomprimieron Tomcat ustedes, la carpeta base suele ser la que contiene `bin/`, `conf/` y `webapps/`. Con Java 21 en el PATH o en `JAVA_HOME`, entren en esa carpeta y ejecuten `sh bin/catalina.sh run`. Dejen la terminal abierta mientras lo usan.

Desde la PC de Tomcat, verifiquen que ambos WSDL devuelvan XML, con el central corriendo:

```bash
curl -fsS 'http://192.168.1.50:18083/edext/catalogo?wsdl'
curl -fsS 'http://192.168.1.50:18083/edext/evaluaciones?wsdl'
```

Copien la plantilla `web.properties.example` a **`.edEXT/web.properties` dentro de la carpeta personal que usa Java al ejecutar Tomcat**. Si lo arrancan desde su propia terminal, normalmente será `~/.edEXT/web.properties`. Si corre como servicio, el archivo tiene que quedar en la carpeta personal de ese servicio y su usuario debe poder leerlo. Si la JVM tiene definido `-Duser.home`, usen esa ruta. Dejen las dos URL con la **IP del central**:

```properties
soap.catalogo.wsdl=http://192.168.1.50:18083/edext/catalogo?wsdl
soap.evaluaciones.wsdl=http://192.168.1.50:18083/edext/evaluaciones?wsdl
```

Para probar todo en una sola PC, usen `127.0.0.1` en ambos archivos. Si cambian `central.properties`, reinicien el central. Si cambian `web.properties` después del despliegue, vuelvan a cargar la aplicación desde Manager o reinicien Tomcat según cómo lo tengan instalado.

## 4. Desplegar `web.war`

### Con HTML App Manager

Es el método que pide el punto 7.8 de la entrega.

1. En la PC de Tomcat abran [Tomcat Manager](http://localhost:8080/manager/html). Si usan otro puerto, cambien `8080`. Entren con una cuenta de Tomcat que tenga el rol `manager-gui`.
2. Si aparece la fila `/web` y quieren reemplazar esa versión, usen **Undeploy** en esa fila. Esto retira el despliegue anterior.
3. En **Deploy → WAR file to deploy**, pulsen **Elegir archivo**, seleccionen `web.war` y pulsen **Deploy**.
4. Verifiquen que `/web` figure como iniciado en **Applications**. Abran [edEXT en la PC de Tomcat](http://localhost:8080/web/inicio). Desde otra PC usen `http://192.168.1.60:8080/web/inicio`, con la IP real de Tomcat.

Si el Manager ya permite entrar desde otra PC, pueden abrir `http://192.168.1.60:8080/manager/html` desde la PC que tiene el WAR y elegir el archivo ahí. Si reciben **403** desde otra máquina, prueben el Manager local en la PC de Tomcat.

### Si nunca se configuró la cuenta

En una instalación estándar que usa `tomcat-users.xml`, quien administra Tomcat agrega estas líneas **dentro de `<tomcat-users>`** en `$CATALINA_BASE/conf/tomcat-users.xml`, con una contraseña propia. Si el rol ya está declarado, alcanza con agregar el usuario.

```xml
<role rolename="manager-gui"/>
<user username="edext-deploy" password="CAMBIAR_POR_UNA_CONTRASEÑA" roles="manager-gui"/>
```

Reinicien esa instancia y entren desde la PC de Tomcat con esa cuenta. Si la PC está administrada por el CURE, pidan al encargado que cree la cuenta o les proporcione una con ese rol. [Configuración del Manager](https://tomcat.apache.org/tomcat-10.1-doc/manager-howto.html#Configuring_Manager_Application_Access)

### Si el Manager no está instalado

Si Tomcat responde en su puerto pero `/manager/html` da **404** incluso desde la misma PC, verifiquen con el encargado que el Manager esté desplegado. En Ubuntu 24.04, si Tomcat se instaló con los paquetes de Ubuntu, el encargado puede instalar `tomcat10-admin` con `sudo apt install tomcat10-admin`. Si usan una instalación descomprimida, el paquete completo de Apache Tomcat incluye la aplicación `manager` en `webapps/`. [Paquete de Ubuntu](https://packages.ubuntu.com/noble/tomcat10-admin) · [Descargas de Apache Tomcat 10.1](https://tomcat.apache.org/download-10.cgi)

### Para probar sin Manager

Con permiso de escritura, copien `web.war` a la carpeta de despliegue del Tomcat en uso. Consulten `appBase` del `<Host>` en `$CATALINA_BASE/conf/server.xml`. Si usa el valor habitual `webapps`, desde la carpeta base pueden ejecutar:

```bash
cp "/ruta/donde/descargaron/web.war" webapps/web.war
```

Si el despliegue automático en caliente está habilitado, Tomcat lo detecta. Si la instancia solo despliega al arrancar, arránquenla después de copiarlo. Si ya hay una versión de edEXT, coordinen su reemplazo con quien administra esa instancia. Comprueben el mismo enlace `/web/inicio`. [Despliegue automático de Tomcat](https://tomcat.apache.org/tomcat-10.1-doc/config/host.html#Automatic_Application_Deployment)

## 5. Probar la Tarea 3

Con los datos del cargador inicial pueden entrar con el docente de prueba `heisenberg` / `heisenberg123` y la estudiante `chechi` / `chechi123`. Para un alta, elijan un instituto del docente con el que iniciaron sesión.

- **Estrellas.** Busquen **Flor del Ceibo**. Debe mostrar **6 votos y un promedio de 3,67**. En el detalle, comprueben tres votos de 3, dos de 4 y uno de 5.
- **Mis notas.** Como estudiante `chechi`, entren a **Mi perfil → Mis notas**. Deben figurar **7 en Dalavuelta, 9 en Extensionismo Industrial y 11 en Inclusión Energética**.
- **Certificado.** Si la base ya tiene la inscripción de `chechi` a **EFI Ingeniería Mecánica** de la Tarea 2, descarguen su certificado desde **Mi perfil → Programas**. Si solo hicieron las cargas inicial y de Tarea 3, creen un programa que empiece hoy, agreguen **Dalavuelta** e inscriban allí a `chechi`. Con su nota de la edición cerrada ya puede obtener ese PDF.
- **Altas, notas y acta docente.** Creen un curso y una edición que empiece hoy. Asígnense como docente y comprueben que la edición aparezca enseguida en el detalle del curso. Inscriban a un estudiante, acéptenlo en **Seleccionar estudiantes**, guarden una nota en **Ingresar evaluaciones → Instituto → Curso** y cierren la edición. Abran **Ver resultados definitivos** para comprobar el acta. El estudiante también puede valorar ese curso desde **Valorar un curso**.
- **Curso asociado a un programa.** Agreguen el curso a un programa y comprueben que la asociación aparezca enseguida desde ambos detalles.
- **Video y cancelación.** En una edición vigente, inscriban a otro estudiante con un enlace de YouTube que permita la reproducción incorporada. Antes de que el docente resuelva la inscripción, editen el enlace desde el detalle de la edición y comprueben que se pueda reproducir en **Seleccionar estudiantes**. Prueben **Cancelar inscripción** con una inscripción pendiente. Los enlaces del anexo todavía se cargan manualmente para esta prueba.

## Si algo falla

| Síntoma | Qué revisar |
| --- | --- |
| `UnsupportedClassVersionError` | Java 21 en el proceso que falla. Tomcat como servicio puede usar un Java distinto al de la terminal. |
| Los WSDL abren en el central pero no en la PC de Tomcat | Comprueben que `soap.host` sea la IP del central en la red, que Swing siga abierto y que la red permita llegar a su puerto SOAP. Si el CURE bloquea el tráfico entre las PC, consulten al encargado. |
| El Manager abre pero rechaza el usuario | Revisen que la cuenta tenga rol `manager-gui` en esa instancia. |
| El Manager indica que `/web` ya existe | Retiren la versión anterior de edEXT en **Applications** y vuelvan a subir el WAR. |
| Tomcat responde, pero `/web/inicio` da 404 o `/web` figura detenido | Revisen que el archivo se llame `web.war` y el contexto sea `/web`. Revisen los logs por errores al leer `web.properties` o al conectar con SOAP. Para una instalación como servicio usen `journalctl -u tomcat10 -n 80 --no-pager` si ese es el nombre de la unidad. Para una instancia propia revisen la terminal de `catalina.sh run` o la carpeta `logs/`. |
| MySQL indica `Unknown column VIDEO_MOTIVACION_URL` | Revisen el paso 2 para bases existentes. |

## Datos adicionales de Tarea 2

Si necesitan la carga completa de Tarea 2, ejecuten `java -cp servidor.jar persistencia.CargarDatosPruebaTarea2` después de la carga inicial y antes del anexo de Tarea 3. Ese cargador usa `root` en el MySQL local y lee la contraseña de `EDEXT_DB_PASSWORD`. Si la variable no está definida, usa contraseña vacía. Busca las imágenes originales con sus subcarpetas en `Descargas/edext-imagenes-tarea2`, dentro de la carpeta personal. En Ubuntu respeten las mayúsculas y la extensión exacta de cada imagen.
