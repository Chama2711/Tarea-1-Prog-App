package logica;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author adrie
 */

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import persistencia.ControladorPersistencia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;







public class ControladorUsuario {
    private ControladorPersistencia controlPersistencia = new ControladorPersistencia();
    
   public void modificarDatosUsuario(String nickname, String nuevoNombre, String nuevoApellido, LocalDate nuevaFecha) {
        try {
            // 1. Traemos el usuario de la BD
            Usuario usu = controlPersistencia.obtenerUsuario(nickname);
            
            if (usu != null) {
                // 2. Actualizamos los datos (el nickname y email no cambian)
                usu.setNombre(nuevoNombre);
                usu.setApellido(nuevoApellido);
                usu.setFechaNacimiento(nuevaFecha);
                
                // 3. Mandamos a guardar los cambios a la BD
                controlPersistencia.editarUsuario(usu);
            }
        } catch (Exception ex) {
            System.out.println("Error al modificar usuario: " + ex.getMessage());
        }
    }
    
    // Ejemplo de listar para tus JList:
    public List<String> obtenerNicknamesUsuarios() {
        List<Usuario> listaUsuarios = controlPersistencia.obtenerUsuarios();
        List<String> nicknames = new ArrayList<>();
        
        for (Usuario u : listaUsuarios) {
            nicknames.add(u.getNick());
        }
        return nicknames;
    }


    public boolean existeNickname(String nick) {
    // Si la BD devuelve un usuario, significa que el nick ya existe
    return controlPersistencia.obtenerUsuario(nick) != null;
}

public boolean existeCorreo(String mail) {
    // Buscamos en la lista de la BD si el correo ya está en uso
    List<Usuario> usuarios = controlPersistencia.obtenerUsuarios();
    for (Usuario u : usuarios) {
        if (u.getMail().equalsIgnoreCase(mail)) {
            return true;
        }
    }
    return false;
}

    public void registrarEstudiante(String ni, String m, String no, String a, LocalDate fn) {
        Estudiante e = new Estudiante(ni, m, no, a, fn);
        guardar(e);
    }

    public void registrarDocente(String ni, String m, String no, String a, LocalDate fn) {
        Docente d = new Docente(ni, m, no, a, fn);
        guardar(d);
    }

    private void guardar(Usuario u) {
    try {
        // En vez de guardarlo en un mapa, lo mandamos a la base de datos
        controlPersistencia.crearUsuario(u);
    } catch (Exception ex) {
        System.out.println("Error al guardar en BD: " + ex.getMessage());
    }
}
    
    
    
    
public List<String> listarNicknamesUsuarios() {
    List<String> nicks = new ArrayList<>();
    List<Usuario> usuarios = controlPersistencia.obtenerUsuarios();
    
    // Recorremos los usuarios de la BD y sacamos solo los nicknames
    for (Usuario u : usuarios) {
        nicks.add(u.getNick()); 
    }
    return nicks;
}

public Usuario obtenerUsuarioPorNickname(String nickname) {
    // Le pedimos el usuario directamente a la base de datos
    return controlPersistencia.obtenerUsuario(nickname);
}

public void modificarUsuario(String nickname, String nuevoNombre, String nuevoApellido, LocalDate nuevaFechaNac) {
    try {
        Usuario u = controlPersistencia.obtenerUsuario(nickname);
        
        if (u != null) {
            u.setNombre(nuevoNombre);
            u.setApellido(nuevoApellido);
            u.setFechaNacimiento(nuevaFechaNac);
            
            // Esta es la línea clave: le avisa a la BD que guarde los nuevos cambios
            controlPersistencia.editarUsuario(u);
        }
    } catch (Exception ex) {
        System.out.println("Error al modificar en BD: " + ex.getMessage());
    }
}

public List<String> obtenerNombresInstitutos() {
        // Le pedimos los objetos completos a la persistencia
        List<Instituto> institutos = controlPersistencia.obtenerInstitutos();
        List<String> nombres = new ArrayList<>();
        
        // Extraemos solo los nombres para mandarlos a la ventana (ComboBox)
        if (institutos != null) {
            for (Instituto inst : institutos) {
                nombres.add(inst.getNombre());
            }
        }
        return nombres;
    }


}


    

