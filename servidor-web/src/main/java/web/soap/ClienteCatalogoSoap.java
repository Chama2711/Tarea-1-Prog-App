package web.soap;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import web.soap.generado.CatalogoService;
import web.soap.generado.ServicioCatalogo;
import web.soap.generado.DtCursoConsulta;
import web.soap.generado.DtProgramaConsulta;
import web.soap.generado.DtEdicionConsulta;

/** Consulta el catálogo mediante el servicio SOAP del central. */
public final class ClienteCatalogoSoap {
    private final CatalogoService servicio;

    public ClienteCatalogoSoap() throws IOException {
        this(Path.of(System.getProperty("user.home"), ".edEXT", "web.properties"));
    }

    ClienteCatalogoSoap(Path archivoConfiguracion) throws IOException {
        this(leerWsdl(archivoConfiguracion));
    }

    public ClienteCatalogoSoap(URL wsdl) {
        servicio = new CatalogoService(Objects.requireNonNull(wsdl));
    }

    public List<String> listarInstitutos() {
        ServicioCatalogo puerto = servicio.getCatalogoPort();
        List<String> nombres = puerto.listarInstitutos();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarInstitutosDocente(String nickDocente) {
        ServicioCatalogo puerto = servicio.getCatalogoPort();
        List<String> nombres = puerto.listarInstitutosDocente(nickDocente);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategorias() {
        List<String> nombres = servicio.getCatalogoPort().listarCategorias();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategoriasCurso(String nombreCurso) {
        List<String> nombres = servicio.getCatalogoPort().listarCategoriasCurso(nombreCurso);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategoriasPrograma(String nombrePrograma) {
        List<String> nombres = servicio.getCatalogoPort().listarCategoriasPrograma(nombrePrograma);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCursos() {
        List<String> nombres = servicio.getCatalogoPort().listarCursos();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCursosPorInstituto(String nombreInstituto) {
        List<String> nombres = servicio.getCatalogoPort().listarCursosPorInstituto(nombreInstituto);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public DtCursoConsulta consultarCurso(String nombreCurso) {
        return servicio.getCatalogoPort().consultarCurso(nombreCurso);
    }

    public byte[] generarCertificadoPrograma(String nickEstudiante, String nombrePrograma) {
        return ejecutar(() -> servicio.getCatalogoPort().generarCertificadoPrograma(nickEstudiante, nombrePrograma));
    }

    public byte[] obtenerImagenCurso(String nombreCurso) {
        return servicio.getCatalogoPort().obtenerImagenCurso(nombreCurso);
    }

    public List<DtProgramaConsulta> listarProgramas() {
        var programas = servicio.getCatalogoPort().listarProgramas();
        return programas == null ? List.of() : List.copyOf(programas);
    }

    public DtProgramaConsulta consultarPrograma(String nombrePrograma) {
        return servicio.getCatalogoPort().consultarPrograma(nombrePrograma);
    }

    public boolean estaInscriptoAPrograma(String nickEstudiante, Long programaId) {
        return servicio.getCatalogoPort().estaInscriptoAPrograma(nickEstudiante, programaId);
    }

    public DtEdicionConsulta consultarEdicion(String nombreEdicion) {
        return servicio.getCatalogoPort().consultarEdicion(nombreEdicion);
    }

    public byte[] obtenerImagenPrograma(String nombrePrograma) {
        return servicio.getCatalogoPort().obtenerImagenPrograma(nombrePrograma);
    }

    public byte[] obtenerImagenEdicion(String nombreEdicion) {
        return servicio.getCatalogoPort().obtenerImagenEdicion(nombreEdicion);
    }

    public logica.DTAutenticacion autenticarUsuario(String identificador, String clave) {
        var identidad = ejecutar(() -> servicio.getCatalogoPort().autenticarUsuario(identificador, clave));
        if (identidad == null) throw new jakarta.xml.ws.WebServiceException("El central no devolvió una identidad.");
        return new logica.DTAutenticacion(identidad.getNick(), identidad.getNombre(),
                logica.DTAutenticacion.Rol.valueOf(identidad.getRol()));
    }

    public List<logica.DTUsuarioConsulta> listarUsuariosConsulta() {
        return ejecutar(() -> servicio.getCatalogoPort().listarUsuariosConsulta()).stream()
                .map(ClienteCatalogoSoap::usuario).toList();
    }

    public logica.DTPerfilUsuario consultarPerfilUsuario(String nick, String consultante) {
        var dato = ejecutar(() -> servicio.getCatalogoPort().consultarPerfilUsuario(nick, consultante));
        return dato == null ? null : new logica.DTPerfilUsuario(usuario(dato.getUsuario()), dato.isPropio(),
                dato.getInscripciones().stream().map(ClienteCatalogoSoap::inscripcion).toList(),
                dato.getEdiciones().stream().map(ClienteCatalogoSoap::edicion).toList(), dato.getProgramas(),
                dato.getAceptados().stream().map(ClienteCatalogoSoap::inscripcion).toList());
    }

    public List<logica.DTUsuarioConsulta> listarSeguidosConsulta(String nick) {
        return ejecutar(() -> servicio.getCatalogoPort().listarSeguidosConsulta(nick)).stream()
                .map(ClienteCatalogoSoap::usuario).toList();
    }

    public boolean esSeguidor(String seguidor, String seguido) {
        return ejecutar(() -> servicio.getCatalogoPort().esSeguidor(seguidor, seguido));
    }

    public byte[] obtenerImagenUsuario(String nick) {
        return ejecutar(() -> servicio.getCatalogoPort().obtenerImagenUsuario(nick));
    }

    public void registrarEstudiante(String nick, String correo, String nombre, String apellido,
            java.time.LocalDate nacimiento, String clave, String confirmacion, byte[] imagen) {
        ejecutar(() -> { servicio.getCatalogoPort().registrarEstudiante(nick, correo, nombre, apellido,
                textoFecha(nacimiento), clave, confirmacion, imagen); return null; });
    }

    public void registrarDocente(String nick, String correo, String nombre, String apellido,
            java.time.LocalDate nacimiento, String instituto, String clave, String confirmacion, byte[] imagen) {
        ejecutar(() -> { servicio.getCatalogoPort().registrarDocente(nick, correo, nombre, apellido,
                textoFecha(nacimiento), instituto, clave, confirmacion, imagen); return null; });
    }

    public void modificarUsuario(String nick, String nombre, String apellido, java.time.LocalDate nacimiento,
            String clave, String confirmacion) {
        ejecutar(() -> { servicio.getCatalogoPort().modificarUsuario(nick, nombre, apellido,
                textoFecha(nacimiento), clave, confirmacion); return null; });
    }

    public void seguirUsuario(String seguidor, String seguido) {
        ejecutar(() -> { servicio.getCatalogoPort().seguirUsuario(seguidor, seguido); return null; });
    }

    public void dejarDeSeguirUsuario(String seguidor, String seguido) {
        ejecutar(() -> { servicio.getCatalogoPort().dejarDeSeguirUsuario(seguidor, seguido); return null; });
    }

    public List<String> listarNombresCursos() { return listarCursos(); }
    public List<String> listarNombresCursosPorInstituto(String instituto) { return listarCursosPorInstituto(instituto); }
    public List<String> listarNombresProgramas() {
        return List.copyOf(ejecutar(() -> servicio.getCatalogoPort().listarNombresProgramas()));
    }
    public List<String> listarNicknamesDocentesPorInstituto(String instituto) {
        return List.copyOf(ejecutar(() -> servicio.getCatalogoPort().listarNicknamesDocentesPorInstituto(instituto)));
    }
    public List<String> listarEdicionesDocente(String nick) {
        return List.copyOf(ejecutar(() -> servicio.getCatalogoPort().listarEdicionesDocente(nick)));
    }

    public void altaCursoDocente(String nick, String instituto, String nombre, String descripcion,
            String duracion, int horas, int creditos, String url, List<String> previas,
            List<String> categorias, byte[] imagen) {
        ejecutar(() -> { servicio.getCatalogoPort().altaCursoDocente(nick, instituto, nombre, descripcion,
                duracion, horas, creditos, url, previas, categorias, imagen); return null; });
    }

    public void altaEdicionCursoDocente(String nick, String instituto, String curso, String nombre,
            java.time.LocalDate inicio, java.time.LocalDate fin, int cupo, List<String> docentes, byte[] imagen) {
        ejecutar(() -> { servicio.getCatalogoPort().altaEdicionCursoDocente(nick, instituto, curso, nombre,
                textoFecha(inicio), textoFecha(fin), cupo, docentes, imagen); return null; });
    }

    public void altaProgramaFormacion(String nickDocente, String nombre, String descripcion,
            java.time.LocalDate inicio, java.time.LocalDate fin, java.time.LocalDate alta, byte[] imagen) {
        ejecutar(() -> { servicio.getCatalogoPort().altaProgramaFormacion(nickDocente, nombre, descripcion,
                textoFecha(inicio), textoFecha(fin), textoFecha(alta), imagen); return null; });
    }

    public void agregarCursoAPrograma(String nickDocente, String programa, String curso) {
        ejecutar(() -> { servicio.getCatalogoPort().agregarCursoAPrograma(nickDocente, programa, curso); return null; });
    }

    public void inscribirEstudianteAEdicion(String nick, String nombre) {
        ejecutar(() -> { servicio.getCatalogoPort().inscribirEstudianteAEdicion(nick, nombre); return null; });
    }

    public void inscribirEstudianteAPrograma(String nick, String nombre) {
        ejecutar(() -> { servicio.getCatalogoPort().inscribirEstudianteAPrograma(nick, nombre); return null; });
    }

    public List<logica.DTEdicionConsulta> listarEdicionesParaSeleccion(String nick, String instituto, String curso) {
        return ejecutar(() -> servicio.getCatalogoPort().listarEdicionesParaSeleccion(nick, instituto, curso))
                .stream().map(ClienteCatalogoSoap::edicion).toList();
    }

    public List<logica.DTInscripcion> listarAceptadosEdicion(String nick, String nombreEdicion) {
        return ejecutar(() -> servicio.getCatalogoPort().listarAceptadosEdicion(nick, nombreEdicion))
                .stream().map(ClienteCatalogoSoap::inscripcion).toList();
    }

    public List<logica.DTRankingInscripcion> listarInscripcionesParaSeleccion(String nick, String nombreEdicion,
            logica.OrdenInscripciones orden) {
        return ejecutar(() -> servicio.getCatalogoPort().listarInscripcionesParaSeleccion(nick, nombreEdicion, orden.name()))
                .stream().map(d -> new logica.DTRankingInscripcion(inscripcion(d.getInscripcion()), d.getRechazosAnteriores()))
                .toList();
    }

    public void seleccionarEstudiantes(String nick, String nombreEdicion, java.util.Map<Long, logica.EstadoInscripcion> decisiones) {
        var datos = decisiones.entrySet().stream().map(e -> {
            var dto = new web.soap.generado.DtDecisionInscripcion();
            dto.setId(e.getKey()); dto.setEstado(e.getValue().name()); return dto;
        }).toList();
        ejecutar(() -> { servicio.getCatalogoPort().seleccionarEstudiantes(nick, nombreEdicion, datos); return null; });
    }

    public void registrarValoracionCurso(String nickEstudiante, String nombreCurso, int puntuacion) {
        ejecutar(() -> {
            servicio.getCatalogoPort().registrarValoracionCurso(
                    nickEstudiante,
                    nombreCurso,
                    puntuacion
            );
            return null;
        });
    }

    public double obtenerPromedioValoraciones(String nombreCurso) {
        return ejecutar(() ->
                servicio.getCatalogoPort()
                        .obtenerPromedioValoraciones(nombreCurso)
        );
    }

    public Integer obtenerValoracionEstudiante(
            String nickEstudiante,
            String nombreCurso) {

        return ejecutar(() ->
                servicio.getCatalogoPort().obtenerValoracionEstudiante(
                        nickEstudiante,
                        nombreCurso
                )
        );
    }

    private static logica.DTUsuarioConsulta usuario(web.soap.generado.DtUsuarioConsulta d) {
        return new logica.DTUsuarioConsulta(d.getNick(), d.getNombre(), d.getApellido(), d.getCorreo(),
                fecha(d.getFechaNacimiento()), logica.DTAutenticacion.Rol.valueOf(d.getRol()), d.isTieneImagen(), d.getInstitutos());
    }

    private static logica.DTInscripcion inscripcion(web.soap.generado.DtInscripcionConsulta d) {
        return new logica.DTInscripcion(d.getId(), d.getNicknameEstudiante(), d.getNombreEstudiante(),
                d.getNombreEdicion(), d.getNombreCurso(), fecha(d.getFechaInscripcion()),
                logica.EstadoInscripcion.valueOf(d.getEstado()));
    }

    private static logica.DTEdicionConsulta edicion(web.soap.generado.DtEdicionConsulta d) {
        return new logica.DTEdicionConsulta(d.getNombre(), d.getNombreCurso(), fecha(d.getInicio()), fecha(d.getFin()),
                fecha(d.getPublicacion()), d.getCupo(), d.isVigente(), d.isTieneImagen(),
                d.getDocente().stream().map(doc -> new logica.DTUsuarioConsulta(doc.getNick(), doc.getNombre(),
                        doc.getApellido(), null, null, logica.DTAutenticacion.Rol.DOCENTE, false, List.of())).toList());
    }

    private static String textoFecha(java.time.LocalDate fecha) { return fecha == null ? null : fecha.toString(); }
    private static java.time.LocalDate fecha(String fecha) {
        return fecha == null || fecha.isBlank() ? null : java.time.LocalDate.parse(fecha);
    }

    @FunctionalInterface
    private interface Llamada<T> { T invocar() throws web.soap.generado.OperacionRechazada; }

    private static <T> T ejecutar(Llamada<T> llamada) {
        try {
            return llamada.invocar();
        } catch (web.soap.generado.OperacionRechazada error) {
            var detalle = error.getFaultInfo();
            if (detalle == null) throw new jakarta.xml.ws.WebServiceException("Respuesta SOAP de error incompleta.", error);
            switch (detalle.getCodigo()) {
                case "ACCESO" -> throw new SecurityException(detalle.getMensaje(), error);
                case "NO_ENCONTRADO" -> throw new logica.RecursoNoEncontradoException(detalle.getMensaje());
                case "VALIDACION" -> throw new IllegalArgumentException(detalle.getMensaje(), error);
                case "VALORACION_DUPLICADA" -> throw new logica.ValoracionDuplicadaException(detalle.getMensaje(), error);
                default -> throw new jakarta.xml.ws.WebServiceException("No se pudo completar la operación SOAP.", error);
            }
        }
    }

    private static URL leerWsdl(Path archivo) throws IOException {
        Properties configuracion = new Properties();
        try (var lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            configuracion.load(lector);
        }
        String valor = configuracion.getProperty("soap.catalogo.wsdl");
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta configurar soap.catalogo.wsdl en " + archivo);
        }
        URI direccion = URI.create(valor.trim());
        if (!("http".equalsIgnoreCase(direccion.getScheme())
                || "https".equalsIgnoreCase(direccion.getScheme())) || direccion.getHost() == null) {
            throw new IllegalArgumentException("soap.catalogo.wsdl debe ser una URL HTTP o HTTPS válida.");
        }
        return direccion.toURL();
    }
}
