package edu.eetac.dsa.flatmates.dao;

import edu.eetac.dsa.flatmates.entity.ColeccionUser;
import edu.eetac.dsa.flatmates.entity.User;

import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by Admin on 09/11/2015.
 */
public class UserDAOImpl implements UserDAO{

    @Context
    private Application app;
    @Override
    public User createUser(String loginid, String password, String email, String fullname, String info, boolean sexo, InputStream imagen) throws SQLException, UserAlreadyExistsException {
        Connection connection = null;
        PreparedStatement stmt = null;
        String id = null;
        User user=null;
        UUID uuid =writeAndConvertImage(imagen);
        try {

            user = getUserByLoginid(loginid);
            if (user != null)
                throw new UserAlreadyExistsException();
            connection = Database.getConnection();
            stmt = connection.prepareStatement(UserDAOQuery.UUID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                id = rs.getString(1);
            else
                throw new SQLException();

            connection.setAutoCommit(false);
            stmt.close();

            if (sexo == true) {
                stmt = connection.prepareStatement(UserDAOQuery.CREATE_USER_HOMBRE);
            }
            else{
                stmt = connection.prepareStatement(UserDAOQuery.CREATE_USER_MUJER);
            }
            stmt.setString(1, id);
            stmt.setString(2, loginid);
            stmt.setString(3, password);
            stmt.setString(4, email);
            stmt.setString(5, fullname);
            stmt.setString(6, info);
            stmt.setString(7, uuid.toString());

            stmt.executeUpdate();

            stmt.close();

            stmt = connection.prepareStatement(UserDAOQuery.ASSIGN_ROLE_REGISTERED);
            stmt.setString(1, id);
            stmt.executeUpdate();
            connection.commit();

            stmt.close();

        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }

        return getUserById(id);
    }

    private UUID writeAndConvertImage(InputStream file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);

        } catch (IOException e) {
            throw new InternalServerErrorException(
                    "Something has been wrong when reading the file.");
        }
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString() + ".png";

        try {
            PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("flatmates");
            ImageIO.write(image, "png", new File(prb.getString("uploadFolder") + filename));
        } catch (IOException e) {
            throw new InternalServerErrorException(
                    "Something has been wrong when converting the file.");
        }

        return uuid;
    }
    @Override
    public User updateProfile(String id, String email, String fullname, String info) throws SQLException {
        User user = null;

        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.UPDATE_USER);
            stmt.setString(1, email);
            stmt.setString(2, fullname);
            stmt.setString(3, info);
            stmt.setString(4, id);
            int rows = stmt.executeUpdate();
            if (rows == 1)
                user = getUserById(id);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        return user;
    }

    @Override
    public User updatePassword (String id, String password) throws SQLException{
        User user = null;

        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.UPDATE_PASSWORD);
            stmt.setString(1, password);
            stmt.setString(2, id);
            int rows = stmt.executeUpdate();
            if (rows == 1)
                user = getUserById(id);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        return user;
    }
    @Override
    public User getUserById(String id) throws SQLException {
        // Modelo a devolver
        User user = null;
        PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("flatmates");
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            // Obtiene la conexión del DataSource
            connection = Database.getConnection();

            // Prepara la consulta
            stmt = connection.prepareStatement(UserDAOQuery.GET_USER_BY_ID);
            // Da valor a los parámetros de la consulta
            stmt.setString(1, id);

            // Ejecuta la consulta
            ResultSet rs = stmt.executeQuery();
            // Procesa los resultados
            if (rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setLoginid(rs.getString("loginid"));
                user.setEmail(rs.getString("email"));
                user.setFullname(rs.getString("fullname"));
                user.setTareas(rs.getInt("tareas"));
                user.setSexo(rs.getString("sexo"));
                user.setInfo(rs.getString("info"));
                user.setPuntos(rs.getInt("puntos"));
                user.setFilename(rs.getString("imagen")+ ".png");
                user.setImageURL(prb.getString("imgBaseURL")+ user.getFilename());
                //user.setFilename(prb.getString("imgBaseURL")+ rs.getString("imagen") + ".png");

                //user.setImageURL(prb.getString("imgBaseURL")+ rs.getString("imageURL") + ".png");
            }
        } catch (SQLException e) {
            // Relanza la excepción
            throw e;
        } finally {
            // Libera la conexión
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        // Devuelve el modelo
        return user;
    }

    @Override

    public ColeccionUser getUsersByLogin_root (String login) throws SQLException{
        ColeccionUser coleccionUser = new ColeccionUser();
        PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("flatmates");
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            String QUERY = "select hex(id) as id, loginid, email, fullname, sexo, info, tareas, puntos, imagen from users ";
            QUERY = QUERY.concat("where loginid like '%").concat(login)
                    .concat("%' ");
            connection = Database.getConnection();
            stmt = connection.prepareStatement(QUERY);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setLoginid(rs.getString("loginid"));
                user.setEmail(rs.getString("email"));
                user.setFullname(rs.getString("fullname"));
                user.setTareas(rs.getInt("tareas"));
                user.setSexo(rs.getString("sexo"));
                user.setInfo(rs.getString("info"));
                user.setPuntos(rs.getInt("puntos"));
                user.setFilename(rs.getString("imagen")+ ".png");
                user.setImageURL(prb.getString("imgBaseURL")+ user.getFilename());
                coleccionUser.getUsers().add(user);
                //user.setFilename(prb.getString("imgBaseURL")+ rs.getString("imagen") + ".png");

                //user.setImageURL(prb.getString("imgBaseURL")+ rs.getString("imageURL") + ".png");
            }
        } catch (SQLException e) {
            // Relanza la excepción
            throw e;
        } finally {
            // Libera la conexión
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        // Devuelve el modelo
        return coleccionUser;
    }

    @Override
    public User getUserByLoginid(String loginid) throws SQLException {
        User user = null;
        PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("flatmates");
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.GET_USER_BY_USERNAME);
            stmt.setString(1, loginid);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                user = new User();
                user.setId(rs.getString("id"));
                user.setLoginid(rs.getString("loginid"));
                user.setEmail(rs.getString("email"));
                user.setFullname(rs.getString("fullname"));
                user.setInfo(rs.getString("info"));
                user.setTareas(rs.getInt("tareas"));
                user.setPuntos(rs.getInt("puntos"));
                user.setFilename(rs.getString("imagen")+ ".png");
                user.setImageURL(prb.getString("imgBaseURL")+ user.getFilename());

            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        return user;
    }

    @Override
    public boolean deleteUser(String id) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.DELETE_USER);
            stmt.setString(1, id);

            int rows = stmt.executeUpdate();
            return (rows == 1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }
    }

    @Override
    public boolean checkPassword(String id, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.GET_PASSWORD);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(password.getBytes());
                    String passedPassword = new BigInteger(1, md.digest()).toString(16);

                    return passedPassword.equalsIgnoreCase(storedPassword);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

    }
    @Override
    public User updatePuntos(String loginid, int puntos) throws SQLException {
        User user = null;

        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = Database.getConnection();

            stmt = connection.prepareStatement(UserDAOQuery.SET_PUNTOS);
            stmt.setInt(1, puntos);
            stmt.setString(2, loginid);
            int rows = stmt.executeUpdate();
            if (rows == 1)
                user= getUserById(loginid);
        } catch (SQLException ex){
            throw ex;
        } finally {
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }

        return user;
    }


}