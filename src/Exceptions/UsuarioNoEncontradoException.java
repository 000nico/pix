package Exceptions;

public class UsuarioNoEncontradoException extends Exception {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
    public UsuarioNoEncontradoException() { super("Usuario No Encontrado");}

}
