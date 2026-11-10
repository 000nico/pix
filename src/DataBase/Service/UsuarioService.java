package DataBase.Service;

import Classes.Gestoras.GestoraUsuarios;
import Classes.Users.Administrator;
import Classes.Users.Artist;
import Classes.Users.User;
import DataBase.DAO.PublicacionDAO;
import DataBase.DAO.UsuarioDAO;
import Enum.RolUsuario;
import Exceptions.UsuarioNoEncontradoException;
import Exceptions.UsuarioSinPermisosException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// servicio de usuarios: tiene las validaciones y los permisos, y usa el dao para guardar. los metodos que requieren permisos reciben "quienpide", el usuario que hace la accion
public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PublicacionDAO publicacionDAO = new PublicacionDAO();

    // registro abierto: cualquiera puede crearse una cuenta de artista
    public Artist registrarArtista(String nombre, String email, String password, String bio) throws SQLException {
        validarDatos(nombre, email, password);
        verificarEmailLibre(email);

        Artist artista = new Artist(0, nombre.trim(), email.trim(), password, RolUsuario.ARTISTA, bio);
        usuarioDAO.insertar(artista);
        return artista;
    }

    // solo un administrador puede crear a otro administrador
    public Administrator crearAdministrador(User quienPide, String nombre, String email, String password, int nivelAcceso) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        validarDatos(nombre, email, password);
        verificarEmailLibre(email);

        Administrator admin = new Administrator(0, nombre.trim(), email.trim(), password, RolUsuario.ADMINISTRADOR);
        admin.setNivelAcceso(nivelAcceso);
        usuarioDAO.insertar(admin);
        return admin;
    }

    // login: devuelve el usuario si el email y la password coinciden
    public User login(String email, String password) throws UsuarioNoEncontradoException, SQLException {
        User user = usuarioDAO.buscarPorEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            throw new UsuarioNoEncontradoException("email o password incorrectos");
        }
        return user;
    }

    // busca un usuario por id
    public User buscarPorId(int idUsuario) throws UsuarioNoEncontradoException, SQLException {
        User user = usuarioDAO.buscarPorId(idUsuario);
        if (user == null) {
            throw new UsuarioNoEncontradoException("no existe el usuario " + idUsuario);
        }
        return user;
    }

    // listado completo de usuarios: solo administradores
    public List<User> listar(User quienPide) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        return usuarioDAO.listar();
    }

    // cada usuario modifica sus propios datos, un administrador puede modificar los de cualquiera
    public void actualizar(User quienPide, User modificado) throws UsuarioSinPermisosException, UsuarioNoEncontradoException, SQLException {
        if (!esAdministrador(quienPide) && quienPide.getId() != modificado.getId()) {
            throw new UsuarioSinPermisosException("solo podes modificar tus propios datos");
        }
        validarDatos(modificado.getNombre(), modificado.getEmail(), modificado.getPassword());
        buscarPorId(modificado.getId());

        usuarioDAO.actualizar(modificado);
    }

    // baja logica de un usuario: solo administradores
    public void darDeBaja(User quienPide, int idUsuario) throws UsuarioSinPermisosException, UsuarioNoEncontradoException, SQLException {
        exigirAdministrador(quienPide);
        buscarPorId(idUsuario);
        usuarioDAO.bajaLogica(idUsuario);
    }

    // deshace la baja logica: solo administradores
    public void reactivar(User quienPide, int idUsuario) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        usuarioDAO.reactivar(idUsuario);
    }

    // estadistica para el administrador: arma el map usuario -> cantidad de obras usando la gestora de usuarios
    public Map<User, Integer> contarObrasPorUsuario(User quienPide) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);

        GestoraUsuarios gestora = new GestoraUsuarios();
        List<User> usuarios = usuarioDAO.listar();
        for (int i = 0; i < usuarios.size(); i++) {
            User u = usuarios.get(i);
            int cantidad = 0;
            if (u.getRol() == RolUsuario.ARTISTA) {
                cantidad = publicacionDAO.contarPorArtista(u.getId());
            }
            gestora.agregarUsuario(u, cantidad);
        }
        return gestora.getUsuarios();
    }

    // auxiliares

    private boolean esAdministrador(User user) {
        return user.getRol() == RolUsuario.ADMINISTRADOR;
    }

    private void exigirAdministrador(User user) throws UsuarioSinPermisosException {
        if (!esAdministrador(user)) {
            throw new UsuarioSinPermisosException("esta accion es solo para administradores");
        }
    }

    // revisa que el nombre, el email y la password esten bien
    private void validarDatos(String nombre, String email, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("el nombre es obligatorio");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("el email no es valido");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("la password debe tener al menos 4 caracteres");
        }
    }

    // revisa que nadie mas este usando ese email
    private void verificarEmailLibre(String email) throws SQLException {
        if (usuarioDAO.buscarPorEmail(email.trim()) != null) {
            throw new IllegalArgumentException("ya existe un usuario con ese email");
        }
    }
}
