package net.risesoft.oidc.configuration;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.authorization.repository.Oauth2AuthorizationConsentRepository;
import net.risesoft.oidc.authorization.repository.Oauth2AuthorizationRepository;
import net.risesoft.oidc.authorization.repository.Oauth2RegisteredClientRepository;
import net.risesoft.oidc.authorization.service.JpaOAuth2AuthorizationConsentService;
import net.risesoft.oidc.authorization.service.JpaOAuth2AuthorizationService;
import net.risesoft.oidc.authorization.service.JpaRegisteredClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Slf4j
//@Configuration(proxyBeanMethods = false)
public class JpaClientRepositoryConfig {

    @Bean
    public JpaRegisteredClientRepository jpaRegisteredClientRepository(
            Oauth2RegisteredClientRepository oauth2RegisteredClientRepository) {
        return new JpaRegisteredClientRepository(oauth2RegisteredClientRepository);
    }

    @Bean
    public JpaOAuth2AuthorizationService authorizationService(
            Oauth2AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
    }

    @Bean
    public JpaOAuth2AuthorizationConsentService authorizationConsentService(
            Oauth2AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository);
    }

}
