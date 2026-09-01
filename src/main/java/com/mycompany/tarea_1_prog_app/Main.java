package com.mycompany.tarea_1_prog_app;

public class Main
{
    public static void main(String[] args)
    {
       Controlador controlador = new Controlador();
       VentanaPrincipal ventana = new VentanaPrincipal(controlador);
       ventana.setVisible(true);
    }
}
