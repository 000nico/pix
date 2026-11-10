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
import java.util.Set;
import java.util.UUID;

// dao de publicaciones: guarda la obra (tabla obras), su publicacion (tabla publicaciones) y sus tags (tabla tags_publicaciones). la imagen se guarda como archivo .pix y en la base queda solo la ruta
public class PublicacionDAO implements DAO<Publicacion> {

    private static final String CARPETA_IMAGENES = "imagenes";

    // guarda obra + publicacion + tags juntos. si algo falla no se guarda nada (transaccion)
    @Override
    public void insertar(Publicacion pub) throws SQLException {
        Obra obra = pub.getObra();
        if (obra.getFechaPublicacion() == null) {
            obra.setFechaPublicacion(LocalDateTime.now());
        }

        Connection con = ConexionDB.getConexion();
        try {
            con.setAutoCommit(false);
            try {
                String ruta = guardarImagen(obra.getImagen());

                String sqlObra = "INSERT INTO obras (id_artista, titulo, descripcion, ruta_archivo, fecha_publicacion) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psObra = con.prepareStatement(sqlObra, Statement.RETURN_GENERATED_KEYS);
                psObra.setInt(1, obra.getIdArtista());
                psObra.setString(2, obra.getTitulo());
                psObra.setString(3, obra.getDescripcion());
                psObra.setString(4, ruta);
                psObra.setTimestamp(5, Timestamp.valueOf(obra.getFechaPublicacion()));
                psObra.executeUpdate();
                ResultSet keysObra = psObra.getGeneratedKeys();
                if (keysObra.next()) {
                    obra.setId(keysObra.getInt(1));
                }
                keysObra.close();
                psObra.close();

                // una publicacion nueva siempre arranca activa
                String sqlPub = "INSERT INTO publicaciones (id_obra, activo) VALUES (?, TRUE)";
                PreparedStatement psPub = con.prepareStatement(sqlPub, Statement.RETURN_GENERATED_KEYS);
                psPub.setInt(1, obra.getId());
                psPub.executeUpdate();
                ResultSet keysPub = psPub.getGeneratedKeys();
                if (keysPub.next()) {
                    pub.setId(keysPub.getInt(1));
                }
                keysPub.close();
                psPub.close();
                pub.setActivo(true);

                insertarTags(con, pub.getId(), pub.getTags());
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } catch (IOException e) {
                con.rollback();
                throw new SQLException("no se pudo guardar la imagen de la obra", e);
            }
        } finally {
            con.close();
        }
    }

