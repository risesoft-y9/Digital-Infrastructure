package net.risesoft.oidc.authorization.repository;

import java.util.Optional;

import net.risesoft.oidc.authorization.entity.Oauth2AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


// Copy this interface from How-to: Implement core services with JPA documentation: https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html

@Transactional
@Repository
public interface Oauth2AuthorizationConsentRepository extends JpaRepository<Oauth2AuthorizationConsent, Oauth2AuthorizationConsent.AuthorizationConsentId> {
    Optional<Oauth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
