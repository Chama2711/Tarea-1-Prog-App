package logica;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import logica.Curso;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-09-30T23:31:54", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Instituto.class)
public class Instituto_ { 

    public static volatile SetAttribute<Instituto, Curso> cursos;
    public static volatile SingularAttribute<Instituto, Long> id;
    public static volatile SingularAttribute<Instituto, String> nombre;

}