    // busca una publicacion activa por id. si no existe devuelve null
    @Override
    public Publicacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, o.descripcion, o.ruta_archivo, o.fecha_publicacion FROM publicaciones p JOIN obras o ON o.id = p.id_obra WHERE p.id = ? AND p.activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Publicacion pub = null;
            if (rs.next()) {
                pub = mapear(con, rs);
            }
            rs.close();
            ps.close();
            return pub;
        } finally {
            con.close();
        }
    }

    // feed del catalogo: todas las publicaciones activas, de la mas nueva a la mas vieja
    @Override
    public List<Publicacion> listar() throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, o.descripcion, o.ruta_archivo, o.fecha_publicacion FROM publicaciones p JOIN obras o ON o.id = p.id_obra WHERE p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<Publicacion>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                publicaciones.add(mapear(con, rs));
            }
            rs.close();
            ps.close();
            return publicaciones;
        } finally {
            con.close();
        }
    }

    // obras de un artista puntual
    public List<Publicacion> listarPorArtista(int idArtista) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, o.descripcion, o.ruta_archivo, o.fecha_publicacion FROM publicaciones p JOIN obras o ON o.id = p.id_obra WHERE o.id_artista = ? AND p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<Publicacion>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idArtista);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                publicaciones.add(mapear(con, rs));
            }
            rs.close();
            ps.close();
            return publicaciones;
        } finally {
            con.close();
        }
    }

    // busqueda por hashtag
    public List<Publicacion> listarPorTag(String tag) throws SQLException {
        String sql = "SELECT p.id AS id_publicacion, p.activo, o.id AS id_obra, o.id_artista, o.titulo, o.descripcion, o.ruta_archivo, o.fecha_publicacion FROM publicaciones p JOIN obras o ON o.id = p.id_obra JOIN tags_publicaciones t ON t.id_publicacion = p.id WHERE t.tag = ? AND p.activo = TRUE ORDER BY o.fecha_publicacion DESC";
        List<Publicacion> publicaciones = new ArrayList<Publicacion>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tag);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                publicaciones.add(mapear(con, rs));
            }
            rs.close();
            ps.close();
            return publicaciones;
        } finally {
            con.close();
        }
    }

    // cuenta las obras activas de un artista, sirve para las estadisticas del administrador
    public int contarPorArtista(int idArtista) throws SQLException {
        String sql = "SELECT COUNT(*) FROM publicaciones p JOIN obras o ON o.id = p.id_obra WHERE o.id_artista = ? AND p.activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idArtista);
            ResultSet rs = ps.executeQuery();
            int cantidad = 0;
            if (rs.next()) {
                cantidad = rs.getInt(1);
            }
            rs.close();
            ps.close();
            return cantidad;
        } finally {
            con.close();
        }
    }

    // modifica titulo, descripcion y tags. la imagen no se cambia
    @Override
    public void actualizar(Publicacion pub) throws SQLException {
        Connection con = ConexionDB.getConexion();
        try {
            con.setAutoCommit(false);
            try {
                String sqlObra = "UPDATE obras SET titulo = ?, descripcion = ? WHERE id = ?";
                PreparedStatement psObra = con.prepareStatement(sqlObra);
                psObra.setString(1, pub.getObra().getTitulo());
                psObra.setString(2, pub.getObra().getDescripcion());
                psObra.setInt(3, pub.getObra().getId());
                psObra.executeUpdate();
                psObra.close();

                // los tags se borran y se vuelven a cargar
                String sqlBorrarTags = "DELETE FROM tags_publicaciones WHERE id_publicacion = ?";
                PreparedStatement psBorrar = con.prepareStatement(sqlBorrarTags);
                psBorrar.setInt(1, pub.getId());
                psBorrar.executeUpdate();
                psBorrar.close();

                insertarTags(con, pub.getId(), pub.getTags());
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.close();
        }
    }

    // baja logica: la publicacion se oculta pero no se borra
    @Override
    public void bajaLogica(int id) throws SQLException {
        String sql = "UPDATE publicaciones SET activo = FALSE WHERE id = ?";
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

    // deshace la baja logica, lo usa un administrador
    public void reactivar(int id) throws SQLException {
        String sql = "UPDATE publicaciones SET activo = TRUE WHERE id = ?";
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

    // convierte la fila actual en una publicacion con su obra, su imagen y sus tags
    private Publicacion mapear(Connection con, ResultSet rs) throws SQLException {
        int idPublicacion = rs.getInt("id_publicacion");

        Image imagen;
        try {
            imagen = ImageHandler.read(rs.getString("ruta_archivo"));
        } catch (IOException e) {
            throw new SQLException("no se pudo leer la imagen de la obra " + rs.getInt("id_obra"), e);
        }

        Obra obra = new Obra(rs.getInt("id_obra"), rs.getInt("id_artista"), rs.getString("titulo"), rs.getString("descripcion"), imagen, null, rs.getTimestamp("fecha_publicacion").toLocalDateTime(), true);
        return new Publicacion(idPublicacion, cargarTags(con, idPublicacion), rs.getBoolean("activo"), obra);
    }

    // trae los tags de una publicacion
    private Set<String> cargarTags(Connection con, int idPublicacion) throws SQLException {
        Set<String> tags = new HashSet<String>();
        String sql = "SELECT tag FROM tags_publicaciones WHERE id_publicacion = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idPublicacion);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            tags.add(rs.getString("tag"));
        }
        rs.close();
        ps.close();
        return tags;
    }

    // guarda los tags de una publicacion
    private void insertarTags(Connection con, int idPublicacion, Set<String> tags) throws SQLException {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO tags_publicaciones (id_publicacion, tag) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        for (String tag : tags) {
            ps.setInt(1, idPublicacion);
            ps.setString(2, tag);
            ps.executeUpdate();
        }
        ps.close();
    }

    // guarda la imagen como archivo .pix y devuelve la ruta donde quedo
    private String guardarImagen(Image imagen) throws IOException {
        new File(CARPETA_IMAGENES).mkdirs();
        String ruta = CARPETA_IMAGENES + "/" + UUID.randomUUID() + ".pix";
        ImageHandler.write(imagen, ruta);
        return ruta;
    }
}
