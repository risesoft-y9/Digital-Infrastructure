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
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.util.Y9Result;

@RestController
@RequestMapping(value = "/api/service", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class ServiceController {
    private static final String SYSTEM_KEY = "systemId";

    private final ServicesManager servicesManager;
    private final CasConfigurationProperties casConfigurationProperties;

    @DeleteMapping(value = "/{id}")
    public Y9Result<RegisteredService> delete(@PathVariable long id) {
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
        Y9Result<String> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("刷新失败");
        y9result.setSuccess(false);
        try {
            OAuthRegisteredService oAuthRegisteredService = new OAuthRegisteredService();
            if (id != null) {
                // 更新
                oAuthRegisteredService.setId(id);
            }
            oAuthRegisteredService.setClientId(clientId);
            oAuthRegisteredService.setClientSecret(clientSecret);
            oAuthRegisteredService.setServiceId(serviceId);
            oAuthRegisteredService.setName(name);
            oAuthRegisteredService.setDescription(description);
            oAuthRegisteredService
                .setTheme(theme.isBlank() ? casConfigurationProperties.getTheme().getDefaultThemeName() : theme);
            oAuthRegisteredService.setEvaluationOrder(100);
            oAuthRegisteredService.setLogoutUrl(logoutUrl);
            ReturnAllAttributeReleasePolicy returnAllAttributeReleasePolicy = new ReturnAllAttributeReleasePolicy();
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseCredentialPassword(true);
            returnAllAttributeReleasePolicy.setAuthorizedToReleaseProxyGrantingTicket(true);
            returnAllAttributeReleasePolicy.setExcludeDefaultAttributes(false);
            DefaultPrincipalAttributesRepository defaultPrincipalAttributesRepository =
                new DefaultPrincipalAttributesRepository();
            returnAllAttributeReleasePolicy.setPrincipalAttributesRepository(defaultPrincipalAttributesRepository);
            oAuthRegisteredService.setAttributeReleasePolicy(returnAllAttributeReleasePolicy);
            oAuthRegisteredService.setSupportedGrantTypes(
                new HashSet<>(Arrays.asList("authorization_code", "password", "client_credentials", "refresh_token")));
            oAuthRegisteredService.setSupportedResponseTypes(new HashSet<>(Arrays.asList("code", "token")));
            oAuthRegisteredService.setBypassApprovalPrompt(true);
            oAuthRegisteredService.setGenerateRefreshToken(true);
            oAuthRegisteredService.setRenewRefreshToken(true);
            oAuthRegisteredService.setJwtAccessToken(false);
            if (StringUtils.isNotBlank(systemId)) {
                Map<String, RegisteredServiceProperty> propertyMap = new HashMap<>();
                propertyMap.put(SYSTEM_KEY, new DefaultRegisteredServiceProperty(systemId));
                oAuthRegisteredService.setProperties(propertyMap);
            }
            servicesManager.save(oAuthRegisteredService);

            servicesManager.load();
            y9result.setCode(200);
            y9result.setMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
            y9result.setSuccess(true);
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }

    @PostMapping(value = "/regexService")
    public Y9Result<String> regexRegisteredService(String serviceId, String name, String description) {
        Y9Result<String> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("刷新失败");
        y9result.setSuccess(false);
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
            y9result.setCode(200);
            y9result.setMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
            y9result.setSuccess(true);
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }

    @GetMapping(value = "/reload")
    public Y9Result<String> reload() {
        Y9Result<String> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("刷新失败");
        y9result.setSuccess(false);
        try {
            servicesManager.load();
            y9result.setCode(200);
            y9result.setMsg("刷新成功,总共：" + servicesManager.count() + " 条记录");
            y9result.setSuccess(true);
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }
}
