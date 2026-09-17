package mx.edu.uacm.userservices.respository;

import mx.edu.uacm.userservices.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
}
