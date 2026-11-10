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


 //Servicio de publicaciones (las obras del catálogo).
 //Publica solo un artista; edita o da de baja el dueño de la obra o un administrador.

public class PublicacionService {

    private final PublicacionDAO publicacionDAO = new PublicacionDAO();
    private final ComentarioDAO comentarioDAO = new ComentarioDAO();

    // Publica una obra nueva. Solo pueden los artistas.
    public Publicacion publicar(User autor, String titulo, String descripcion, Image imagen, Set<String> tags)
            throws UsuarioSinPermisosException, SQLException {
        if (autor.getRol() != RolUsuario.ARTISTA) {
            throw new UsuarioSinPermisosException("Solo los artistas pueden publicar obras");
        }
        validarTitulo(titulo);
        if (imagen == null || !imagen.isComplete()) {
            throw new IllegalArgumentException("La imagen está incompleta");
        }

        Set<String> tagsLimpios = limpiarTags(tags);
        Obra obra = new Obra(0, autor.getId(), titulo.trim(), descripcion, imagen, tagsLimpios,
                LocalDateTime.now(), true);
        Publicacion publicacion = new Publicacion(0, tagsLimpios, true, obra);

        publicacionDAO.insertar(publicacion);
        return publicacion;
    }

    // Feed del catálogo: lista de publicaciones activas, de la más nueva a la más vieja.
    public List<Publicacion> verFeed() throws SQLException {
        return publicacionDAO.listar();
    }

    // Busca una publicación y le carga sus comentarios.
    public Publicacion buscarPorId(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);

        for (Comentario c : comentarioDAO.listarPorObra(publicacion.getObra().getId())) {
            publicacion.getObra().addComentario(c);
        }
        return publicacion;
    }

    public List<Publicacion> listarPorArtista(int idArtista) throws SQLException {
        return publicacionDAO.listarPorArtista(idArtista);
    }

    // Búsqueda por hashtag (no importa si se escribe con # o en mayúsculas).
    public List<Publicacion> buscarPorTag(String tag) throws SQLException {
        Set<String> limpio = limpiarTags(java.util.Collections.singleton(tag));
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El tag está vacío");
        }
        return publicacionDAO.listarPorTag(limpio.iterator().next());
    }

    // Edita título, descripción y tags. Solo el dueño o un administrador.
    public void editar(User quienPide, int idPublicacion, String titulo, String descripcion, Set<String> tags)
            throws ObraNoEncontradaException, UsuarioSinPermisosException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);
        exigirDuenioOAdmin(quienPide, publicacion);
        validarTitulo(titulo);

        publicacion.getObra().setTitulo(titulo.trim());
        publicacion.getObra().setDescripcion(descripcion);
        publicacion.setTags(limpiarTags(tags));
        publicacionDAO.actualizar(publicacion);
    }

    // Baja lógica de una obra. Solo el dueño o un administrador.
    public void darDeBaja(User quienPide, int idPublicacion)
            throws ObraNoEncontradaException, UsuarioSinPermisosException, SQLException {
        Publicacion publicacion = obtener(idPublicacion);
        exigirDuenioOAdmin(quienPide, publicacion);
        publicacionDAO.bajaLogica(idPublicacion);
    }

    // Deshace la baja lógica. Solo administradores.
    public void reactivar(User quienPide, int idPublicacion) throws UsuarioSinPermisosException, SQLException {
        if (quienPide.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new UsuarioSinPermisosException("Solo un administrador puede reactivar obras");
        }
        publicacionDAO.reactivar(idPublicacion);
    }

    // ---------- auxiliares ----------

    private Publicacion obtener(int idPublicacion) throws ObraNoEncontradaException, SQLException {
        return publicacionDAO.buscarPorId(idPublicacion)
                // orElseThrow es un metodo del optional
                .orElseThrow(() -> new ObraNoEncontradaException("No existe la obra " + idPublicacion));
    }

    private void exigirDuenioOAdmin(User quienPide, Publicacion publicacion) throws UsuarioSinPermisosException {
        boolean esDuenio = publicacion.getObra().getIdArtista() == quienPide.getId();
        boolean esAdmin = quienPide.getRol() == RolUsuario.ADMINISTRADOR;

        if (!esDuenio && !esAdmin) {
            throw new UsuarioSinPermisosException("Solo el dueño de la obra o un administrador pueden hacer esto");
        }
    }

    private void validarTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
    }

    // Deja los tags en minúscula y sin "#". Al ser un Set, los repetidos desaparecen solos.
    private Set<String> limpiarTags(Set<String> tags) {
        Set<String> limpios = new HashSet<>();
        if (tags == null) {
            return limpios;
        }
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            String t = tag.trim().toLowerCase();
            if (t.startsWith("#")) {
                t = t.substring(1);
            }
            if (!t.isEmpty()) {
                limpios.add(t);
            }
        }
        return limpios;
    }
}
