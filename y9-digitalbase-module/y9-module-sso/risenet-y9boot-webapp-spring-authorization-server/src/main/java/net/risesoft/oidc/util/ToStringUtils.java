package net.risesoft.oidc.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.Collection;
import java.util.Set;

public final class ToStringUtils {
    private ToStringUtils() {}

    public static String toString(OAuth2AuthorizationConsent consent) {
        if (consent == null) {
            return null;
        }
        String clientId = consent.getRegisteredClientId();
        String principalName = consent.getPrincipalName();
        Collection<String> scopes = consent.getScopes();
        Collection<String> authorities = consent.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        return """
            OAuth2AuthorizationConsent[registeredClientId=%s, principalName=%s, \
            scopes=%s, authorities=%s]"""
                .formatted(clientId, principalName, scopes, authorities);
    }

    public static String toString(OAuth2Authorization authorization) {
        if (authorization == null) {
            return null;
        }
        String id = authorization.getId();
        String principalName = authorization.getPrincipalName();
        Set<String> scopes = authorization.getAuthorizedScopes();
        Token<OAuth2AccessToken> accessTokenToken =
            authorization.getAccessToken();
        String accessTokenValues = null;
        if (accessTokenToken != null) {
            OAuth2AccessToken token = accessTokenToken.getToken();
            accessTokenValues = """
                OAuth2AccessToken[tokenType=%s, metadata=%s, \
                value=%s, issuedAt=%s, expiresAt=%s, scopes=%s, tokenType=%s]\
                """.formatted(token.getTokenType().getValue(),
                        accessTokenToken.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt(), token.getScopes(),
                        token.getTokenType().getValue());
        }
        Token<OAuth2RefreshToken> refreshTokenToken =
            authorization.getRefreshToken();
        String refreshTokenValues = null;
        if (refreshTokenToken != null) {
            OAuth2RefreshToken token = refreshTokenToken.getToken();
            refreshTokenValues = """
                OAuth2RefreshToken[metadata=%s, value=%s, \
                issuedAt=%s, expiresAt=%s]\
                """.formatted(refreshTokenToken.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt());
        }
        Token<OAuth2AuthorizationCode> authorizationCodeToken =
            authorization.getToken(OAuth2AuthorizationCode.class);
        String authorizationCodeValues = null;
        if (authorizationCodeToken != null) {
            OAuth2AuthorizationCode token =
                authorizationCodeToken.getToken();
            authorizationCodeValues = """
                OAuth2AuthorizationCode[metadata=%s, value=%s, \
                issuedAt=%s, expiresAt=%s]\
                """.formatted(authorizationCodeToken.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt());
        }
        Token<OidcIdToken> oidcIdTokenWrapper =
            authorization.getToken(OidcIdToken.class);
        String oidcIdTokenValues = null;
        if (oidcIdTokenWrapper != null) {
            OidcIdToken token = oidcIdTokenWrapper.getToken();
            oidcIdTokenValues = """
                OidcIdToken[metadata=%s, value=%s, issuedAt=%s, \
                expiresAt=%s, claims=%s]\
                """.formatted(oidcIdTokenWrapper.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt(), token.getClaims());
        }
        Token<OAuth2UserCode> userCodeWrapper =
            authorization.getToken(OAuth2UserCode.class);
         String userCodeValues = null;
         if (userCodeWrapper != null) {
            OAuth2UserCode token = userCodeWrapper.getToken();
            userCodeValues = """
                OAuth2UserCode[metadata=%s, value=%s, \
                issuedAt=%s, expiresAt=%s]\
                """.formatted(userCodeWrapper.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt());
         }
        Token<OAuth2DeviceCode> deviceCodeWrapper =
            authorization.getToken(OAuth2DeviceCode.class);
        String deviceCodeValues = null;
        if (deviceCodeWrapper != null) {
            OAuth2DeviceCode token = deviceCodeWrapper.getToken();
            deviceCodeValues = """
                OAuth2DeviceCode[metadata=%s, value=%s, \
                issuedAt=%s, expiresAt=%s]\
                """.formatted(deviceCodeWrapper.getMetadata(),
                        token.getTokenValue(), token.getIssuedAt(),
                        token.getExpiresAt());
        }
        return """
            OAuth2Authorization[id=%s, registeredClientId=%s, \
            principalName=%s, authorizedScopes=%s, authorizationCode=%s, \
            accessToken=%s, refreshToken=%s, oidcIdToken=%s, userCode=%s, \
            deviceCode=%s, attributes=%s]\
            """.formatted(id, authorization.getRegisteredClientId(),
                    principalName, scopes, authorizationCodeValues,
                    accessTokenValues, refreshTokenValues,
                    oidcIdTokenValues, userCodeValues, deviceCodeValues,
                    authorization.getAttributes());
    }
}
