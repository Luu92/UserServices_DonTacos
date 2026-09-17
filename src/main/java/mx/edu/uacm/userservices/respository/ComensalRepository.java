package mx.edu.uacm.userservices.respository;

import mx.edu.uacm.userservices.model.Comensal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComensalRepository extends JpaRepository<Comensal, Long> {
    boolean existsByTelefono(String telefono);
}
