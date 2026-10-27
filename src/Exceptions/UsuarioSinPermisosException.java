package Exceptions;

public class UsuarioSinPermisosException extends Exception {
    public UsuarioSinPermisosException(String message) {
        super(message);
    }
    public UsuarioSinPermisosException() {
        super("Usuario Sin Permiso");
    }
}
