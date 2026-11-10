package DataBase.DAO;

import Classes.Publicacion.Coleccion;
import DataBase.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de colecciones: maneja las tablas "colecciones" y "colecciones_obras".
 */
public class ColeccionDAO implements DAO<Coleccion> {

    // Guarda la colección y las obras que ya tenga, todo junto (transacción).
    @Override
    public void insertar(Coleccion coleccion) throws SQLException {
        String sql = "INSERT INTO colecciones (id_usuario, nombre, descripcion, activo) VALUES (?, ?, ?, TRUE)";

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, coleccion.getIdUsuario());
                    ps.setString(2, coleccion.getNombre());
                    ps.setString(3, coleccion.getDescripcion());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            coleccion.setId(keys.getInt(1));
                        }
                    }
                }
                coleccion.setActivo(true);

                for (int idObra : coleccion.getIdsObras()) {
                    vincular(con, coleccion.getId(), idObra);
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Busca una colección activa por id.
    @Override
    public Optional<Coleccion> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE id = ? AND activo = TRUE";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(con, rs));
                }
                return Optional.empty();
            }
        }
    }

    // Todas las colecciones activas.
    @Override
    public List<Coleccion> listar() throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE activo = TRUE";
        List<Coleccion> colecciones = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                colecciones.add(mapear(con, rs));
            }
        }
        return colecciones;
    }

    // Colecciones de un usuario.
    public List<Coleccion> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE id_usuario = ? AND activo = TRUE";
        List<Coleccion> colecciones = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colecciones.add(mapear(con, rs));
                }
            }
        }
        return colecciones;
    }

    // Cambia nombre y descripción (las obras se manejan con agregarObra / quitarObra).
    @Override
    public void actualizar(Coleccion coleccion) throws SQLException {
        String sql = "UPDATE colecciones SET nombre = ?, descripcion = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, coleccion.getNombre());
            ps.setString(2, coleccion.getDescripcion());
            ps.setInt(3, coleccion.getId());
            ps.executeUpdate();
        }
    }

    // Baja lógica: la colección se oculta pero no se borra.
    @Override
    public void bajaLogica(int id) throws SQLException {
        String sql = "UPDATE colecciones SET activo = FALSE WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Suma una obra a la colección.
    public void agregarObra(int idColeccion, int idObra) throws SQLException {
        try (Connection con = ConexionDB.getConexion()) {
            vincular(con, idColeccion, idObra);
        }
    }

    // Saca una obra de la colección (la obra en sí no se toca).
    public void quitarObra(int idColeccion, int idObra) throws SQLException {
        String sql = "DELETE FROM colecciones_obras WHERE id_coleccion = ? AND id_obra = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idColeccion);
            ps.setInt(2, idObra);
            ps.executeUpdate();
        }
    }

    private void vincular(Connection con, int idColeccion, int idObra) throws SQLException {
        String sql = "INSERT INTO colecciones_obras (id_coleccion, id_obra) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idColeccion);
            ps.setInt(2, idObra);
            ps.executeUpdate();
        }
    }

    // Convierte la fila actual en Coleccion y le carga los ids de sus obras.
    private Coleccion mapear(Connection con, ResultSet rs) throws SQLException {
        Coleccion c = new Coleccion(rs.getInt("id"), rs.getInt("id_usuario"),
                rs.getString("nombre"), rs.getString("descripcion"), rs.getBoolean("activo"));

        String sql = "SELECT id_obra FROM colecciones_obras WHERE id_coleccion = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            try (ResultSet obras = ps.executeQuery()) {
                while (obras.next()) {
                    c.getIdsObras().add(obras.getInt("id_obra"));
                }
            }
        }
        return c;
    }
}
