package logica;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Categoria;
import logica.Curso;
import logica.EdicionCurso;
import logica.Instituto;
import logica.ProgramaFormacion;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Curso.class)
public class Curso_ { 

    public static volatile SingularAttribute<Curso, String> descripcion;
    public static volatile SetAttribute<Curso, Curso> previas;
    public static volatile SingularAttribute<Curso, Integer> cantidadHoras;
    public static volatile SingularAttribute<Curso, LocalDate> fechaRegistro;
    public static volatile SetAttribute<Curso, Categoria> categorias;
    public static volatile SingularAttribute<Curso, String> nombre;
    public static volatile SingularAttribute<Curso, String> url;
    public static volatile SingularAttribute<Curso, Instituto> instituto;
    public static volatile SetAttribute<Curso, EdicionCurso> ediciones;
    public static volatile SetAttribute<Curso, ProgramaFormacion> programas;
    public static volatile SingularAttribute<Curso, String> duracion;
    public static volatile SingularAttribute<Curso, Long> id;
    public static volatile SingularAttribute<Curso, Integer> creditos;

}