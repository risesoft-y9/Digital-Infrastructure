package y9.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalAttributesRepository;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.mfa.BaseMultifactorAuthenticationProviderProperties;
import org.apereo.cas.services.CasRegisteredService;
import org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.util.Y9Result;

@Lazy(false)
@RestController
@RequestMapping(value = "/api/service", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class ServiceController {
    private static final String SYSTEM_KEY = "systemId";

    private final ServicesManager servicesManager;
    private final CasConfigurationProperties casConfigurationProperties;

    @PostMapping(value = "/delete")
    public Y9Result<RegisteredService> delete(long id) {
        RegisteredService registeredService = servicesManager.delete(id);
        return Y9Result.success(registeredService);
    }

    @GetMapping(value = "/findBySystemId")
    public Y9Result<Collection<RegisteredService>> findBySystemId(String systemId) {
        Collection<RegisteredService> services = servicesManager.findServiceBy(registeredService -> {
            RegisteredServiceProperty systemIdProperty = registeredService.getProperties().get(SYSTEM_KEY);
            if (systemIdProperty == null) {
                return false;
            }
            return systemIdProperty.contains(systemId);
        });
        return Y9Result.success(services);
    }

    @GetMapping(value = "/{id}")
    public Y9Result<RegisteredService> get(@PathVariable long id) {
        return Y9Result.success(servicesManager.findServiceBy(id));
    }

    @GetMapping(value = "/listAll")
    public Y9Result<Collection<RegisteredService>> listAll() {
        return Y9Result.success(servicesManager.getAllServices());
    }

    @PostMapping(value = "/oAuthService")
    public Y9Result<String> oAuthService(@RequestParam(required = false) Long id,
        @RequestParam(required = false) String theme, String clientId, String clientSecret, String serviceId,
        String name, @RequestParam(required = false) String description,
        @RequestParam(required = false) String systemId, @RequestParam(required = false) String logoutUrl) {
        try {
            OidcRegisteredService svc = new OidcRegisteredService();
            if (id != null) {
                // 更新
                svc.setId(id);
            }
            svc.setClientId(clientId);
            svc.setClientSecret(clientSecret);
            svc.setServiceId(serviceId);
            svc.setName(name);
            svc.setDescription(description);
            svc.setTheme(theme.isBlank() ? casConfigurationProperties.getTheme().getDefaultThemeName() : theme);
            svc.setEvaluationOrder(100);
            svc.setLogoutUrl(logoutUrl);
            ReturnAllAttributeReleasePolicy returnAllAttributeReleasePolicy = new ReturnAllAttributeReleasePolicy();
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseCredentialPassword(true);
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseProxyGrantingTicket(true);
            returnAllAttributeReleasePolicy.setExcludeDefaultAttributes(false);
            DefaultPrincipalAttributesRepository defaultPrincipalAttributesRepository =
                new DefaultPrincipalAttributesRepository();
            returnAllAttributeReleasePolicy.setPrincipalAttributesRepository(defaultPrincipalAttributesRepository);
            svc.setAttributeReleasePolicy(returnAllAttributeReleasePolicy);
            svc.setSupportedGrantTypes(
                new HashSet<>(Arrays.asList("authorization_code", "password", "client_credentials", "refresh_token")));
            svc.setSupportedResponseTypes(new HashSet<>(Arrays.asList("code", "token")));
            svc.setBypassApprovalPrompt(true);
            svc.setGenerateRefreshToken(true);
            svc.setRenewRefreshToken(true);
            svc.setJwtAccessToken(false);
            if (StringUtils.isNotBlank(systemId)) {
                Map<String, RegisteredServiceProperty> propertyMap = new HashMap<>();
                propertyMap.put(SYSTEM_KEY, new DefaultRegisteredServiceProperty(systemId));
                svc.setProperties(propertyMap);
            }
            servicesManager.save(svc);

            servicesManager.load();
            return Y9Result.successMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("刷新失败");
        }
    }

    @PostMapping(value = "/regexService")
    public Y9Result<String> regexRegisteredService(String serviceId, String name, String description) {
        try {
            CasRegisteredService regexRegisteredService = new CasRegisteredService();
            regexRegisteredService.setServiceId(serviceId);
            regexRegisteredService.setName(name);
            regexRegisteredService.setDescription(description);
            regexRegisteredService.setTheme(casConfigurationProperties.getTheme().getDefaultThemeName());
            regexRegisteredService.setEvaluationOrder(100);
            ReturnAllAttributeReleasePolicy returnAllAttributeReleasePolicy = new ReturnAllAttributeReleasePolicy();
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseCredentialPassword(true);
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseProxyGrantingTicket(true);
            returnAllAttributeReleasePolicy.setExcludeDefaultAttributes(false);
            DefaultPrincipalAttributesRepository defaultPrincipalAttributesRepository =
                new DefaultPrincipalAttributesRepository();
            returnAllAttributeReleasePolicy.setPrincipalAttributesRepository(defaultPrincipalAttributesRepository);
            regexRegisteredService.setAttributeReleasePolicy(returnAllAttributeReleasePolicy);
            DefaultRegisteredServiceMultifactorPolicy defaultRegisteredServiceMultifactorPolicy =
                new DefaultRegisteredServiceMultifactorPolicy();
            defaultRegisteredServiceMultifactorPolicy.setFailureMode(
                BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.CLOSED);
            defaultRegisteredServiceMultifactorPolicy.setBypassEnabled(false);
            regexRegisteredService.setMultifactorAuthenticationPolicy(defaultRegisteredServiceMultifactorPolicy);
            servicesManager.save(regexRegisteredService);

            servicesManager.load();
            return Y9Result.successMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("刷新失败");
        }
    }

    @GetMapping(value = "/reload")
    public Y9Result<String> reload() {
        try {
            servicesManager.load();
            return Y9Result.successMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("刷新失败");
        }
    }
}
