package mx.edu.uacm.userservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecuperarCuentaResponse {
    private String mensaje;
    private String recoveryToken;
}
