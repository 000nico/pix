package DataBase.Service;

import Classes.Publicacion.Coleccion;
import Classes.Publicacion.Publicacion;
import Classes.Users.User;
import DataBase.DAO.ColeccionDAO;
import DataBase.DAO.PublicacionDAO;
import Enum.RolUsuario;
import Exceptions.ObraNoEncontradaException;
import Exceptions.UsuarioSinPermisosException;

import java.sql.SQLException;
import java.util.List;

// servicio de colecciones (albumes). cada usuario maneja las suyas, un administrador tambien puede darlas de baja
public class ColeccionService {

    private ColeccionDAO coleccionDAO = new ColeccionDAO();
    private PublicacionDAO publicacionDAO = new PublicacionDAO();

    // crea una coleccion vacia para el usuario
    public Coleccion crear(User duenio, String nombre, String descripcion) throws SQLException {
        validarNombre(nombre);

        Coleccion coleccion = new Coleccion(duenio.getId(), nombre.trim(), descripcion);
        coleccionDAO.insertar(coleccion);
        return coleccion;
    }

    // busca una coleccion por id
    public Coleccion buscarPorId(int idColeccion) throws SQLException {
        Coleccion coleccion = coleccionDAO.buscarPorId(idColeccion);
        if (coleccion == null) {
            throw new IllegalArgumentException("no existe la coleccion " + idColeccion);
        }
        return coleccion;
    }

    // las colecciones del usuario
    public List<Coleccion> listarPorUsuario(int idUsuario) throws SQLException {
        return coleccionDAO.listarPorUsuario(idUsuario);
    }

    // todas las colecciones activas, las ve un administrador
    public List<Coleccion> listarTodas(User quienPide) throws UsuarioSinPermisosException, SQLException {
        if (quienPide.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new UsuarioSinPermisosException("esta accion es solo para administradores");
        }
        return coleccionDAO.listar();
    }

    // cambia nombre y descripcion. solo el duenio
    public void editar(User quienPide, int idColeccion, String nombre, String descripcion) throws UsuarioSinPermisosException, SQLException {
        validarNombre(nombre);
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);

        coleccion.setNombre(nombre.trim());
        coleccion.setDescripcion(descripcion);
        coleccionDAO.actualizar(coleccion);
    }

    // baja logica: el duenio o un administrador
    public void darDeBaja(User quienPide, int idColeccion) throws UsuarioSinPermisosException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);

        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;
        if (coleccion.getIdUsuario() != quienPide.getId() && !esAdmin) {
            throw new UsuarioSinPermisosException("solo el duenio o un administrador pueden borrar la coleccion");
        }
        coleccionDAO.bajaLogica(idColeccion);
    }

    // suma una obra a la coleccion. solo el duenio
    public void agregarObra(User quienPide, int idColeccion, int idPublicacion) throws UsuarioSinPermisosException, ObraNoEncontradaException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);
        int idObra = obtenerPublicacion(idPublicacion).getObra().getId();

        if (coleccion.getIdsObras().contains(idObra)) {
            throw new IllegalArgumentException("la obra ya esta en la coleccion");
        }
        coleccionDAO.agregarObra(idColeccion, idObra);
    }

    // saca una obra de la coleccion, la obra no se borra. solo el duenio
    public void quitarObra(User quienPide, int idColeccion, int idPublicacion) throws UsuarioSinPermisosException, ObraNoEncontradaException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);
        int idObra = obtenerPublicacion(idPublicacion).getObra().getId();

        if (!coleccion.getIdsObras().contains(idObra)) {
            throw new IllegalArgumentException("la obra no esta en la coleccion");
        }
        coleccionDAO.quitarObra(idColeccion, idObra);
    }

    // auxiliares

    private Publicacion obtenerPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = publicacionDAO.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new ObraNoEncontradaException("no existe la obra " + idPublicacion);
        }
        return publicacion;
    }

    // revisa que quien pide sea el duenio de la coleccion
    private void exigirDuenio(User quienPide, Coleccion coleccion) throws UsuarioSinPermisosException {
        if (coleccion.getIdUsuario() != quienPide.getId()) {
            throw new UsuarioSinPermisosException("solo el duenio de la coleccion puede hacer esto");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("el nombre de la coleccion es obligatorio");
        }
    }
}
