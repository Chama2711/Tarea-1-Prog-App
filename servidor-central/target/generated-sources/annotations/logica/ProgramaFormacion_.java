package logica;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Curso;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(ProgramaFormacion.class)
public class ProgramaFormacion_ { 

    public static volatile SingularAttribute<ProgramaFormacion, String> descripcion;
    public static volatile MapAttribute<ProgramaFormacion, String, Curso> cursos;
    public static volatile SingularAttribute<ProgramaFormacion, LocalDate> fechaAlta;
    public static volatile SingularAttribute<ProgramaFormacion, LocalDate> fechaInicio;
    public static volatile SingularAttribute<ProgramaFormacion, Long> id;
    public static volatile SingularAttribute<ProgramaFormacion, String> nombre;
    public static volatile SingularAttribute<ProgramaFormacion, LocalDate> fechaFin;

}