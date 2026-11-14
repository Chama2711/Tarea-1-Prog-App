    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package soap;

    import jakarta.jws.WebMethod;
    import jakarta.jws.WebParam;
    import jakarta.jws.WebService;
import java.util.List;
    import java.util.Objects;
    import logica.IServidorCentral;


    @WebService(
            serviceName = "CatalogoService",
            portName = "CatalogoPort",
            targetNamespace = "http://soap.edext/"
    )


    public class ServicioCatalogo {
        private final IServidorCentral central;

        public ServicioCatalogo(IServidorCentral central) {
            this.central = Objects.requireNonNull(central);
        }

        @WebMethod(operationName = "listarInstitutos")
        public String[] listarInstitutos() {
            return central.listarNombresInstitutos()
                    .toArray(String[]::new);
        }

        @WebMethod(operationName = "listarInstitutosDocente")
        public String[] listarInstitutosDocente(@WebParam(name = "nickDocente") String nickDocente) {
            return central.listarNombresInstitutosDocente(nickDocente)
                    .toArray(String[]::new);
        }

        @WebMethod(operationName = "listarCategorias")
        public String[] listarCategorias() {
            return central.listarNombresCategorias().toArray(String[]::new);
        }

        @WebMethod(operationName = "listarCategoriasCurso")
        public String[] listarCategoriasCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
            try {
                return central.listarCategoriasCurso(nombreCurso).stream()
                        .map(logica.Categoria::getNombre).toArray(String[]::new);
            } catch (Exception error) {
                throw new IllegalStateException("No se pudieron consultar las categorías del curso.", error);
            }
        }

        @WebMethod(operationName = "listarCategoriasPrograma")
        public String[] listarCategoriasPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) {
            return central.listarNombresCategoriasPrograma(nombrePrograma).toArray(String[]::new);
        }

        @WebMethod(operationName = "listarCursos")
        public String[] listarCursos() {
            List<String> cursos = central.listarNombresCursos();
            if (cursos == null) {
                return new String[0];
            }
            return cursos.toArray(String[]::new);
        }

        @WebMethod(operationName = "listarCursosPorInstituto")
        public String[] listarCursosPorInstituto(@WebParam(name = "nombreInstituto") String nombreInstituto) {
            return central.listarNombresCursosPorInstituto(nombreInstituto).toArray(String[]::new);
        }

        @WebMethod(operationName = "consultarCurso")
        public soap.dto.DTCursoConsulta consultarCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
            return central.consultarCurso(nombreCurso);
        }

        @WebMethod(operationName = "obtenerImagenCurso")
        public byte[] obtenerImagenCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
            try {
                return central.obtenerImagenCurso(nombreCurso);
            } catch (Exception error) {
                throw new IllegalStateException("No se pudo consultar la imagen del curso.", error);
            }
        }

        @WebMethod(operationName = "listarProgramas")
        public soap.dto.DTProgramaConsulta[] listarProgramas() {
            return central.listarProgramasConsulta().toArray(soap.dto.DTProgramaConsulta[]::new);
        }

        @WebMethod(operationName = "consultarPrograma")
        public soap.dto.DTProgramaConsulta consultarPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) {
            return central.consultarPrograma(nombrePrograma);
        }

        @WebMethod(operationName = "estaInscriptoAPrograma")
        public boolean estaInscriptoAPrograma(@WebParam(name = "nickEstudiante") String nickEstudiante,
                @WebParam(name = "programaId") Long programaId) {
            return central.estaInscriptoAPrograma(nickEstudiante, programaId);
        }

        @WebMethod(operationName = "consultarEdicion")
        public soap.dto.DTEdicionConsulta consultarEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion) {
            var edicion = central.consultarEdicion(nombreEdicion);
            return edicion == null ? null : new soap.dto.DTEdicionConsulta(edicion);
        }

        @WebMethod(operationName = "obtenerImagenPrograma")
        public byte[] obtenerImagenPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) {
            try {
                return central.obtenerImagenPrograma(nombrePrograma);
            } catch (Exception error) {
                throw new IllegalStateException("No se pudo consultar la imagen del programa.", error);
            }
        }

        @WebMethod(operationName = "obtenerImagenEdicion")
        public byte[] obtenerImagenEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion) {
            try {
                return central.obtenerImagenEdicion(nombreEdicion);
            } catch (Exception error) {
                throw new IllegalStateException("No se pudo consultar la imagen de la edición.", error);
            }
        }

        @WebMethod(operationName = "autenticarUsuario")
        public soap.dto.DTIdentidad autenticarUsuario(@WebParam(name = "identificador") String identificador, @WebParam(name = "clave") String clave) throws OperacionRechazada {
            return ejecutar(() -> {
                var usuario = central.autenticarUsuario(identificador, clave);
                var identidad = new soap.dto.DTIdentidad(usuario);
                if (usuario.getRol() == logica.DTAutenticacion.Rol.ESTUDIANTE) {
                    identidad.tokenNotas = TokenNotas.emitir(usuario.getNick());
                }
                return identidad;
            });
        }

        @WebMethod(operationName = "listarUsuariosConsulta")
        public soap.dto.DTUsuarioConsulta[] listarUsuariosConsulta() throws OperacionRechazada {
            return ejecutar(() -> central.listarUsuariosConsulta().stream().map(soap.dto.DTUsuarioConsulta::new).toArray(soap.dto.DTUsuarioConsulta[]::new));
        }

        @WebMethod(operationName = "consultarPerfilUsuario")
        public soap.dto.DTPerfilConsulta consultarPerfilUsuario(@WebParam(name = "nick") String nick, @WebParam(name = "nickConsultante") String nickConsultante) throws OperacionRechazada {
            return ejecutar(() -> {
                var perfil = central.consultarPerfilUsuario(nick, nickConsultante);
                return perfil == null ? null : new soap.dto.DTPerfilConsulta(perfil);
            });
        }

        @WebMethod(operationName = "listarSeguidosConsulta")
        public soap.dto.DTUsuarioConsulta[] listarSeguidosConsulta(@WebParam(name = "nick") String nick) throws OperacionRechazada {
            return ejecutar(() -> central.listarSeguidosConsulta(nick).stream().map(soap.dto.DTUsuarioConsulta::new).toArray(soap.dto.DTUsuarioConsulta[]::new));
        }

        @WebMethod(operationName = "esSeguidor")
        public boolean esSeguidor(@WebParam(name = "nickSeguidor") String nickSeguidor, @WebParam(name = "nickSeguido") String nickSeguido) throws OperacionRechazada {
            return ejecutar(() -> central.esSeguidor(nickSeguidor, nickSeguido));
        }

        @WebMethod(operationName = "obtenerImagenUsuario")
        public byte[] obtenerImagenUsuario(@WebParam(name = "nick") String nick) throws OperacionRechazada {
            try {
                return central.obtenerImagenUsuario(nick);
            } catch (Exception error) {
                throw new IllegalStateException("No se pudo consultar la imagen del usuario.", error);
            }
        }

        @WebMethod(operationName = "registrarEstudiante")
        public void registrarEstudiante(@WebParam(name = "nick") String nick, @WebParam(name = "correo") String correo, @WebParam(name = "nombre") String nombre, @WebParam(name = "apellido") String apellido, @WebParam(name = "fechaNacimiento") String fechaNacimiento, @WebParam(name = "clave") String clave, @WebParam(name = "confirmacion") String confirmacion, @WebParam(name = "imagen") byte[] imagen) throws OperacionRechazada {
            ejecutar(() -> { central.registrarEstudiante(nick, correo, nombre, apellido, fecha(fechaNacimiento), clave, confirmacion, imagen); return null; });
        }

        @WebMethod(operationName = "registrarDocente")
        public void registrarDocente(@WebParam(name = "nick") String nick, @WebParam(name = "correo") String correo, @WebParam(name = "nombre") String nombre, @WebParam(name = "apellido") String apellido, @WebParam(name = "fechaNacimiento") String fechaNacimiento, @WebParam(name = "instituto") String instituto, @WebParam(name = "clave") String clave, @WebParam(name = "confirmacion") String confirmacion, @WebParam(name = "imagen") byte[] imagen) throws OperacionRechazada {
            ejecutar(() -> { central.registrarDocente(nick, correo, nombre, apellido, fecha(fechaNacimiento), instituto, clave, confirmacion, imagen); return null; });
        }

        @WebMethod(operationName = "modificarUsuario")
        public void modificarUsuario(@WebParam(name = "nick") String nick, @WebParam(name = "nombre") String nombre, @WebParam(name = "apellido") String apellido, @WebParam(name = "fechaNacimiento") String fechaNacimiento, @WebParam(name = "clave") String clave, @WebParam(name = "confirmacion") String confirmacion) throws OperacionRechazada {
            ejecutar(() -> { central.modificarUsuario(nick, nombre, apellido, fecha(fechaNacimiento), clave, confirmacion); return null; });
        }

        @WebMethod(operationName = "seguirUsuario")
        public void seguirUsuario(@WebParam(name = "nickSeguidor") String nickSeguidor, @WebParam(name = "nickSeguido") String nickSeguido) throws OperacionRechazada {
            ejecutar(() -> { central.seguirUsuario(nickSeguidor, nickSeguido); return null; });
        }

        @WebMethod(operationName = "dejarDeSeguirUsuario")
        public void dejarDeSeguirUsuario(@WebParam(name = "nickSeguidor") String nickSeguidor, @WebParam(name = "nickSeguido") String nickSeguido) throws OperacionRechazada {
            ejecutar(() -> { central.dejarDeSeguirUsuario(nickSeguidor, nickSeguido); return null; });
        }

        @WebMethod(operationName = "altaCursoDocente")
        public void altaCursoDocente(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "instituto") String instituto, @WebParam(name = "nombre") String nombre, @WebParam(name = "descripcion") String descripcion, @WebParam(name = "duracion") String duracion, @WebParam(name = "horas") int horas, @WebParam(name = "creditos") int creditos, @WebParam(name = "url") String url, @WebParam(name = "previas") java.util.List<String> previas, @WebParam(name = "categorias") java.util.List<String> categorias, @WebParam(name = "imagen") byte[] imagen) throws OperacionRechazada {
            ejecutar(() -> { central.altaCursoDocente(nickDocente, instituto, nombre, descripcion, duracion, horas, creditos, url, previas == null ? java.util.List.of() : previas, categorias == null ? java.util.List.of() : categorias, imagen); return null; });
        }

        @WebMethod(operationName = "altaEdicionCursoDocente")
        public void altaEdicionCursoDocente(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "instituto") String instituto, @WebParam(name = "curso") String curso, @WebParam(name = "nombre") String nombre, @WebParam(name = "inicio") String inicio, @WebParam(name = "fin") String fin, @WebParam(name = "cupo") int cupo, @WebParam(name = "docentes") java.util.List<String> docentes, @WebParam(name = "imagen") byte[] imagen) throws OperacionRechazada {
            ejecutar(() -> { central.altaEdicionCursoDocente(nickDocente, instituto, curso, nombre, fecha(inicio), fecha(fin), cupo, docentes == null ? java.util.List.of() : docentes, imagen); return null; });
        }

        @WebMethod(operationName = "altaProgramaFormacion")
        public void altaProgramaFormacion(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "nombre") String nombre, @WebParam(name = "descripcion") String descripcion, @WebParam(name = "inicio") String inicio, @WebParam(name = "fin") String fin, @WebParam(name = "alta") String alta, @WebParam(name = "imagen") byte[] imagen) throws OperacionRechazada {
            ejecutar(() -> {
                exigirDocente(nickDocente);
                central.altaProgramaFormacion(nombre, descripcion, fecha(inicio), fecha(fin), fecha(alta), imagen);
                return null;
            });
        }

        @WebMethod(operationName = "agregarCursoAPrograma")
        public void agregarCursoAPrograma(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "programa") String programa, @WebParam(name = "curso") String curso) throws OperacionRechazada {
            ejecutar(() -> {
                exigirDocente(nickDocente);
                central.agregarCursoAPrograma(programa, curso);
                return null;
            });
        }

        @WebMethod(operationName = "listarNombresProgramas")
        public String[] listarNombresProgramas() throws OperacionRechazada {
            return ejecutar(() -> central.listarNombresProgramas().toArray(String[]::new));
        }

        @WebMethod(operationName = "listarNicknamesDocentesPorInstituto")
        public String[] listarNicknamesDocentesPorInstituto(@WebParam(name = "instituto") String instituto) throws OperacionRechazada {
            return ejecutar(() -> central.listarNicknamesDocentesPorInstituto(instituto).toArray(String[]::new));
        }

        @WebMethod(operationName = "listarEdicionesDocente")
        @jakarta.xml.ws.RequestWrapper(className = "soap.catalogo.jaxws.ListarEdicionesDocente")
        @jakarta.xml.ws.ResponseWrapper(className = "soap.catalogo.jaxws.ListarEdicionesDocenteResponse")
        public String[] listarEdicionesDocente(@WebParam(name = "nickDocente") String nickDocente) throws OperacionRechazada {
            return ejecutar(() -> central.listarEdicionesDocente(nickDocente).toArray(String[]::new));
        }

        @WebMethod(operationName = "inscribirEstudianteAEdicion")
        public void inscribirEstudianteAEdicion(@WebParam(name = "nickEstudiante") String nickEstudiante, @WebParam(name = "nombre") String nombre) throws OperacionRechazada {
            ejecutar(() -> { central.inscribirEstudianteAEdicion(nickEstudiante, nombre); return null; });
        }

        @WebMethod(operationName = "registrarValoracionCurso")
        public void registrarValoracionCurso(
                @WebParam(name = "nickEstudiante") String nickEstudiante,
                @WebParam(name = "nombreCurso") String nombreCurso,
                @WebParam(name = "puntuacion") int puntuacion)
                throws OperacionRechazada {

            ejecutar(() -> {

                if (nombreCurso == null || nombreCurso.isBlank()) {
                    throw new IllegalArgumentException(
                            "Debe indicar el curso que desea valorar."
                    );
                }

                logica.Curso curso =
                        central.buscarCurso(nombreCurso.trim());

                if (curso == null) {
                    throw new logica.RecursoNoEncontradoException(
                            "El curso indicado no existe."
                    );
                }

                central.registrarValoracionCurso(
                        nickEstudiante,
                        curso.getId(),
                        puntuacion
                );

                return null;
            });
        }

        @WebMethod(operationName = "obtenerPromedioValoraciones")
    public double obtenerPromedioValoraciones(
            @WebParam(name = "nombreCurso") String nombreCurso)
            throws OperacionRechazada {

        return ejecutar(() -> {

            if (nombreCurso == null || nombreCurso.isBlank()) {
                throw new IllegalArgumentException(
                        "Debe indicar un curso válido."
                );
            }

            logica.Curso curso =
                    central.buscarCurso(nombreCurso.trim());

            if (curso == null) {
                throw new logica.RecursoNoEncontradoException(
                        "El curso indicado no existe."
                );
            }

            return central.obtenerPromedioValoraciones(
                    curso.getId()
            );
        });
    }

        @WebMethod(operationName = "obtenerValoracionEstudiante")
        public Integer obtenerValoracionEstudiante(
                @WebParam(name = "nickEstudiante") String nickEstudiante,
                @WebParam(name = "nombreCurso") String nombreCurso)
                throws OperacionRechazada {

            return ejecutar(() -> {

                if (nombreCurso == null || nombreCurso.isBlank()) {
                    throw new IllegalArgumentException(
                            "Debe indicar un curso válido."
                    );
                }

                logica.Curso curso =
                        central.buscarCurso(nombreCurso.trim());

                if (curso == null) {
                    throw new logica.RecursoNoEncontradoException(
                            "El curso indicado no existe."
                    );
                }

                return central.obtenerValoracionEstudiante(
                        nickEstudiante,
                        curso.getId()
                );
            });
        }

        @WebMethod(operationName = "inscribirEstudianteAPrograma")
        public void inscribirEstudianteAPrograma(@WebParam(name = "nickEstudiante") String nickEstudiante, @WebParam(name = "nombre") String nombre) throws OperacionRechazada {
            ejecutar(() -> { central.inscribirEstudianteAPrograma(nickEstudiante, nombre); return null; });
        }

        @WebMethod(operationName = "listarEdicionesParaSeleccion")
        public soap.dto.DTEdicionConsulta[] listarEdicionesParaSeleccion(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "instituto") String instituto, @WebParam(name = "curso") String curso) throws OperacionRechazada {
            return ejecutar(() -> central.listarEdicionesParaSeleccion(nickDocente, instituto, curso).stream().map(soap.dto.DTEdicionConsulta::new).toArray(soap.dto.DTEdicionConsulta[]::new));
        }

        @WebMethod(operationName = "listarAceptadosEdicion")
        @jakarta.xml.ws.RequestWrapper(className = "soap.catalogo.jaxws.ListarAceptadosEdicion")
        @jakarta.xml.ws.ResponseWrapper(className = "soap.catalogo.jaxws.ListarAceptadosEdicionResponse")
        public soap.dto.DTInscripcionConsulta[] listarAceptadosEdicion(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "edicion") String edicion) throws OperacionRechazada {
            return ejecutar(() -> central.listarAceptadosEdicion(nickDocente, edicion).stream().map(soap.dto.DTInscripcionConsulta::new).toArray(soap.dto.DTInscripcionConsulta[]::new));
        }

        @WebMethod(operationName = "listarInscripcionesParaSeleccion")
        public soap.dto.DTRankingConsulta[] listarInscripcionesParaSeleccion(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "edicion") String edicion, @WebParam(name = "orden") String orden) throws OperacionRechazada {
            return ejecutar(() -> central.listarInscripcionesParaSeleccion(nickDocente, edicion, logica.OrdenInscripciones.valueOf(orden)).stream().map(soap.dto.DTRankingConsulta::new).toArray(soap.dto.DTRankingConsulta[]::new));
        }

        @WebMethod(operationName = "seleccionarEstudiantes")
        public void seleccionarEstudiantes(@WebParam(name = "nickDocente") String nickDocente, @WebParam(name = "edicion") String edicion, @WebParam(name = "decisiones") java.util.List<soap.dto.DTDecisionInscripcion> decisiones) throws OperacionRechazada {
            ejecutar(() -> {
                var mapa = new java.util.LinkedHashMap<Long, logica.EstadoInscripcion>();
                if (decisiones == null || decisiones.isEmpty()) throw new IllegalArgumentException("Seleccione al menos una inscripción.");
                for (var decision : decisiones) {
                    if (decision == null || decision.id == null || decision.id <= 0 || decision.estado == null
                            || mapa.containsKey(decision.id)) {
                        throw new IllegalArgumentException("La decisión enviada no es válida.");
                    }
                    var estado = logica.EstadoInscripcion.valueOf(decision.estado);
                    if (estado == logica.EstadoInscripcion.INSCRIPTO) {
                        throw new IllegalArgumentException("Seleccione Aceptada o Rechazada.");
                    }
                    mapa.put(decision.id, estado);
                }
                central.seleccionarEstudiantes(nickDocente, edicion, mapa);
                return null;
            });
        }

        private void exigirDocente(String nick) {
            if (nick == null || nick.isBlank() || !(central.obtenerUsuario(nick) instanceof logica.Docente)) {
                throw new SecurityException("Se requiere un docente registrado.");
            }
        }

        private static java.time.LocalDate fecha(String valor) {
            return valor == null || valor.isBlank() ? null : java.time.LocalDate.parse(valor);
        }

        @FunctionalInterface
        private interface Consulta<T> { T realizar() throws Exception; }

        private static <T> T ejecutar(Consulta<T> consulta) throws OperacionRechazada {
            try {
                return consulta.realizar();
            } catch (SecurityException error) {
                throw new OperacionRechazada(error.getMessage(), new soap.dto.DetalleRechazo("ACCESO", error.getMessage()));
            } catch (logica.ValoracionDuplicadaException error) {
                throw new OperacionRechazada(error.getMessage(), new soap.dto.DetalleRechazo("VALORACION_DUPLICADA", error.getMessage()));
            } catch (logica.RecursoNoEncontradoException error) {
                throw new OperacionRechazada(error.getMessage(), new soap.dto.DetalleRechazo("NO_ENCONTRADO", error.getMessage()));
            } catch (IllegalArgumentException error) {
                throw new OperacionRechazada(error.getMessage(), new soap.dto.DetalleRechazo("VALIDACION", error.getMessage()));
            } catch (Exception error) {
                // La API anterior usa Exception para el curso ya asociado al programa.
                if (error.getClass() == Exception.class) {
                    throw new OperacionRechazada(error.getMessage(), new soap.dto.DetalleRechazo("VALIDACION", error.getMessage()));
                }
                throw new IllegalStateException("No se pudo completar la operación en el servidor central.", error);
            }
        }
    }
