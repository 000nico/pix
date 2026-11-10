import Classes.Image.Header;
import Classes.Image.Image;
import Classes.Image.Pixel;
import Classes.Publicacion.Coleccion;
import Classes.Publicacion.Comentario;
import Classes.Publicacion.Publicacion;
import Classes.Users.Administrator;
import Classes.Users.Artist;
import Classes.Users.User;
import DataBase.DAO.UsuarioDAO;
import Enum.RolUsuario;
import Exceptions.ObraNoEncontradaException;
import Exceptions.UsuarioNoEncontradoException;
import Exceptions.UsuarioSinPermisosException;
import DataBase.Service.ColeccionService;
import DataBase.Service.ComentarioService;
import DataBase.Service.PublicacionService;
import DataBase.Service.UsuarioService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Prueba rápida de toda la capa de datos y servicios contra la base MySQL.
 * Cada línea imprime [OK] o [FALLO]. Se puede correr varias veces: los emails llevan un sufijo único.
 */
public class PruebaMain {

    public static void main(String[] args) throws Exception {
        String sufijo = String.valueOf(System.currentTimeMillis());

        UsuarioService usuarios = new UsuarioService();
        PublicacionService publicaciones = new PublicacionService();
        ComentarioService comentarios = new ComentarioService();
        ColeccionService colecciones = new ColeccionService();

        System.out.println("=== USUARIOS ===");
        // El primer administrador se crea directo con el DAO (el servicio exige ser admin para crear admins)
        Administrator admin = new Administrator(0, "admin", "admin" + sufijo + "@pix.com", "1234",
                RolUsuario.ADMINISTRADOR);
        admin.setNivelAcceso(2);
        new UsuarioDAO().insertar(admin);
        resultado("Administrador guardado con id " + admin.getId(), admin.getId() > 0);

        Artist ian = usuarios.registrarArtista("ian", "ian" + sufijo + "@mail.com", "1234", "pixel artist");
        Artist otro = usuarios.registrarArtista("otro", "otro" + sufijo + "@mail.com", "1234", "otra bio");
        resultado("Artistas registrados con id " + ian.getId() + " y " + otro.getId(),
                ian.getId() > 0 && otro.getId() > 0);

        User logueado = usuarios.login(ian.getEmail(), "1234");
        resultado("Login correcto, rol: " + logueado.getRol(), logueado.getRol() == RolUsuario.ARTISTA);

        try {
            usuarios.login(ian.getEmail(), "contraseña-mala");
            resultado("Login con contraseña mala debería fallar", false);
        } catch (UsuarioNoEncontradoException e) {
            resultado("Login con contraseña mala rechazado: " + e.getMessage(), true);
        }

        try {
            usuarios.registrarArtista("copia", ian.getEmail(), "1234", "");
            resultado("Email repetido debería rechazarse", false);
        } catch (IllegalArgumentException e) {
            resultado("Email repetido rechazado: " + e.getMessage(), true);
        }

        System.out.println("\n=== OBRAS ===");
        Set<String> tags = new HashSet<>(Arrays.asList("#Retro", "paisaje", "retro"));
        Publicacion pub = publicaciones.publicar(ian, "Atardecer", "mi primera obra", crearImagen(), tags);
        resultado("Obra publicada con id " + pub.getId(), pub.getId() > 0);
        resultado("Tags normalizados (esperado 2): " + pub.getTags(), pub.getTags().size() == 2);

        Publicacion leida = publicaciones.buscarPorId(pub.getId());
        Header h = leida.getObra().getImagen().getHeader();
        resultado("Obra leída de la base: '" + leida.getObra().getTitulo() + "', imagen " + h.getWidth() + "x"
                + h.getHeight(), h.getWidth() == 20 && h.getHeight() == 20);

        resultado("Feed tiene al menos 1 obra", !publicaciones.verFeed().isEmpty());
        resultado("Búsqueda por tag 'RETRO' encuentra la obra", !publicaciones.buscarPorTag("RETRO").isEmpty());

        try {
            publicaciones.publicar(admin, "No debería", "", crearImagen(), tags);
            resultado("Un administrador no debería poder publicar", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("Administrador no puede publicar: " + e.getMessage(), true);
        }

        System.out.println("\n=== COMENTARIOS ===");
        Comentario c = comentarios.comentar(otro, pub.getId(), "Qué lindo!");
        resultado("Comentario guardado con id " + c.getId(), c.getId() > 0);
        resultado("La obra tiene 1 comentario",
                comentarios.listarPorPublicacion(pub.getId()).size() == 1);

        try {
            comentarios.editar(ian, c.getId(), "intento editar el de otro");
            resultado("Editar comentario ajeno debería rechazarse", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("Editar comentario ajeno rechazado: " + e.getMessage(), true);
        }

        System.out.println("\n=== PERMISOS SOBRE OBRAS ===");
        try {
            publicaciones.darDeBaja(otro, pub.getId());
            resultado("Otro artista no debería poder dar de baja la obra", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("Otro artista no puede dar de baja la obra: " + e.getMessage(), true);
        }

        System.out.println("\n=== COLECCIONES ===");
        Coleccion col = colecciones.crear(ian, "Favoritas", "mis obras preferidas");
        resultado("Colección creada con id " + col.getId(), col.getId() > 0);
        colecciones.agregarObra(ian, col.getId(), pub.getId());
        resultado("La colección tiene 1 obra", colecciones.buscarPorId(col.getId()).getIdsObras().size() == 1);

        System.out.println("\n=== ESTADÍSTICAS (MAP) ===");
        Map<User, Integer> mapa = usuarios.contarObrasPorUsuario(admin);
        for (Map.Entry<User, Integer> e : mapa.entrySet()) {
            System.out.println("  " + e.getKey().getNombre() + " -> " + e.getValue() + " obras");
        }
        resultado("El Map no está vacío", !mapa.isEmpty());

        System.out.println("\n=== BAJA LÓGICA ===");
        publicaciones.darDeBaja(admin, pub.getId());
        try {
            publicaciones.buscarPorId(pub.getId());
            resultado("La obra dada de baja no debería encontrarse", false);
        } catch (ObraNoEncontradaException e) {
            resultado("Obra dada de baja ya no aparece: " + e.getMessage(), true);
        }

        System.out.println("\nListo. Mirá las tablas en Workbench: la obra sigue en la base con activo = 0.");
    }

    // Imagen de prueba de 20x20 con un degradado
    private static Image crearImagen() {
        Image imagen = new Image(new Header(20, 20));
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                imagen.addPixel(new Pixel(x * 12, y * 12, 128));
            }
        }
        return imagen;
    }

    private static void resultado(String mensaje, boolean ok) {
        System.out.println((ok ? "  [OK]    " : "  [FALLO] ") + mensaje);
    }
}
