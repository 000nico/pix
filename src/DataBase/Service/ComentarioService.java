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


//Servicio de comentarios. Comenta cualquier usuario logueado;
//edita solo el autor; borra el autor o un administrador.

public class ComentarioService {

    private final ComentarioDAO comentarioDAO = new ComentarioDAO();
    private final PublicacionDAO publicacionDAO = new PublicacionDAO();

    // Agrega un comentario a una publicación.
    public Comentario comentar(User autor, int idPublicacion, String texto)
            throws ObraNoEncontradaException, SQLException {
        validarTexto(texto);
        Publicacion publicacion = obtenerPublicacion(idPublicacion);

        Comentario comentario = new Comentario(publicacion.getObra().getId(), autor.getId(), texto.trim());
        comentarioDAO.insertar(comentario);
        return comentario;
    }

    // Comentarios de una publicación, del más viejo al más nuevo.
    public List<Comentario> listarPorPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = obtenerPublicacion(idPublicacion);
        return comentarioDAO.listarPorObra(publicacion.getObra().getId());
    }

    // Solo el autor puede cambiar el texto de su comentario.
    public void editar(User quienPide, int idComentario, String nuevoTexto)
            throws UsuarioSinPermisosException, SQLException {
        validarTexto(nuevoTexto);
        Comentario comentario = obtenerComentario(idComentario);

        if (comentario.getId_usuario() != quienPide.getId()) {
            throw new UsuarioSinPermisosException("Solo el autor puede editar su comentario");
        }
        comentario.setContent(nuevoTexto.trim());
        comentarioDAO.actualizar(comentario);
    }

    // Baja lógica: la puede hacer el autor o un administrador (moderación).
    public void eliminar(User quienPide, int idComentario) throws UsuarioSinPermisosException, SQLException {
        Comentario comentario = obtenerComentario(idComentario);

        boolean esAutor = comentario.getId_usuario() == quienPide.getId();
        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;
        if (!esAutor && !esAdmin) {
            throw new UsuarioSinPermisosException("Solo el autor o un administrador pueden borrar el comentario");
        }
        comentarioDAO.bajaLogica(idComentario);
    }

    // auxiliares

    private Publicacion obtenerPublicacion(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        return publicacionDAO.buscarPorId(idPublicacion)
                .orElseThrow(() -> new ObraNoEncontradaException("No existe la obra " + idPublicacion));
    }

    private Comentario obtenerComentario(int idComentario) throws SQLException {
        return comentarioDAO.buscarPorId(idComentario)
                // orElseThrow es un metodo del optional
                .orElseThrow(() -> new IllegalArgumentException("No existe el comentario " + idComentario));
    }

    private void validarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }
        if (texto.length() > 500) {
            throw new IllegalArgumentException("El comentario puede tener hasta 500 caracteres");
        }
    }
}
