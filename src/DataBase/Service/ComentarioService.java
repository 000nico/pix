package DataBase.Service;

import Classes.Publicacion.Comentario;
import Classes.Publicacion.Publicacion;
import Classes.Users.User;
import DataBase.DAO.ComentarioDAO;
import DataBase.DAO.PublicacionDAO;
import Enum.RolUsuario;
import Exceptions.ObraNoEncontradaException;
import Exceptions.UsuarioSinPermisosException;

import java.sql.SQLException;
import java.util.List;

// servicio de comentarios. comenta cualquier usuario logueado, edita solo el autor, borra el autor o un administrador
public class ComentarioService {

    private ComentarioDAO comentarioDAO = new ComentarioDAO();
    private PublicacionDAO publicacionDAO = new PublicacionDAO();

    // agrega un comentario a una publicacion
    public Comentario comentar(User autor, int idPublicacion, String texto) throws ObraNoEncontradaException, SQLException {
        validarTexto(texto);
        Publicacion publicacion = obtenerPublicacion(idPublicacion);

        Comentario comentario = new Comentario(publicacion.getObra().getId(), autor.getId(), texto.trim());
        comentarioDAO.insertar(comentario);
        return comentario;
    }

    // comentarios de una publicacion, del mas viejo al mas nuevo
    public List<Comentario> listarPorPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = obtenerPublicacion(idPublicacion);
        return comentarioDAO.listarPorObra(publicacion.getObra().getId());
    }

    // todos los comentarios activos, para que los modere un administrador
    public List<Comentario> listarTodos(User quienPide) throws UsuarioSinPermisosException, SQLException {
        if (quienPide.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new UsuarioSinPermisosException("esta accion es solo para administradores");
        }
        return comentarioDAO.listar();
    }

    // solo el autor puede cambiar el texto de su comentario
    public void editar(User quienPide, int idComentario, String nuevoTexto) throws UsuarioSinPermisosException, SQLException {
        validarTexto(nuevoTexto);
        Comentario comentario = obtenerComentario(idComentario);

        if (comentario.getId_usuario() != quienPide.getId()) {
            throw new UsuarioSinPermisosException("solo el autor puede editar su comentario");
        }
        comentario.setContent(nuevoTexto.trim());
        comentarioDAO.actualizar(comentario);
    }

    // baja logica: la puede hacer el autor o un administrador (moderacion)
    public void eliminar(User quienPide, int idComentario) throws UsuarioSinPermisosException, SQLException {
        Comentario comentario = obtenerComentario(idComentario);

        boolean esAutor = comentario.getId_usuario() == quienPide.getId();
        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;
        if (!esAutor && !esAdmin) {
            throw new UsuarioSinPermisosException("solo el autor o un administrador pueden borrar el comentario");
        }
        comentarioDAO.bajaLogica(idComentario);
    }

    // auxiliares

    private Publicacion obtenerPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = publicacionDAO.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new ObraNoEncontradaException("no existe la obra " + idPublicacion);
        }
        return publicacion;
    }

    private Comentario obtenerComentario(int idComentario) throws SQLException {
        Comentario comentario = comentarioDAO.buscarPorId(idComentario);
        if (comentario == null) {
            throw new IllegalArgumentException("no existe el comentario " + idComentario);
        }
        return comentario;
    }

    // revisa que el comentario tenga texto y no pase los 500 caracteres
    private void validarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("el comentario no puede estar vacio");
        }
        if (texto.length() > 500) {
            throw new IllegalArgumentException("el comentario puede tener hasta 500 caracteres");
        }
    }
}
