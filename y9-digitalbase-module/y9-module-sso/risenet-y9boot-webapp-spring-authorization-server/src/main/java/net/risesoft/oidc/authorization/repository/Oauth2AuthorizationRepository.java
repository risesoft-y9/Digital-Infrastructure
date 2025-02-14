package net.risesoft.oidc.authorization.repository;

import java.util.Optional;
import net.risesoft.oidc.authorization.entity.Oauth2Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


// Copy this interface from How-to: Implement core services with JPA documentation: https://docs.spring.io/spring-Oauth2Authorization-server/reference/guides/how-to-jpa.html

@Repository
public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, String> {
    Optional<Oauth2Authorization> findByState(String state);
    Optional<Oauth2Authorization> findByAuthorizationCodeValue(String authorizationCode);
    Optional<Oauth2Authorization> findByAccessTokenValue(String accessToken);
    Optional<Oauth2Authorization> findByRefreshTokenValue(String refreshToken);
    Optional<Oauth2Authorization> findByOidcIdTokenValue(String idToken);
    Optional<Oauth2Authorization> findByUserCodeValue(String userCode);
    Optional<Oauth2Authorization> findByDeviceCodeValue(String deviceCode);
    @Query("select a from Oauth2Authorization a where a.state = :token" +
            " or a.authorizationCodeValue = :token" +
            " or a.accessTokenValue = :token" +
            " or a.refreshTokenValue = :token" +
            " or a.oidcIdTokenValue = :token" +
            " or a.userCodeValue = :token" +
            " or a.deviceCodeValue = :token"
    )
    Optional<Oauth2Authorization> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(@Param("token") String token);
}