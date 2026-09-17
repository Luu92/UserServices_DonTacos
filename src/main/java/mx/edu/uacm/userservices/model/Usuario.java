package mx.edu.uacm.userservices.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Column(nullable = false, unique = true)
    private String correo;
    private String contrasenia;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    public Usuario(){
    }

    public Usuario(String nombre, String apeMaterno, String apePaterno, String contrasenia, String correo) {
        this.nombre = nombre;
        this.apeMaterno = apeMaterno;
        this.apePaterno = apePaterno;
        this.contrasenia = contrasenia;
        this.correo = correo;
    }
}
