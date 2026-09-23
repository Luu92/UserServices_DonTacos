package mx.edu.uacm.userservices.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NuevaContraseniaRequest {

    @NotBlank(message = "El token de recuperación es obligatorio")
    private String recoveryToken;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaContrasenia;


}
