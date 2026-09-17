package mx.edu.uacm.userservices.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Administrador extends Usuario{

    public Administrador() {
    }

    public Administrador(String nombre, String apeMaterno, String apePaterno, String contrasenia, String correo) {
        super(nombre, apeMaterno, apePaterno, contrasenia, correo);
    }

}
