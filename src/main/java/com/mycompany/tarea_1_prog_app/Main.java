package com.mycompany.tarea_1_prog_app;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main
{
    public static void main(String[] args)
    {
      
        
       Controlador controlador = new Controlador();
       VentanaPrincipal ventana = new VentanaPrincipal(controlador);
       ventana.setVisible(true);
    }
}
