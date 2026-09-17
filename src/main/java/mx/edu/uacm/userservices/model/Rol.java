package mx.edu.uacm.userservices.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    public Rol() {
    }

    public Rol(String nombre) {
        this.nombre = nombre;
    }
}
