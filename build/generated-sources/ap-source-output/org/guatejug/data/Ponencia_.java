package org.guatejug.data;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.guatejug.data.Ponente;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2012-10-27T20:05:55")
@StaticMetamodel(Ponencia.class)
public class Ponencia_ { 

    public static volatile SingularAttribute<Ponencia, Short> categoria;
    public static volatile SingularAttribute<Ponencia, String> titulo;
    public static volatile SingularAttribute<Ponencia, Short> datashow;
    public static volatile SingularAttribute<Ponencia, String> otros;
    public static volatile SingularAttribute<Ponencia, Short> tipo;
    public static volatile SingularAttribute<Ponencia, Short> laptop;
    public static volatile SingularAttribute<Ponencia, Integer> idPonencia;
    public static volatile ListAttribute<Ponencia, Ponente> ponenteList;
    public static volatile SingularAttribute<Ponencia, Short> nivel;
    public static volatile SingularAttribute<Ponencia, String> resumen;
    public static volatile SingularAttribute<Ponencia, Short> duracion;

}