package mx.edu.uacm.userservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComensalResponse {
    private Long id;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String correo;
    private String telefono;
    private String rol;
}
