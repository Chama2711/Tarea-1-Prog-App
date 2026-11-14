/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import logica.Categoria;


public class PruebaCategoria {
    
    public static void main(String[] args) {

        ControladorPersistencia cp = new ControladorPersistencia();

        try {
             // 1. Mostrar categorías existentes
            System.out.println("=== CATEGORIAS ===");

            for (Categoria categoria : cp.listarCategorias()) {
                System.out.println(
                    categoria.getId() + " - " + categoria.getNombre()
                );
            }

            // 2. Asociar Tecnología con MicroBit
            cp.agregarCategoriaACurso(
                "MicroBit",
                "Tecnología"
            );

            System.out.println(
                "\nCategoría asociada correctamente a MicroBit."
            );

            // 3. Consultar categorías del curso
            System.out.println("\n=== CATEGORIAS DE MICROBIT ===");

            for (Categoria categoria :
                    cp.listarCategoriasCurso("MicroBit")) {

                System.out.println(
                    categoria.getNombre()
                );
            }


        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
