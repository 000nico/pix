package Exceptions;

// salta cuando no se encuentra el usuario buscado o falla el login
public class UsuarioNoEncontradoException extends Exception {

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNoEncontradoException() {
        super("usuario no encontrado");
    }
}
