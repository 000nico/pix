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

// dao de colecciones: maneja las tablas "colecciones" y "colecciones_obras"
public class ColeccionDAO implements DAO<Coleccion> {

    // guarda la coleccion y las obras que ya tenga, todo junto (transaccion)
    @Override
    public void insertar(Coleccion coleccion) throws SQLException {
        String sql = "INSERT INTO colecciones (id_usuario, nombre, descripcion, activo) VALUES (?, ?, ?, TRUE)";
        Connection con = ConexionDB.getConexion();
        try {
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, coleccion.getIdUsuario());
                ps.setString(2, coleccion.getNombre());
                ps.setString(3, coleccion.getDescripcion());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    coleccion.setId(keys.getInt(1));
                }
                keys.close();
                ps.close();
                coleccion.setActivo(true);

                for (int i = 0; i < coleccion.getIdsObras().size(); i++) {
                    vincular(con, coleccion.getId(), coleccion.getIdsObras().get(i));
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.close();
        }
    }

    // busca una coleccion activa por id. si no existe devuelve null
    @Override
    public Coleccion buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE id = ? AND activo = TRUE";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Coleccion coleccion = null;
            if (rs.next()) {
                coleccion = mapear(con, rs);
            }
            rs.close();
            ps.close();
            return coleccion;
        } finally {
            con.close();
        }
    }

    // todas las colecciones activas, las ve el administrador
    @Override
    public List<Coleccion> listar() throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE activo = TRUE";
        List<Coleccion> colecciones = new ArrayList<Coleccion>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                colecciones.add(mapear(con, rs));
            }
            rs.close();
            ps.close();
            return colecciones;
        } finally {
            con.close();
        }
    }

    // colecciones de un usuario
    public List<Coleccion> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM colecciones WHERE id_usuario = ? AND activo = TRUE";
        List<Coleccion> colecciones = new ArrayList<Coleccion>();
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                colecciones.add(mapear(con, rs));
            }
            rs.close();
            ps.close();
            return colecciones;
        } finally {
            con.close();
        }
    }

    // cambia nombre y descripcion. las obras se manejan con agregarobra y quitarobra
    @Override
    public void actualizar(Coleccion coleccion) throws SQLException {
        String sql = "UPDATE colecciones SET nombre = ?, descripcion = ? WHERE id = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, coleccion.getNombre());
            ps.setString(2, coleccion.getDescripcion());
            ps.setInt(3, coleccion.getId());
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // baja logica: la coleccion se oculta pero no se borra
    @Override
    public void bajaLogica(int id) throws SQLException {
        String sql = "UPDATE colecciones SET activo = FALSE WHERE id = ?";
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

    // suma una obra a la coleccion
    public void agregarObra(int idColeccion, int idObra) throws SQLException {
        Connection con = ConexionDB.getConexion();
        try {
            vincular(con, idColeccion, idObra);
        } finally {
            con.close();
        }
    }

    // saca una obra de la coleccion, la obra en si no se toca
    public void quitarObra(int idColeccion, int idObra) throws SQLException {
        String sql = "DELETE FROM colecciones_obras WHERE id_coleccion = ? AND id_obra = ?";
        Connection con = ConexionDB.getConexion();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idColeccion);
            ps.setInt(2, idObra);
            ps.executeUpdate();
            ps.close();
        } finally {
            con.close();
        }
    }

    // agrega la fila que une una coleccion con una obra
    private void vincular(Connection con, int idColeccion, int idObra) throws SQLException {
        String sql = "INSERT INTO colecciones_obras (id_coleccion, id_obra) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idColeccion);
        ps.setInt(2, idObra);
        ps.executeUpdate();
        ps.close();
    }

    // convierte la fila actual en una coleccion y le carga los ids de sus obras
    private Coleccion mapear(Connection con, ResultSet rs) throws SQLException {
        Coleccion c = new Coleccion(rs.getInt("id"), rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("descripcion"), rs.getBoolean("activo"));

        String sql = "SELECT id_obra FROM colecciones_obras WHERE id_coleccion = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, c.getId());
        ResultSet obras = ps.executeQuery();
        while (obras.next()) {
            c.getIdsObras().add(obras.getInt("id_obra"));
        }
        obras.close();
        ps.close();
        return c;
    }
}
