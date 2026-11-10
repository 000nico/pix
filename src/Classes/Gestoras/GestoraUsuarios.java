package Classes.Gestoras;

import Classes.Users.User;
import Exceptions.UsuarioNoEncontradoException;

import java.util.HashMap;
import java.util.Map;

// gestora de usuarios: guarda a cada usuario junto con la cantidad de obras que tiene. la usa el usuarioservice para las estadisticas del administrador
public class GestoraUsuarios {

    // usuario -> cantidad de obras
    private Map<User, Integer> usuarios = new HashMap<User, Integer>();

    // agrega un usuario con su cantidad de obras
    public void agregarUsuario(User user, int cantObras) {
        usuarios.put(user, cantObras);
    }

    // saca un usuario del map
    public void eliminarUsuario(User user) {
        usuarios.remove(user);
    }

    // busca un usuario por id y devuelve su cantidad de obras
    public int cantidadObras(int idUsuario) throws UsuarioNoEncontradoException {
        for (User user : usuarios.keySet()) {
            if (user.getId() == idUsuario) {
                return usuarios.get(user);
            }
        }
        throw new UsuarioNoEncontradoException("no existe el usuario " + idUsuario);
    }

    public Map<User, Integer> getUsuarios() {
        return usuarios;
    }
}
