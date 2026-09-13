/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;

/**
 *
 * @author adrie
 */
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;



public class ControladorUsuario {
    private Map<String, Usuario> usuariosByNick = new HashMap<>();
    private Map<String, Usuario> usuariosByMail = new HashMap<>();

    public boolean existeNickname(String nick) {
        return usuariosByNick.containsKey(nick.toLowerCase());
    }

    public boolean existeCorreo(String mail) {
        return usuariosByMail.containsKey(mail.toLowerCase());
    }

    public void registrarEstudiante(String nick, String mail, String nombre, String apellido, String fechaNac) {
        Estudiante e = new Estudiante(nick, mail, nombre, apellido, fechaNac);
        guardar(e);
    }

    public void registrarDocente(String nick, String mail, String nombre, String apellido, String fechaNac, Instituto inst) {
        Docente d = new Docente(nick, mail, nombre, apellido, fechaNac, inst);
        guardar(d);
    }

    private void guardar(Usuario u) {
        usuariosByNick.put(u.getNick().toLowerCase(), u);
        usuariosByMail.put(u.getMail().toLowerCase(), u);
    }
    
    
    
    
    // Obtener la lista de nicknames usando las claves del map usuariosByNick
public List<String> listarNicknamesUsuarios() {
    return new ArrayList<>(usuariosByNick.keySet());
}

// Buscar el usuario directo por su nickname en el map usuariosByNick
public Usuario obtenerUsuarioPorNickname(String nickname) {
    return usuariosByNick.get(nickname);
}

public void modificarUsuario(String nickname, String nuevoNombre, String nuevoApellido, String nuevaFechaNac) {
    Usuario u = usuariosByNick.get(nickname);
    if (u != null) {
        u.setNombre(nuevoNombre);
        u.setApellido(nuevoApellido);
        u.setFechaNac(nuevaFechaNac); 
    }
}



    
}
