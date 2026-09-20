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
    
public void modificarDatosUsuario(String nickname, String nuevoNombre,
        String nuevoApellido, LocalDate nuevaFecha) {
    modificarUsuario(nickname, nuevoNombre, nuevoApellido, nuevaFecha);
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

public void registrarDocente(String ni, String m, String no, String a,
        LocalDate fn, String nombreInstituto) {
    Docente d = new Docente(ni, m, no, a, fn);
    guardar(d, nombreInstituto);
}

private void guardar(Usuario u) {
    guardar(u, null);
}

private void guardar(Usuario u, String nombreInstituto) {
    try {
        controlPersistencia.crearUsuario(u, nombreInstituto);
    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new IllegalStateException("No se pudo guardar el usuario.", e);
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

public void modificarUsuario(String nickname, String nuevoNombre,
        String nuevoApellido, LocalDate nuevaFechaNac) {
    if (nuevoNombre == null || nuevoNombre.isBlank() || nuevoApellido == null
            || nuevoApellido.isBlank() || nuevaFechaNac == null
            || nuevaFechaNac.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Revise nombre, apellido y fecha de nacimiento.");
    }
    try {
        Usuario u = controlPersistencia.obtenerUsuario(nickname);
        if (u == null) throw new IllegalArgumentException("El usuario ya no existe.");
        u.setNombre(nuevoNombre.trim());
        u.setApellido(nuevoApellido.trim());
        u.setFechaNacimiento(nuevaFechaNac);
        controlPersistencia.editarUsuario(u);
    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new IllegalStateException("No se pudo modificar el usuario.", e);
    }
}

    public List<String> listarCursosOEdicionesUsuario(String nick) {
        return controlPersistencia.listarCursosOEdicionesUsuario(nick);
    }

    public List<String> listarProgramasUsuario(String nick) {
        return controlPersistencia.listarProgramasUsuario(nick);
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


    

