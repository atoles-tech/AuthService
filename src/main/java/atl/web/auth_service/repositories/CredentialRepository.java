package atl.web.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import atl.web.auth_service.model.Credential;

@Repository
public interface CredentialRepository extends JpaRepository<Credential,Long>{
    Optional<Credential> findByEmail(String email);
    Boolean existsByEmail(String username);
}
