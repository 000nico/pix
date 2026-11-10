package DataBase.DAO;

import Classes.Users.Administrator;
import Classes.Users.Artist;
import Classes.Users.User;
import DataBase.ConexionDB;
import Enum.RolUsuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// dao de usuarios: el unico que habla con la tabla "usuarios"
public class UsuarioDAO implements DAO<User> {

    // guarda un usuario nuevo y le asigna el id que genero la base
    @Override
    public void insertar(User user) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol, bio, nivel_acceso, activo, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRol().name());

            // la bio es solo de artistas y el nivel de acceso solo de administradores
            if (user instanceof Artist) {
                ps.setString(5, ((Artist) user).getBio());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            if (user instanceof Administrator) {
                ps.setInt(6, ((Administrator) user).getNivelAcceso());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, user.isActivo());
            ps.setDate(8, Date.valueOf(user.getFechaRegistro()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            keys.close();
            ps.close();
        } finally {
            con.close();
        }
    }

    // busca un usuario activo por id. si no existe devuelve null
    @Override
    public User buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            User user = null;
            if (rs.next()) {
                user = mapear(rs);
            }
            rs.close();
            ps.close();
            return user;
        } finally {
            con.close();
        }
    }

    // sirve para el login: busca un usuario activo por su email
    public User buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            User user = null;
            if (rs.next()) {
                user = mapear(rs);
            }
            rs.close();
            ps.close();
            return user;
        } finally {
            con.close();
        }
    }

    // lista solo los usuarios activos, los dados de baja no aparecen
    @Override
    public List<User> listar() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE activo = TRUE";
        List<User> usuarios = new ArrayList<User>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
            rs.close();
            ps.close();
            return usuarios;
        } finally {
            con.close();
        }
    }

    // modifica los datos de un usuario, el rol y la fecha de registro no se tocan
    @Override
    public void actualizar(User user) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ?, bio = ?, nivel_acceso = ? WHERE id = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            if (user instanceof Artist) {
                ps.setString(4, ((Artist) user).getBio());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            if (user instanceof Administrator) {
                ps.setInt(5, ((Administrator) user).getNivelAcceso());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, user.getId());
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // baja logica: el usuario no se borra, solo se marca como inactivo
    @Override
    public void bajaLogica(int id) throws SQLException {
        cambiarActivo(id, false);
    }

    // deshace la baja logica, lo usaria un administrador
    public void reactivar(int id) throws SQLException {
        cambiarActivo(id, true);
    }

    // cambia el campo activo de un usuario
    private void cambiarActivo(int id, boolean activo) throws SQLException {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // convierte una fila de la base en un artist o un administrator segun el rol
    private User mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String email = rs.getString("email");
        String password = rs.getString("password");
        RolUsuario rol = RolUsuario.valueOf(rs.getString("rol"));

        User user;
        if (rol == RolUsuario.ARTISTA) {
            user = new Artist(id, nombre, email, password, rol, rs.getString("bio"));
        } else {
            Administrator admin = new Administrator(id, nombre, email, password, rol);
            admin.setNivelAcceso(rs.getInt("nivel_acceso"));
            user = admin;
        }

        // se pisan los valores del constructor con los reales de la base
        user.setActivo(rs.getBoolean("activo"));
        user.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        return user;
    }
}
