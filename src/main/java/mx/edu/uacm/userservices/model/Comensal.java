package mx.edu.uacm.userservices.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class Comensal extends Usuario{
    @NotBlank(message = "El correo es obligatorio")
    @Pattern(
            regexp = "\\d{10}",
            message = "El teléfono debe contener 10 dígitos"
    )
    @Column(nullable = false, unique = true)
    private String telefono;

    public Comensal(){
    }

    public Comensal(String nombre, String apeMaterno, String apePaterno, String contrasenia, String correo, String telefono) {
        super(nombre, apeMaterno, apePaterno, contrasenia, correo);
        this.telefono = telefono;
    }

}
