package logica;

import java.util.Set;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Curso;
import logica.EdicionCurso;
import logica.Instituto;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Docente.class)
public class Docente_ extends Usuario_ {

    public static volatile SetAttribute<Docente, Curso> cursos;
    public static volatile SetAttribute<Docente, EdicionCurso> ediciones;
    public static volatile SetAttribute<Docente, Instituto> institutos;
    public static volatile SingularAttribute<Docente, Set> edicionesAsignadas;
    public static volatile SingularAttribute<Docente, Set> cursosAsignados;
    public static volatile SingularAttribute<Docente, Set> programasAsignados;

}