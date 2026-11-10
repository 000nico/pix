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


//Servicio de colecciones (álbumes). Cada usuario maneja las suyas;
 // un administrador también puede darlas de baja.

public class ColeccionService {

    private final ColeccionDAO coleccionDAO = new ColeccionDAO();
    private final PublicacionDAO publicacionDAO = new PublicacionDAO();

    // Crea una colección vacía para el usuario.
    public Coleccion crear(User duenio, String nombre, String descripcion) throws SQLException {
        validarNombre(nombre);

        Coleccion coleccion = new Coleccion(duenio.getId(), nombre.trim(), descripcion);
        coleccionDAO.insertar(coleccion);
        return coleccion;
    }

    public Coleccion buscarPorId(int idColeccion) throws SQLException {
        return coleccionDAO.buscarPorId(idColeccion)
                // orElseThrow es un metodo del optional
                .orElseThrow(() -> new IllegalArgumentException("No existe la colección " + idColeccion));
    }

    // Las colecciones del usuario.
    public List<Coleccion> listarPorUsuario(int idUsuario) throws SQLException {
        return coleccionDAO.listarPorUsuario(idUsuario);
    }

    // Cambia nombre y descripción. Solo el dueño.
    public void editar(User quienPide, int idColeccion, String nombre, String descripcion)
            throws UsuarioSinPermisosException, SQLException {
        validarNombre(nombre);
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);

        coleccion.setNombre(nombre.trim());
        coleccion.setDescripcion(descripcion);
        coleccionDAO.actualizar(coleccion);
    }

    // Baja lógica: el dueño o un administrador.
    public void darDeBaja(User quienPide, int idColeccion) throws UsuarioSinPermisosException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);

        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;
        if (coleccion.getIdUsuario() != quienPide.getId() && !esAdmin) {
            throw new UsuarioSinPermisosException("Solo el dueño o un administrador pueden borrar la colección");
        }
        coleccionDAO.bajaLogica(idColeccion);
    }

    // Suma una obra a la colección. Solo el dueño.
    public void agregarObra(User quienPide, int idColeccion, int idPublicacion)
            throws UsuarioSinPermisosException, ObraNoEncontradaException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);
        int idObra = obtenerPublicacion(idPublicacion).getObra().getId();

        if (coleccion.getIdsObras().contains(idObra)) {
            throw new IllegalArgumentException("La obra ya está en la colección");
        }
        coleccionDAO.agregarObra(idColeccion, idObra);
    }

    // Saca una obra de la colección (la obra no se borra). Solo el dueño.
    public void quitarObra(User quienPide, int idColeccion, int idPublicacion)
            throws UsuarioSinPermisosException, ObraNoEncontradaException, SQLException {
        Coleccion coleccion = buscarPorId(idColeccion);
        exigirDuenio(quienPide, coleccion);
        int idObra = obtenerPublicacion(idPublicacion).getObra().getId();

        if (!coleccion.getIdsObras().contains(idObra)) {
            throw new IllegalArgumentException("La obra no está en la colección");
        }
        coleccionDAO.quitarObra(idColeccion, idObra);
    }

    //  auxiliares

    private Publicacion obtenerPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        return publicacionDAO.buscarPorId(idPublicacion)
                .orElseThrow(() -> new ObraNoEncontradaException("No existe la obra " + idPublicacion));
    }

    private void exigirDuenio(User quienPide, Coleccion coleccion) throws UsuarioSinPermisosException {
        if (coleccion.getIdUsuario() != quienPide.getId()) {
            throw new UsuarioSinPermisosException("Solo el dueño de la colección puede hacer esto");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la colección es obligatorio");
        }
    }
}
