package logica;

import java.util.Set;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Inscripcion;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Estudiante.class)
public class Estudiante_ extends Usuario_ {

    public static volatile SingularAttribute<Estudiante, Set> edicionesInscriptas;
    public static volatile SingularAttribute<Estudiante, Set> programasInscriptos;
    public static volatile SetAttribute<Estudiante, Inscripcion> inscripciones;

}