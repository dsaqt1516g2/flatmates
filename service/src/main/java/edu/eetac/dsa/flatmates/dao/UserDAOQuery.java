package edu.eetac.dsa.flatmates.dao;

/**
 * Created by Admin on 09/11/2015.
 */
public interface UserDAOQuery {
    public final static String UUID = "select REPLACE(UUID(),'-','')";
    public final static String CREATE_USER_HOMBRE = "insert into users (id, loginid, password, email, fullname, sexo, info) values (UNHEX(?), ?, UNHEX(MD5(?)), ?, ?, 'hombre', ?)";
    public final static String CREATE_USER_MUJER = "insert into users (id, loginid, password, email, fullname, sexo, info) values (UNHEX(?), ?, UNHEX(MD5(?)), ?, ?, 'mujer', ?)";
    public final static String UPDATE_USER = "update users set email=?, fullname=?, info=? where id=unhex(?)";
    public final static String ASSIGN_ROLE_REGISTERED = "insert into user_roles (userid, role) values (UNHEX(?), 'registered')";
    public final static String ASSIGN_ROLE_ADMIN = "insert into user_roles (userid, role) values (UNHEX(?), 'admin')";
    public final static String GET_USER_BY_ID = "select hex(u.id) as id, u.loginid, u.email, u.fullname, u.sexo, u.info from users u where id=unhex(?)";
    public final static String GET_USER_BY_USERNAME = "select hex(u.id) as id, u.loginid, u.email, u.fullname, u.sexo, u.info from users u where u.loginid=?";
    public final static String DELETE_USER = "delete from users where id=unhex(?)";
    public final static String GET_PASSWORD =  "select hex(password) as password from users where id=unhex(?)";
    public final static String PUNTOS = "insert into puntos_totales (id, loginid, puntos) values(UNHEX(?), UNHEX(?), '0')";

}