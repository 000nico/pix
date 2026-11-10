package DataBase.Service;

import Classes.Users.Administrator;
import Classes.Users.Artist;
import Classes.Users.User;
import DataBase.DAO.PublicacionDAO;
import DataBase.DAO.UsuarioDAO;
import Enum.RolUsuario;
import Exceptions.UsuarioNoEncontradoException;
import Exceptions.UsuarioSinPermisosException;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

 //Servicio de usuarios: tiene las reglas (validaciones y permisos) y usa el DAO para guardar.
 //Los métodos que requieren permisos reciben "quienPide", el usuario que hace la acción.

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PublicacionDAO publicacionDAO = new PublicacionDAO();

    // Registro abierto: cualquiera puede crearse una cuenta de artista.
    public Artist registrarArtista(String nombre, String email, String password, String bio) throws SQLException {
        validarDatos(nombre, email, password);
        verificarEmailLibre(email);

        Artist artista = new Artist(0, nombre.trim(), email.trim(), password, RolUsuario.ARTISTA, bio);
        usuarioDAO.insertar(artista);
        return artista;
    }

    // Solo un administrador puede crear a otro administrador.
    public Administrator crearAdministrador(User quienPide, String nombre, String email, String password,
                                            int nivelAcceso) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        validarDatos(nombre, email, password);
        verificarEmailLibre(email);

        Administrator admin = new Administrator(0, nombre.trim(), email.trim(), password, RolUsuario.ADMINISTRADOR);
        admin.setNivelAcceso(nivelAcceso);
        usuarioDAO.insertar(admin);
        return admin;
    }

    // Login: devuelve el usuario si el email y la contraseña coinciden.
    public User login(String email, String password) throws UsuarioNoEncontradoException, SQLException {
        Optional<User> encontrado = usuarioDAO.buscarPorEmail(email);

        if (!encontrado.isPresent() || !encontrado.get().getPassword().equals(password)) {
            throw new UsuarioNoEncontradoException("Email o contraseña incorrectos");
        }
        return encontrado.get();
    }

    public User buscarPorId(int idUsuario) throws UsuarioNoEncontradoException, SQLException {
        // orElseThrow es un metodo del optional
        return usuarioDAO.buscarPorId(idUsuario).orElseThrow(UsuarioNoEncontradoException::new);
    }

    // Listado completo de usuarios: solo administradores.
    public List<User> listar(User quienPide) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        return usuarioDAO.listar();
    }

    // Cada usuario modifica sus propios datos; un administrador puede modificar los de cualquiera.
    public void actualizar(User quienPide, User modificado)
            throws UsuarioSinPermisosException, UsuarioNoEncontradoException, SQLException {
        if (!esAdministrador(quienPide) && quienPide.getId() != modificado.getId()) {
            throw new UsuarioSinPermisosException("Solo podés modificar tus propios datos");
        }
        validarDatos(modificado.getNombre(), modificado.getEmail(), modificado.getPassword());
        buscarPorId(modificado.getId());   // si no existe, lanza la excepción

        usuarioDAO.actualizar(modificado);
    }

    // Baja lógica de un usuario: solo administradores.
    public void darDeBaja(User quienPide, int idUsuario)
            throws UsuarioSinPermisosException, UsuarioNoEncontradoException, SQLException {
        exigirAdministrador(quienPide);
        buscarPorId(idUsuario);
        usuarioDAO.bajaLogica(idUsuario);
    }

    // Deshace la baja lógica: solo administradores.
    public void reactivar(User quienPide, int idUsuario) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);
        usuarioDAO.reactivar(idUsuario);
    }

    // Map de usuarios con la cantidad de obras que tiene cada uno (estadística para el administrador).
    // Se usa un Map porque cada usuario aparece una sola vez y se lo asocia con un número.
    public Map<User, Integer> contarObrasPorUsuario(User quienPide) throws UsuarioSinPermisosException, SQLException {
        exigirAdministrador(quienPide);

        Map<User, Integer> resultado = new LinkedHashMap<>();
        for (User u : usuarioDAO.listar()) {
            int cantidad = 0;
            if (u.getRol() == RolUsuario.ARTISTA) {
                cantidad = publicacionDAO.contarPorArtista(u.getId());
            }
            resultado.put(u, cantidad);
        }
        return resultado;
    }

    //  auxiliares

    private boolean esAdministrador(User user) {
        return user.getRol() == RolUsuario.ADMINISTRADOR;
    }

    private void exigirAdministrador(User user) throws UsuarioSinPermisosException {
        if (!esAdministrador(user)) {
            throw new UsuarioSinPermisosException("Esta acción es solo para administradores");
        }
    }

    private void validarDatos(String nombre, String email, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
    }

    private void verificarEmailLibre(String email) throws SQLException {
        if (usuarioDAO.buscarPorEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
    }
}
