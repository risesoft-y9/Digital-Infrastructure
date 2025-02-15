package net.risesoft.oidc.authorization.repository;

import net.risesoft.oidc.authorization.entity.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Copy this interface from How-to: Implement core services with JPA documentation: https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html

@Transactional
@Repository
public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String> {
    Optional<Oauth2RegisteredClient> findByClientId(String clientId);
}
