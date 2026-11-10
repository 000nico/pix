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
import DataBase.Service.ColeccionService;
import DataBase.Service.ComentarioService;
import DataBase.Service.PublicacionService;
import DataBase.Service.UsuarioService;
import Enum.RolUsuario;
import Exceptions.ObraNoEncontradaException;
import Exceptions.UsuarioNoEncontradoException;
import Exceptions.UsuarioSinPermisosException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// prueba de toda la capa de datos y servicios contra la base mysql. cada linea imprime [ok] o [fallo]. se puede correr varias veces porque los emails llevan un sufijo unico
public class PruebaMain {

    public static void main(String[] args) throws Exception {
        String sufijo = String.valueOf(System.currentTimeMillis());

        UsuarioService usuarios = new UsuarioService();
        PublicacionService publicaciones = new PublicacionService();
        ComentarioService comentarios = new ComentarioService();
        ColeccionService colecciones = new ColeccionService();

        System.out.println("=== usuarios ===");
        // el primer administrador se crea directo con el dao porque el servicio exige ser admin para crear admins
        Administrator admin = new Administrator(0, "admin", "admin" + sufijo + "@pix.com", "1234", RolUsuario.ADMINISTRADOR);
        admin.setNivelAcceso(2);
        new UsuarioDAO().insertar(admin);
        resultado("administrador guardado con id " + admin.getId(), admin.getId() > 0);

        Artist ian = usuarios.registrarArtista("ian", "ian" + sufijo + "@mail.com", "1234", "pixel artist");
        Artist otro = usuarios.registrarArtista("otro", "otro" + sufijo + "@mail.com", "1234", "otra bio");
        resultado("artistas registrados con id " + ian.getId() + " y " + otro.getId(), ian.getId() > 0 && otro.getId() > 0);

        User logueado = usuarios.login(ian.getEmail(), "1234");
        resultado("login correcto, rol: " + logueado.getRol(), logueado.getRol() == RolUsuario.ARTISTA);

        try {
            usuarios.login(ian.getEmail(), "mala");
            resultado("login con password mala deberia fallar", false);
        } catch (UsuarioNoEncontradoException e) {
            resultado("login con password mala rechazado", true);
        }

        try {
            usuarios.registrarArtista("copia", ian.getEmail(), "1234", "");
            resultado("email repetido deberia rechazarse", false);
        } catch (IllegalArgumentException e) {
            resultado("email repetido rechazado", true);
        }

        System.out.println("\n=== obras ===");
        Set<String> tags = new HashSet<String>(Arrays.asList("#Retro", "paisaje", "retro"));
        Publicacion pub = publicaciones.publicar(ian, "Atardecer", "mi primera obra", crearImagen(), tags);
        resultado("obra publicada con id " + pub.getId(), pub.getId() > 0);
        resultado("tags normalizados, esperado 2: " + pub.getTags(), pub.getTags().size() == 2);

        Publicacion leida = publicaciones.buscarPorId(pub.getId());
        Header h = leida.getObra().getImagen().getHeader();
        resultado("obra leida de la base: " + leida.getObra().getTitulo() + ", imagen " + h.getWidth() + "x" + h.getHeight(), h.getWidth() == 20 && h.getHeight() == 20);

        resultado("el feed tiene al menos 1 obra", !publicaciones.verFeed().isEmpty());
        resultado("busqueda por tag retro encuentra la obra", !publicaciones.buscarPorTag("RETRO").isEmpty());

        try {
            publicaciones.publicar(admin, "no deberia", "", crearImagen(), tags);
            resultado("un administrador no deberia poder publicar", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("administrador no puede publicar", true);
        }

        System.out.println("\n=== comentarios ===");
        Comentario c = comentarios.comentar(otro, pub.getId(), "que lindo!");
        resultado("comentario guardado con id " + c.getId(), c.getId() > 0);
        resultado("la obra tiene 1 comentario", comentarios.listarPorPublicacion(pub.getId()).size() == 1);

        try {
            comentarios.editar(ian, c.getId(), "intento editar el de otro");
            resultado("editar comentario ajeno deberia rechazarse", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("editar comentario ajeno rechazado", true);
        }

        System.out.println("\n=== permisos sobre obras ===");
        try {
            publicaciones.darDeBaja(otro, pub.getId());
            resultado("otro artista no deberia poder dar de baja la obra", false);
        } catch (UsuarioSinPermisosException e) {
            resultado("otro artista no puede dar de baja la obra", true);
        }

        System.out.println("\n=== colecciones ===");
        Coleccion col = colecciones.crear(ian, "Favoritas", "mis obras preferidas");
        resultado("coleccion creada con id " + col.getId(), col.getId() > 0);
        colecciones.agregarObra(ian, col.getId(), pub.getId());
        resultado("la coleccion tiene 1 obra", colecciones.buscarPorId(col.getId()).getIdsObras().size() == 1);

        System.out.println("\n=== estadisticas (map con la gestora) ===");
        Map<User, Integer> mapa = usuarios.contarObrasPorUsuario(admin);
        for (Map.Entry<User, Integer> entrada : mapa.entrySet()) {
            System.out.println("  " + entrada.getKey().getNombre() + " -> " + entrada.getValue() + " obras");
        }
        resultado("el map no esta vacio", !mapa.isEmpty());

        System.out.println("\n=== baja logica ===");
        publicaciones.darDeBaja(admin, pub.getId());
        try {
            publicaciones.buscarPorId(pub.getId());
            resultado("la obra dada de baja no deberia encontrarse", false);
        } catch (ObraNoEncontradaException e) {
            resultado("obra dada de baja ya no aparece", true);
        }

        System.out.println("\nlisto. mira las tablas en workbench: la obra sigue en la base con activo = 0.");
    }

    // imagen de prueba de 20x20 con un degradado
    private static Image crearImagen() {
        Image imagen = new Image(new Header(20, 20));
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                imagen.addPixel(new Pixel(x * 12, y * 12, 128));
            }
        }
        return imagen;
    }

    // imprime el mensaje con [ok] o [fallo] segun el resultado
    private static void resultado(String mensaje, boolean ok) {
        System.out.println((ok ? "  [ok]    " : "  [fallo] ") + mensaje);
    }
}
