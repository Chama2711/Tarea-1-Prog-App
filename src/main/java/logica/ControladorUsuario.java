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
<<<<<<< Updated upstream:src/main/java/logica/ControladorUsuario.java
import persistencia.ControladorPersistencia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
=======
>>>>>>> Stashed changes:servidor-central/src/main/java/logica/ControladorUsuario.java







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

    public void registrarEstudiante(String ni, String m, String no, String a,
            LocalDate fn, char[] clave, char[] confirmacion) {
        validarDatosAlta(ni, m, no, a, fn, clave, confirmacion);
        Estudiante e = new Estudiante(ni.trim(), m.trim(), no.trim(), a.trim(), fn);
        e.setHashContrasena(Contrasenas.generarHash(clave));
        guardar(e);
    }

public void registrarDocente(String ni, String m, String no, String a,
        LocalDate fn, String nombreInstituto, char[] clave, char[] confirmacion) {
    validarDatosAlta(ni, m, no, a, fn, clave, confirmacion);
    if (nombreInstituto == null || nombreInstituto.isBlank()) {
        throw new IllegalArgumentException("Seleccione el instituto del docente.");
    }
    Docente d = new Docente(ni.trim(), m.trim(), no.trim(), a.trim(), fn);
    d.setHashContrasena(Contrasenas.generarHash(clave));
    guardar(d, nombreInstituto);
}

private void validarDatosAlta(String nick, String mail, String nombre,
        String apellido, LocalDate fecha, char[] clave, char[] confirmacion) {
    if (nick == null || nick.isBlank() || mail == null || mail.isBlank()
            || nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()
            || fecha == null || fecha.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Revise los datos obligatorios y la fecha de nacimiento.");
    }
    Contrasenas.validar(clave, confirmacion);
}

public DTAutenticacion autenticarUsuario(String identificador, char[] clave) {
    if (identificador == null || identificador.isBlank() || clave == null || clave.length == 0) {
        throw new IllegalArgumentException("Nickname, correo o contraseña incorrectos.");
    }
    Usuario usuario = servidorCentral.obtenerUsuarioPorIdentificador(identificador.trim());
    if (usuario == null || !Contrasenas.verificar(clave, usuario.getHashContrasena())) {
        throw new IllegalArgumentException("Nickname, correo o contraseña incorrectos.");
    }
    DTAutenticacion.Rol rol;
    if (usuario instanceof Docente) {
        rol = DTAutenticacion.Rol.DOCENTE;
    } else if (usuario instanceof Estudiante) {
        rol = DTAutenticacion.Rol.ESTUDIANTE;
    } else {
        throw new IllegalArgumentException("Nickname, correo o contraseña incorrectos.");
    }
    return new DTAutenticacion(usuario.getNick(), usuario.getNombre(), rol);
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
    modificarUsuario(nickname, nuevoNombre, nuevoApellido, nuevaFechaNac, null, null);
}

public void modificarUsuario(String nickname, String nuevoNombre,
        String nuevoApellido, LocalDate nuevaFechaNac,
        char[] nuevaClave, char[] confirmacion) {
    if (nuevoNombre == null || nuevoNombre.isBlank() || nuevoApellido == null
            || nuevoApellido.isBlank() || nuevaFechaNac == null
            || nuevaFechaNac.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Revise nombre, apellido y fecha de nacimiento.");
    }
    boolean cambiarClave = (nuevaClave != null && nuevaClave.length > 0)
            || (confirmacion != null && confirmacion.length > 0);
    if (cambiarClave) {
        Contrasenas.validar(nuevaClave, confirmacion);
    }
    try {
        Usuario u = controlPersistencia.obtenerUsuario(nickname);
        if (u == null) throw new IllegalArgumentException("El usuario ya no existe.");
        u.setNombre(nuevoNombre.trim());
        u.setApellido(nuevoApellido.trim());
        u.setFechaNacimiento(nuevaFechaNac);
<<<<<<< Updated upstream:src/main/java/logica/ControladorUsuario.java
        controlPersistencia.editarUsuario(u);
=======
        if (cambiarClave) {
            u.setHashContrasena(Contrasenas.generarHash(nuevaClave));
        }
        servidorCentral.editarUsuario(u);
>>>>>>> Stashed changes:servidor-central/src/main/java/logica/ControladorUsuario.java
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


    

