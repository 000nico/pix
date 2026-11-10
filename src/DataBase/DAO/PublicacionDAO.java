package DataBase.DAO;

import Classes.Image.Image;
import Classes.Image.ImageHandler;
import Classes.Publicacion.Obra;
import Classes.Publicacion.Publicacion;
import DataBase.ConexionDB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * DAO de publicaciones. Maneja el ABMCL de las obras: guarda la obra (tabla obras),
 * su publicación (tabla publicaciones) y sus tags (tabla tags_publicaciones).
 * La imagen se guarda como archivo .pix y en la base queda solo la ruta.
 */
public class PublicacionDAO implements DAO<Publicacion> {

    private static final String CARPETA_IMAGENES = "imagenes";

    // Guarda obra + publicación + tags juntos. Si algo falla, no se guarda nada (transacción).
    @Override
    public void insertar(Publicacion pub) throws SQLException {
        Obra obra = pub.getObra();
        if (obra.getFechaPublicacion() == null) {
            obra.setFechaPublicacion(LocalDateTime.now());
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);
            try {
                String ruta = guardarImagen(obra.getImagen());

                String sqlObra = "INSERT INTO obras (id_artista, titulo, descripcion, ruta_archivo, fecha_publicacion) "
                        + "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlObra, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, obra.getIdArtista());
                    ps.setString(2, obra.getTitulo());
                    ps.setString(3, obra.getDescripcion());
                    ps.setString(4, ruta);
                    ps.setTimestamp(5, Timestamp.valueOf(obra.getFechaPublicacion()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            obra.setId(keys.getInt(1));
                        }
                    }
                }

                // Una publicación nueva siempre arranca activa
                String sqlPub = "INSERT INTO publicaciones (id_obra, activo) VALUES (?, TRUE)";
                try (PreparedStatement ps = con.prepareStatement(sqlPub, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, obra.getId());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            pub.setId(keys.getInt(1));
                        }
                    }
                }
                pub.setActivo(true);

                insertarTags(con, pub.getId(), pub.getTags());
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } catch (IOException e) {
                con.rollback();
                throw new SQLException("No se pudo guardar la imagen de la obra", e);
            }
        }
    }

    // Busca una publicación activa por id.
    @Override
    public Optional<Publicacion> buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, "
                + "o.descripcion, o.ruta_archivo, o.fecha_publicacion "
                + "FROM publicaciones p JOIN obras o ON o.id = p.id_obra "
                + "WHERE p.id = ? AND p.activo = TRUE";

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

    // Feed del catálogo: todas las publicaciones activas, de la más nueva a la más vieja.
    @Override
    public List<Publicacion> listar() throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, "
                + "o.descripcion, o.ruta_archivo, o.fecha_publicacion "
                + "FROM publicaciones p JOIN obras o ON o.id = p.id_obra "
                + "WHERE p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                publicaciones.add(mapear(con, rs));
            }
        }
        return publicaciones;
    }

    // Obras de un artista puntual.
    public List<Publicacion> listarPorArtista(int idArtista) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, "
                + "o.descripcion, o.ruta_archivo, o.fecha_publicacion "
                + "FROM publicaciones p JOIN obras o ON o.id = p.id_obra "
                + "WHERE o.id_artista = ? AND p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    publicaciones.add(mapear(con, rs));
                }
            }
        }
        return publicaciones;
    }

    // Búsqueda por hashtag.
    public List<Publicacion> listarPorTag(String tag) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, "
                + "o.descripcion, o.ruta_archivo, o.fecha_publicacion "
                + "FROM publicaciones p JOIN obras o ON o.id = p.id_obra "
                + "JOIN tags_publicaciones t ON t.id_publicacion = p.id "
                + "WHERE t.tag = ? AND p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tag);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    publicaciones.add(mapear(con, rs));
                }
            }
        }
        return publicaciones;
    }

    // Cuenta las obras activas de un artista (para las estadísticas del administrador).
    public int contarPorArtista(int idArtista) throws SQLException {
        String sql = "SELECT COUNT(*) FROM publicaciones p JOIN obras o ON o.id = p.id_obra "
                + "WHERE o.id_artista = ? AND p.activo = TRUE";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Modifica título, descripción y tags (la imagen no se cambia).
    @Override
    public void actualizar(Publicacion pub) throws SQLException {
        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);
            try {
                String sqlObra = "UPDATE obras SET titulo = ?, descripcion = ? WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlObra)) {
                    ps.setString(1, pub.getObra().getTitulo());
                    ps.setString(2, pub.getObra().getDescripcion());
                    ps.setInt(3, pub.getObra().getId());
                    ps.executeUpdate();
                }

                // Los tags se borran y se vuelven a cargar
                String sqlBorrarTags = "DELETE FROM tags_publicaciones WHERE id_publicacion = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlBorrarTags)) {
                    ps.setInt(1, pub.getId());
                    ps.executeUpdate();
                }
                insertarTags(con, pub.getId(), pub.getTags());

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Baja lógica: la publicación se oculta pero no se borra.
    @Override
    public void bajaLogica(int id) throws SQLException {
        String sql = "UPDATE publicaciones SET activo = FALSE WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Deshace la baja lógica (lo usaría un administrador).
    public void reactivar(int id) throws SQLException {
        String sql = "UPDATE publicaciones SET activo = TRUE WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Convierte la fila actual en Publicacion (con su Obra, su imagen y sus tags).
    private Publicacion mapear(Connection con, ResultSet rs) throws SQLException {
        int idPublicacion = rs.getInt("id_publicacion");

        Image imagen;
        try {
            imagen = ImageHandler.read(rs.getString("ruta_archivo"));
        } catch (IOException e) {
            throw new SQLException("No se pudo leer la imagen de la obra " + rs.getInt("id_obra"), e);
        }

        Obra obra = new Obra(rs.getInt("id_obra"), rs.getInt("id_artista"),
                rs.getString("titulo"), rs.getString("descripcion"), imagen, null,
                rs.getTimestamp("fecha_publicacion").toLocalDateTime(), true);

        return new Publicacion(idPublicacion, cargarTags(con, idPublicacion),
                rs.getBoolean("activo"), obra);
    }

    // Trae los tags de una publicación.
    private Set<String> cargarTags(Connection con, int idPublicacion) throws SQLException {
        Set<String> tags = new HashSet<>();
        String sql = "SELECT tag FROM tags_publicaciones WHERE id_publicacion = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPublicacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag"));
                }
            }
        }
        return tags;
    }

    private void insertarTags(Connection con, int idPublicacion, Set<String> tags) throws SQLException {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO tags_publicaciones (id_publicacion, tag) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String tag : tags) {
                ps.setInt(1, idPublicacion);
                ps.setString(2, tag);
                ps.executeUpdate();
            }
        }
    }

    // Guarda la imagen como archivo .pix y devuelve la ruta donde quedó.
    private String guardarImagen(Image imagen) throws IOException {
        new File(CARPETA_IMAGENES).mkdirs();
        String ruta = CARPETA_IMAGENES + "/" + UUID.randomUUID() + ".pix";
        ImageHandler.write(imagen, ruta);
        return ruta;
    }
}
