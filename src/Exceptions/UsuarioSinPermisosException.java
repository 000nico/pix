package Exceptions;

// salta cuando un usuario quiere hacer algo que no le corresponde por su rol
public class UsuarioSinPermisosException extends Exception {

    public UsuarioSinPermisosException(String message) {
        super(message);
    }

    public UsuarioSinPermisosException() {
        super("usuario sin permisos");
    }
}
