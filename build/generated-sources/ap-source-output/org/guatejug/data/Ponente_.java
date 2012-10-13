package org.guatejug.data;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.guatejug.data.Ponencia;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2012-10-13T02:49:51")
@StaticMetamodel(Ponente.class)
public class Ponente_ { 

    public static volatile SingularAttribute<Ponente, String> nombres;
    public static volatile SingularAttribute<Ponente, Integer> idPonente;
    public static volatile SingularAttribute<Ponente, String> apellidos;
    public static volatile SingularAttribute<Ponente, String> email;
    public static volatile SingularAttribute<Ponente, Ponencia> ponencia;
    public static volatile SingularAttribute<Ponente, String> observaciones;
    public static volatile SingularAttribute<Ponente, String> biografia;

}