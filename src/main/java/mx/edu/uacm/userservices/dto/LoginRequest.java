package mx.edu.uacm.userservices.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String contrasenia;
}
