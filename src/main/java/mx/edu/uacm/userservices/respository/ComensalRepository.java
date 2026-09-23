package mx.edu.uacm.userservices.respository;

import mx.edu.uacm.userservices.model.Comensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComensalRepository extends JpaRepository<Comensal, Long> {
    boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdNot(String telefono, Long id);
    Optional<Comensal> findByCorreoAndTelefono( String correo, String telefono);
    Optional<Comensal> findByCorreo(String correo);
}
