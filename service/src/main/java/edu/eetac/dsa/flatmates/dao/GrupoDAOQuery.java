package edu.eetac.dsa.flatmates.dao;

/**
 * Created by Admin on 21/11/2015.
 */
public interface GrupoDAOQuery {
    public final static String UUID = "select REPLACE(UUID(),'-','')";
    public final static String CREATE_GRUPO = "insert into grupo (id, nombre, info, admin) values (UNHEX(?), ?, ?, UNHEX(?))";
    public final static String GET_GRUPO_BY_ID = "select hex(g.id) as id, g.nombre, g.info, hex(g.admin) as g.admin, g.creation_timestamp, g.last_modified from grupo g, users u where g.id=unhex(?) and u.id=g.userid";
    public final static String GET_GRUPOS = "select hex(id) as id, nombre, info, hex(admin), creation_timestamp, last_modified from grupo";
    public final static String DELETE_GRUPO = "delete from grupo where id=unhex(?)";
    public final static String DEL_GRUPO = "update from users set grupoid = NULL where grupoid=unhex(?) and id=unhex(?)";
    public final static String ADD_GRUPO = "update from users set grupoid = ? where id = ?";
    public final static String PUNTOS =  "insert into puntos_grupo (id, loginid, grupoid, puntos) values(UNHEX(?), UNHEX(?), UNHEX(?), '0')";
}