package atl.web.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import atl.web.auth_service.model.Credential;
import atl.web.auth_service.model.RefreshToken;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByCredential(Credential credential);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Boolean existsByCredential(Credential credential);

    void deleteByRefreshToken(String refreshToken);

    @Modifying
    @Query("UPDATE RefreshToken SET isActive = false WHERE credential = :credential")
    void deactiveRefreshToken(@Param("credential") Credential credential);
}
