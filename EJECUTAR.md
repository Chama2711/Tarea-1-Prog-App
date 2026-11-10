# edEXT en Ubuntu y Windows

## Compilar

Necesitan **JDK 21 y Maven**. Desde la carpeta del proyecto:

- Ubuntu: `sh compilar.sh`
- Windows (CMD): `compilar.cmd`
- Windows (PowerShell): `.\compilar.cmd`

Los scripts ejecutan las pruebas y dejan `servidor.jar`, `web.war`, las plantillas y estas instrucciones en **`target/distribucion`**. Esa carpeta está ignorada por Git.

Si Maven no está en PATH, pueden indicar su ejecutable mediante `MVN`. Por ejemplo, en tu Windows, desde CMD:

```bat
set "JAVA_HOME=D:\Program Files\Java\jdk-21.0.11"
set "MVN=D:\Program Files\netbeans\java\maven\bin\mvn.cmd"
compilar.cmd
```

## Preparar la configuración

Para ejecutar los paquetes alcanza **Java 21**, MySQL para el central y Tomcat **10.1** para la web. No necesitan Maven ni NetBeans en las computadoras donde los ejecutan. Swing necesita una sesión de escritorio.

En la computadora del central, copien `configuracion-ejemplo/central.properties.example` a:

- Ubuntu: `~/.edEXT/central.properties` (creen la carpeta con `mkdir -p ~/.edEXT`).
- Windows: **Win + R → `%USERPROFILE%`**, creen `.edEXT` y guarden allí `central.properties`.

Editen `jdbc.url`, `jdbc.usuario` y `jdbc.contrasena` según su MySQL. La contraseña puede quedar vacía. La base debe estar preparada con los datos del proyecto; el arranque normal no crea ni borra tablas.

En la computadora de Tomcat, hagan lo mismo con `web.properties.example`, guardándolo como **`.edEXT/web.properties` en la carpeta personal del usuario que ejecuta Tomcat**.

Si usan dos computadoras:

- En el central, cambien `soap.host` por la IP de esa computadora en la red.
- En `web.properties`, reemplacen `127.0.0.1` por esa misma IP en las dos direcciones SOAP.
- El puerto SOAP configurado debe ser accesible desde la computadora de Tomcat.

Los archivos se leen en UTF-8. Después de modificar la configuración, reinicien la aplicación correspondiente; no hace falta recompilar.

## Ejecutar

En Ubuntu o Windows, desde la carpeta donde copiaron el JAR:

```text
java -jar servidor.jar
```

Se abre Swing y se publican catálogo y evaluaciones por SOAP. Al cerrar Swing, se detienen los servicios. El JAR incluye las bibliotecas necesarias.

Con el central abierto, entren al **HTML App Manager de Tomcat**, seleccionen `web.war` en el formulario de despliegue y desplieguen. En una instalación con puerto 8080, abran:

```text
http://localhost:8080/web/inicio
```

Desde otra computadora, reemplacen `localhost` por la IP de la computadora de Tomcat. `/web` es el contexto del nuevo `web.war`; el puerto depende de su Tomcat.

## Base nueva

Si todavía no tienen la base, creen `edext_web` en MySQL y, desde la carpeta del JAR, ejecuten una sola vez el cargador existente:

```text
java -cp servidor.jar persistencia.CargarDatosPruebaWeb root ""
```

Cambien `root` y `""` por su usuario y contraseña. Este cargador inicial utiliza MySQL local y exige que `edext_web` esté vacía. Para los datos adicionales de Tarea 2, sigan las instrucciones del README del proyecto. Los scripts de `migraciones` son para bases anteriores que todavía no tengan esas columnas o tablas.
