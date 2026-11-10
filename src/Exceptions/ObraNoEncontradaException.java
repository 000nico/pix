package Exceptions;

// salta cuando no se encuentra la obra buscada
public class ObraNoEncontradaException extends Exception {

    public ObraNoEncontradaException(String message) {
        super(message);
    }

    public ObraNoEncontradaException() {
        super("obra no encontrada");
    }
}
