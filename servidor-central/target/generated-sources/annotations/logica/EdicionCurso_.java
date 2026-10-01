package logica;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Curso;
import logica.Docente;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(EdicionCurso.class)
public class EdicionCurso_ { 

    public static volatile SingularAttribute<EdicionCurso, LocalDate> fechaInicio;
    public static volatile SingularAttribute<EdicionCurso, Curso> curso;
    public static volatile SingularAttribute<EdicionCurso, LocalDate> fechaPublicacion;
    public static volatile SingularAttribute<EdicionCurso, String> nombre;
    public static volatile SingularAttribute<EdicionCurso, LocalDate> fechaFin;
    public static volatile SetAttribute<EdicionCurso, Docente> docentes;
    public static volatile SingularAttribute<EdicionCurso, Integer> cupo;

}