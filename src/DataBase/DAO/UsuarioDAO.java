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
import java.util.Optional;


public class UsuarioDAO implements DAO<User> {

    // Guarda un usuario nuevo y le asigna el id que generó la base.
    @Override
    public void insertar(User user) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol, bio, nivel_acceso, activo, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRol().name());

            // bio es solo de artistas y nivel_acceso solo de administradores
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

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
    }

    // Busca un usuario activo por id. Si no existe, devuelve Optional vacío.
    @Override
    public Optional<User> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND activo = TRUE";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    // Sirve para el login: busca un usuario activo por su email.
    public Optional<User> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND activo = TRUE";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    // Lista solo los usuarios activos (los dados de baja no aparecen).
    @Override
    public List<User> listar() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE activo = TRUE";
        List<User> usuarios = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    // Modifica los datos de un usuario (el rol y la fecha de registro no se tocan).
    @Override
    public void actualizar(User user) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ?, bio = ?, nivel_acceso = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
        }
    }

    // Baja lógica: el usuario no se borra, solo se marca como inactivo.
    @Override
    public void bajaLogica(int id) throws SQLException {
        cambiarActivo(id, false);
    }

    // Deshace la baja lógica (lo usaría un administrador).
    public void reactivar(int id) throws SQLException {
        cambiarActivo(id, true);
    }

    private void cambiarActivo(int id, boolean activo) throws SQLException {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // Convierte una fila de la base en un objeto: Artist o Administrator según el rol.
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

        // Se pisan los valores que puso el constructor con los reales de la base
        user.setActivo(rs.getBoolean("activo"));
        user.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        return user;
    }
}
