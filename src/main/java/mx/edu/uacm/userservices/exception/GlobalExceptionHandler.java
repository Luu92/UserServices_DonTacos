package mx.edu.uacm.userservices.exception;

import mx.edu.uacm.userservices.dto.MensajeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(
            MethodArgumentNotValidException exception) {

        Map<String, String> errores = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<MensajeResponse> manejarCredencialesInvalidas(
            CredencialesInvalidasException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeResponse(exception.getMessage()));
    }

    @ExceptionHandler(ComensalNoEncontradoException.class)
    public ResponseEntity<MensajeResponse> manejarComensalNoEncontrado(
            ComensalNoEncontradoException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MensajeResponse(exception.getMessage()));
    }

    @ExceptionHandler(TelefonoRegistradoException.class)
    public ResponseEntity<MensajeResponse> TelefonoRegistradoException(TelefonoRegistradoException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MensajeResponse(exception.getMessage()));
    }

    @ExceptionHandler(RecuperarCuentaException.class)
    public ResponseEntity<MensajeResponse> RecuperarCuentaException(RecuperarCuentaException exception){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeResponse(exception.getMessage()));
    }

}
