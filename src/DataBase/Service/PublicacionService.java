package DataBase.Service;

import Classes.Image.Image;
import Classes.Publicacion.Comentario;
import Classes.Publicacion.Obra;
import Classes.Publicacion.Publicacion;
import Classes.Users.User;
import DataBase.DAO.ComentarioDAO;
import DataBase.DAO.PublicacionDAO;
import Enum.RolUsuario;
import Exceptions.ObraNoEncontradaException;
import Exceptions.UsuarioSinPermisosException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// servicio de publicaciones (las obras del catalogo). publica solo un artista, edita o da de baja el duenio de la obra o un administrador
public class PublicacionService {

    private PublicacionDAO publicacionDAO = new PublicacionDAO();
    private ComentarioDAO comentarioDAO = new ComentarioDAO();

    // publica una obra nueva. solo pueden los artistas
    public Publicacion publicar(User autor, String titulo, String descripcion, Image imagen, Set<String> tags) throws UsuarioSinPermisosException, SQLException {
        if (autor.getRol() != RolUsuario.ARTISTA) {
            throw new UsuarioSinPermisosException("solo los artistas pueden publicar obras");
        }
        validarTitulo(titulo);
        if (imagen == null || !imagen.isComplete()) {
            throw new IllegalArgumentException("la imagen esta incompleta");
        }

        Set<String> tagsLimpios = limpiarTags(tags);
        Obra obra = new Obra(0, autor.getId(), titulo.trim(), descripcion, imagen, tagsLimpios, LocalDateTime.now(), true);
        Publicacion publicacion = new Publicacion(0, tagsLimpios, true, obra);

        publicacionDAO.insertar(publicacion);
        return publicacion;
    }

    // feed del catalogo: lista de publicaciones activas, de la mas nueva a la mas vieja
    public List<Publicacion> verFeed() throws SQLException {
        return publicacionDAO.listar();
    }

    // busca una publicacion y le carga sus comentarios
    public Publicacion buscarPorId(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);

        List<Comentario> comentarios = comentarioDAO.listarPorObra(publicacion.getObra().getId());
        for (int i = 0; i < comentarios.size(); i++) {
            publicacion.getObra().addComentario(comentarios.get(i));
        }
        return publicacion;
    }

    // obras de un artista puntual
    public List<Publicacion> listarPorArtista(int idArtista) throws SQLException {
        return publicacionDAO.listarPorArtista(idArtista);
    }

    // busqueda por hashtag, no importa si se escribe con # o en mayusculas
    public List<Publicacion> buscarPorTag(String tag) throws SQLException {
        String limpio = limpiarTag(tag);
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("el tag esta vacio");
        }
        return publicacionDAO.listarPorTag(limpio);
    }

    // edita titulo, descripcion y tags. solo el duenio o un administrador
    public void editar(User quienPide, int idPublicacion, String titulo, String descripcion, Set<String> tags) throws ObraNoEncontradaException, UsuarioSinPermisosException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);
        exigirDuenioOAdmin(quienPide, publicacion);
        validarTitulo(titulo);

        publicacion.getObra().setTitulo(titulo.trim());
        publicacion.getObra().setDescripcion(descripcion);
        publicacion.setTags(limpiarTags(tags));
        publicacionDAO.actualizar(publicacion);
    }

    // baja logica de una obra. solo el duenio o un administrador
    public void darDeBaja(User quienPide, int idPublicacion) throws ObraNoEncontradaException, UsuarioSinPermisosException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);
        exigirDuenioOAdmin(quienPide, publicacion);
        publicacionDAO.bajaLogica(idPublicacion);
    }

    // deshace la baja logica. solo administradores
    public void reactivar(User quienPide, int idPublicacion) throws UsuarioSinPermisosException, SQLException {
        if (quienPide.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new UsuarioSinPermisosException("solo un administrador puede reactivar obras");
        }
        publicacionDAO.reactivar(idPublicacion);
    }

    // auxiliares

    private Publicacion obtener(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = publicacionDAO.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new ObraNoEncontradaException("no existe la obra " + idPublicacion);
        }
        return publicacion;
    }

    // revisa que quien pide sea el duenio de la obra o un administrador
    private void exigirDuenioOAdmin(User quienPide, Publicacion publicacion) throws UsuarioSinPermisosException {
        boolean esDuenio = publicacion.getObra().getIdArtista() == quienPide.getId();
        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;

        if (!esDuenio && !esAdmin) {
            throw new UsuarioSinPermisosException("solo el duenio de la obra o un administrador pueden hacer esto");
        }
    }

    private void validarTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("el titulo es obligatorio");
        }
    }

    // deja los tags en minuscula y sin #. al ser un set los repetidos desaparecen solos
    private Set<String> limpiarTags(Set<String> tags) {
        Set<String> limpios = new HashSet<String>();
        if (tags == null) {
            return limpios;
        }
        for (String tag : tags) {
            String limpio = limpiarTag(tag);
            if (!limpio.isEmpty()) {
                limpios.add(limpio);
            }
        }
        return limpios;
    }

    // deja un tag en minuscula, sin # y sin espacios de mas
    private String limpiarTag(String tag) {
        if (tag == null) {
            return "";
        }
        String t = tag.trim().toLowerCase();
        if (t.startsWith("#")) {
            t = t.substring(1);
        }
        return t;
    }
}
