# edEXT — Ejecutar Swing y SOAP

Necesitan **Java 21, NetBeans y MySQL iniciado**.

## 1. Preparar la base

Si ya tienen `edext_web` con los datos, salteen este paso.

En MySQL:

```sql
CREATE DATABASE edext_web;
```

En NetBeans, dentro de `servidor-central`, ejecuten `persistencia.CargarDatosPruebaWeb`. Crea las tablas y carga los datos iniciales. Por defecto usa `root` sin contraseña; si usan otras credenciales, recibe usuario y contraseña como argumentos.

Revisen también el usuario y la contraseña de su MySQL en:

```text
servidor-central/src/main/resources/META-INF/persistence.xml
```

Dejen las dos propiedades de creación de tablas en `none`.

## 2. Configurar SOAP

**En Windows:**

1. Presionen **Windows + R**, escriban `%USERPROFILE%` y acepten.
2. En la carpeta que se abre, creen una carpeta llamada `.edEXT`.
3. Abran el Bloc de notas y peguen este contenido:

```properties
soap.host=127.0.0.1
soap.port=18083
soap.path=/edext/catalogo
```

4. En el Bloc de notas, elijan **Guardar como** y seleccionen la carpeta `.edEXT` que crearon.
5. Pongan como nombre `central.properties`, tipo **Todos los archivos** y codificación **UTF-8**. Guarden y comprueben que no termine en `.txt`.

**En Ubuntu:**

1. Abran **Archivos → Carpeta personal**.
2. Presionen **Ctrl + H** para mostrar las carpetas ocultas.
3. Creen una carpeta llamada `.edEXT`.
4. Abran el editor de texto y peguen las mismas tres líneas de configuración de arriba.
5. Guarden el archivo como `central.properties` dentro de `.edEXT`, sin extensión `.txt`.

Queda en `/home/SU_USUARIO/.edEXT/central.properties`. El programa encuentra esa carpeta automáticamente; el resto de los pasos es el mismo.

Cada compañero debe crear este archivo; no viene con Git.

Para el cliente SOAP de la web, creen también `web.properties` en esa misma carpeta `.edEXT`, con:

```properties
soap.catalogo.wsdl=http://127.0.0.1:18083/edext/catalogo?wsdl
```

Hay una plantilla en `configuracion-ejemplo/web.properties.example`. La aplicación web crea el cliente SOAP al arrancar. Consulta los institutos del menú, registro, selección, altas y aceptados; también las categorías del menú, alta de curso, búsqueda y detalles de cursos y programas. Si cambian la dirección, reinicien la aplicación web; no necesitan recompilar.

## 3. Ejecutar

1. Abran el proyecto raíz en NetBeans y hagan **Clean and Build**.
2. Dentro de `estacion-trabajo`, ejecuten `presentacion.PrincipalFrame`.
3. Se abre Swing y se publica SOAP. No hace falta iniciar Tomcat.
4. Abran [el WSDL](http://127.0.0.1:18083/edext/catalogo?wsdl): debe aparecer XML.

Al cerrar Swing también se detiene SOAP. La dirección funciona en la misma PC donde ejecutan el proyecto.

Para probar el menú web, dejen el central abierto y después inicien Tomcat con el WAR actualizado. Los listados de institutos y categorías de la web se consultan por SOAP, incluidos los institutos del docente autenticado. Las categorías usan el mismo servicio y la misma configuración de conexión. Inicio, listado, búsqueda, detalle e imagen de cursos también usan SOAP. Los selectores de cursos en formularios, las altas, las consultas de programas y docentes y las demás operaciones mantienen su acceso actual a MySQL. El usuario que ejecuta Tomcat debe tener el archivo `.edEXT/web.properties` en su carpeta personal.

Después de actualizar las operaciones SOAP, reinicien el central y vuelvan a desplegar el WAR actualizado. Maven regenera el cliente desde el WSDL incluido en el proyecto.

Si apagan el central con la web ya iniciada, el menú muestra avisos en lugar de los institutos y las categorías. Al volver a iniciar el central y recargar la página, el listado se recupera.

## Datos adicionales de Tarea 2

Para esta prueba de institutos alcanza la carga inicial. Si necesitan los datos adicionales, ejecuten después `persistencia.CargarDatosPruebaTarea2`: requiere la variable `EDEXT_DB_PASSWORD` y las imágenes originales en `%USERPROFILE%\Descargas\edext-imagenes-tarea2`, con sus subcarpetas. Ese cargador rechaza una variable no definida, incluso si MySQL usa contraseña vacía.
