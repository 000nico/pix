package Classes.Gestoras;

import Classes.Users.User;
import Exceptions.UsuarioNoEncontradoException;

import java.util.HashMap;
import java.util.Map;

public class GestoraUsuarios {

     //id_usuario - cant_obreas
    private Map<User, Integer> usuarios= new HashMap<>();

    public void agregarUsuario(User user, int cantObras){
        usuarios.put(user, cantObras);
    }

    public void eliminarUsuario(User user){
        usuarios.remove(user);
    }

    public Integer obtenerUsuario(int idUsuario) throws UsuarioNoEncontradoException {
        for (Map.Entry<User, Integer> e : usuarios.entrySet()) {
            User user = e.getKey();

            if (user.getId() == idUsuario) {
                return user.getId();
            }
        }

        throw new UsuarioNoEncontradoException();
    }
}
