package mx.edu.uacm.userservices.exception;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException(String mensaje){
        super(mensaje);
    }
}
