package DataBase.DAO;

import Classes.Publicacion.Comentario;
import DataBase.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// dao de comentarios: el unico que habla con la tabla "comentarios"
public class ComentarioDAO implements DAO<Comentario> {

    // guarda un comentario nuevo y le asigna el id que genero la base
    @Override
    public void insertar(Comentario comentario) throws SQLException {
        if (comentario.getFecha() == null) {
            comentario.setFecha(LocalDateTime.now());
        }
        String sql = "INSERT INTO comentarios (id_obra, id_usuario, contenido, fecha, activo) VALUES (?, ?, ?, ?, TRUE)";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, comentario.getIdObra());
            ps.setInt(2, comentario.getId_usuario());
            ps.setString(3, comentario.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(comentario.getFecha()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                comentario.setId(keys.getInt(1));
            }
            keys.close();
            ps.close();
            comentario.setActivo(true);
        } finally {
            con.close();
        }
    }

    // busca un comentario activo por id. si no existe devuelve null
    @Override
    public Comentario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM comentarios WHERE id = ? AND activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Comentario comentario = null;
            if (rs.next()) {
                comentario = mapear(rs);
            }
            rs.close();
            ps.close();
            return comentario;
        } finally {
            con.close();
        }
    }

    // todos los comentarios activos, sirve para que el administrador los modere
    @Override
    public List<Comentario> listar() throws SQLException {
        String sql = "SELECT * FROM comentarios WHERE activo = TRUE ORDER BY fecha";
        List<Comentario> comentarios = new ArrayList<Comentario>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comentarios.add(mapear(rs));
            }
            rs.close();
            ps.close();
            return comentarios;
        } finally {
            con.close();
        }
    }

    // comentarios de una obra, del mas viejo al mas nuevo
    public List<Comentario> listarPorObra(int idObra) throws SQLException {
        String sql = "SELECT * FROM comentarios WHERE id_obra = ? AND activo = TRUE ORDER BY fecha";
        List<Comentario> comentarios = new ArrayList<Comentario>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idObra);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comentarios.add(mapear(rs));
            }
            rs.close();
            ps.close();
            return comentarios;
        } finally {
            con.close();
        }
    }

    // solo se puede cambiar el texto del comentario
    @Override
    public void actualizar(Comentario comentario) throws SQLException {
        String sql = "UPDATE comentarios SET contenido = ? WHERE id = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, comentario.getContent());
            ps.setInt(2, comentario.getId());
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // baja logica: el comentario se oculta pero no se borra
    @Override
    public void bajaLogica(int id) throws SQLException {
        String sql = "UPDATE comentarios SET activo = FALSE WHERE id = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // convierte la fila actual en un objeto comentario
    private Comentario mapear(ResultSet rs) throws SQLException {
        Comentario c = new Comentario(rs.getInt("id_obra"), rs.getInt("id_usuario"), rs.getString("contenido"));
        c.setId(rs.getInt("id"));
        c.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        c.setActivo(rs.getBoolean("activo"));
        return c;
    }
}
