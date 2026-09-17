package mx.edu.uacm.userservices.exception;

public class ComensalNoEncontradoException extends  RuntimeException{
    public ComensalNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
