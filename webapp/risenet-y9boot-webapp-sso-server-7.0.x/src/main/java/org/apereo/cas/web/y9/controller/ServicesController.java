package org.apereo.cas.web.y9.controller;

import java.util.Arrays;
import java.util.HashSet;

import org.apereo.cas.authentication.principal.DefaultPrincipalAttributesRepository;
import org.apereo.cas.configuration.model.support.mfa.BaseMultifactorAuthenticationProviderProperties;
import org.apereo.cas.services.CasRegisteredService;
import org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.web.y9.util.Y9Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/services")
@Slf4j
@RequiredArgsConstructor
public class ServicesController {

    private final ServicesManager servicesManager;

    @PostMapping(value = "/oAuthService")
    public Y9Result<String> oAuthService(String clientId, String clientSecret, String serviceId, String name,
        String description) {
        Y9Result<String> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("刷新失败");
        y9result.setSuccess(false);
        try {
            OAuthRegisteredService oAuthRegisteredService = new OAuthRegisteredService();
            oAuthRegisteredService.setClientId(clientId);
            oAuthRegisteredService.setClientSecret(clientSecret);
            oAuthRegisteredService.setServiceId(serviceId);
            oAuthRegisteredService.setName(name);
            oAuthRegisteredService.setDescription(description);
            oAuthRegisteredService.setTheme("y9-apereo");
            oAuthRegisteredService.setEvaluationOrder(100);
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
            regexRegisteredService.setTheme("y9-apereo");
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
