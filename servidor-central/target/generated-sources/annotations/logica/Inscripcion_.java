package logica;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.EdicionCurso;
import logica.ProgramaFormacion;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Inscripcion.class)
public class Inscripcion_ { 

    public static volatile SingularAttribute<Inscripcion, ProgramaFormacion> programa;
    public static volatile SingularAttribute<Inscripcion, Long> id;
    public static volatile SingularAttribute<Inscripcion, LocalDate> fechaInscripcion;
    public static volatile SingularAttribute<Inscripcion, EdicionCurso> edicion;

}