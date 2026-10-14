/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.nio.file.Path;

public final class ImagenesEntidades {

    private ImagenesEntidades() {
    }

    private static ImagenesUsuario almacenamiento(String tipo) {
        Path carpeta = Path.of(
                System.getProperty("user.home"),
                "edEXT",
                "imagenes",
                tipo
        );

        return new ImagenesUsuario(carpeta);
    }

    public static ImagenesUsuario cursos() {
        return almacenamiento("cursos");
    }

    public static ImagenesUsuario ediciones() {
        return almacenamiento("ediciones");
    }

    public static ImagenesUsuario programas() {
        return almacenamiento("programas");
    }
}
