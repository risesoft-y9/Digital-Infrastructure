package net.risesoft.oidc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.y9.entity.Y9User;
import net.risesoft.oidc.y9.repository.Y9UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

/**
 * 用于生成测试的用户
 * @author dingzhaojun
 * @date 2023/8/13
 * @since 9.6.3
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {
    private final Y9UserRepository y9UserRepository;
    private final PasswordEncoder passwordEncoder;

    private final JdbcRegisteredClientRepository clientRepository;
    //private final JpaRegisteredClientRepository clientRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Y9User y9User = null;
        try {
            y9User = y9UserRepository.findByPersonIdAndTenantId("11111111-1111-1111-1111-111111111117",
                "11111111-1111-1111-1111-111111111113");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (y9User == null) {
            y9User = new Y9User();
            y9User.setId("11111111-1111-1111-1111-111111111117");
            y9User.setPersonId("11111111-1111-1111-1111-111111111117");
            y9User.setParentId("11111111-1111-1111-1111-111111111115");
            y9User.setTenantId("11111111-1111-1111-1111-111111111113");
            y9User.setDn("cn=系统管理员,o=组织");
            y9User.setGlobalManager(true);
            y9User.setGuidPath("11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111117");
            y9User.setLoginName("systemManager");
            y9User.setManagerLevel(1);
            y9User.setName("系统管理员");
            y9User.setOriginal(true);
            y9User.setPassword(passwordEncoder.encode("Risesoft@2025"));
            y9User.setPersonType("Manager");
            y9User.setSex(1);
            y9User.setTenantName("default");
            y9User.setTenantShortName("default");
            //y9User.setCreateTime(new Date());
            //y9User.setUpdateTime(new Date());
            // y9User.setIdNum(null);
            // y9User.setOriginalId(null);
            // y9User.setEmail(null);
            // y9User.setMobile(null);
            // y9User.setOrderedPath(null);
            // y9User.setPositions(null);
            // y9User.setRoles(null);
            y9UserRepository.save(y9User);
        }

        RegisteredClient client01 = RegisteredClient.withId("client01")
                .clientId("clientid_oidc")
                .clientSecret(passwordEncoder.encode("secret_oidc"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:7099/mydemo/login/oauth2/code/client01")
                .postLogoutRedirectUri("http://localhost:7099/mydemo")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        clientRepository.save(client01);
    }
}
