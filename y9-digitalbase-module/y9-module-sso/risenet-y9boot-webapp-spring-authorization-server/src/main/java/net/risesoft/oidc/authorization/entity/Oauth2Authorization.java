package net.risesoft.oidc.authorization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;

// Copy this class from How-to: Implement core services with JPA documentation: https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html

@Entity
@Table(name = "jpa_oauth2_authorization")
public class Oauth2Authorization {

    @Id
    @Column(name = "id", length = 100, nullable = false)
    private String id;

    @Column(name = "registered_client_id", length = 100, nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;

    @Column(name = "authorization_grant_type", length = 100, nullable = false)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String authorizedScopes;

    @Column(name = "attributes", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String attributes;

    @Column(name = "state", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String state;

    @Column(name = "authorization_code_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    @Column(name = "authorization_code_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String authorizationCodeMetadata;

    @Column(name = "access_token_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String accessTokenMetadata;

    @Column(name = "access_token_type", length = 100)
    private String accessTokenType;

    @Column(name = "access_token_scopes", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String accessTokenScopes;

    @Column(name = "oidc_id_token_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String oidcIdTokenValue;

    @Column(name = "oidc_id_token_issued_at")
    private Instant oidcIdTokenIssuedAt;

    @Column(name = "oidc_id_token_expires_at")
    private Instant oidcIdTokenExpiresAt;

    @Column(name = "oidc_id_token_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String oidcIdTokenMetadata;

    @Column(name = "oidc_id_token_claims", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String oidcIdTokenClaims;

    @Column(name = "refresh_token_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;

    @Column(name = "refresh_token_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String refreshTokenMetadata;

    @Column(name = "user_code_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String userCodeValue;

    @Column(name = "user_code_issued_at")
    private Instant userCodeIssuedAt;

    @Column(name = "user_code_expires_at")
    private Instant userCodeExpiresAt;

    @Column(name = "user_code_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String userCodeMetadata;

    @Column(name = "device_code_value", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String deviceCodeValue;

    @Column(name = "device_code_issued_at")
    private Instant deviceCodeIssuedAt;

    @Column(name = "device_code_expires_at")
    private Instant deviceCodeExpiresAt;

    @Column(name = "device_code_metadata", columnDefinition = "text")
    @Lob
    //@JdbcTypeCode(Types.LONGVARCHAR)
    private String deviceCodeMetadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public String getAuthorizedScopes() {
        return this.authorizedScopes;
    }

    public void setAuthorizedScopes(String authorizedScopes) {
        this.authorizedScopes = authorizedScopes;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthorizationCodeValue() {
        return authorizationCodeValue;
    }

    public void setAuthorizationCodeValue(String authorizationCode) {
        this.authorizationCodeValue = authorizationCode;
    }

    public Instant getAuthorizationCodeIssuedAt() {
        return authorizationCodeIssuedAt;
    }

    public void setAuthorizationCodeIssuedAt(Instant authorizationCodeIssuedAt) {
        this.authorizationCodeIssuedAt = authorizationCodeIssuedAt;
    }

    public Instant getAuthorizationCodeExpiresAt() {
        return authorizationCodeExpiresAt;
    }

    public void setAuthorizationCodeExpiresAt(Instant authorizationCodeExpiresAt) {
        this.authorizationCodeExpiresAt = authorizationCodeExpiresAt;
    }

    public String getAuthorizationCodeMetadata() {
        return authorizationCodeMetadata;
    }

    public void setAuthorizationCodeMetadata(String authorizationCodeMetadata) {
        this.authorizationCodeMetadata = authorizationCodeMetadata;
    }

    public String getAccessTokenValue() {
        return accessTokenValue;
    }

    public void setAccessTokenValue(String accessToken) {
        this.accessTokenValue = accessToken;
    }

    public Instant getAccessTokenIssuedAt() {
        return accessTokenIssuedAt;
    }

    public void setAccessTokenIssuedAt(Instant accessTokenIssuedAt) {
        this.accessTokenIssuedAt = accessTokenIssuedAt;
    }

    public Instant getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(Instant accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public String getAccessTokenMetadata() {
        return accessTokenMetadata;
    }

    public void setAccessTokenMetadata(String accessTokenMetadata) {
        this.accessTokenMetadata = accessTokenMetadata;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public String getAccessTokenScopes() {
        return accessTokenScopes;
    }

    public void setAccessTokenScopes(String accessTokenScopes) {
        this.accessTokenScopes = accessTokenScopes;
    }

    public String getRefreshTokenValue() {
        return refreshTokenValue;
    }

    public void setRefreshTokenValue(String refreshToken) {
        this.refreshTokenValue = refreshToken;
    }

    public Instant getRefreshTokenIssuedAt() {
        return refreshTokenIssuedAt;
    }

    public void setRefreshTokenIssuedAt(Instant refreshTokenIssuedAt) {
        this.refreshTokenIssuedAt = refreshTokenIssuedAt;
    }

    public Instant getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(Instant refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getRefreshTokenMetadata() {
        return refreshTokenMetadata;
    }

    public void setRefreshTokenMetadata(String refreshTokenMetadata) {
        this.refreshTokenMetadata = refreshTokenMetadata;
    }

    public String getOidcIdTokenValue() {
        return oidcIdTokenValue;
    }

    public void setOidcIdTokenValue(String idToken) {
        this.oidcIdTokenValue = idToken;
    }

    public Instant getOidcIdTokenIssuedAt() {
        return oidcIdTokenIssuedAt;
    }

    public void setOidcIdTokenIssuedAt(Instant idTokenIssuedAt) {
        this.oidcIdTokenIssuedAt = idTokenIssuedAt;
    }

    public Instant getOidcIdTokenExpiresAt() {
        return oidcIdTokenExpiresAt;
    }

    public void setOidcIdTokenExpiresAt(Instant idTokenExpiresAt) {
        this.oidcIdTokenExpiresAt = idTokenExpiresAt;
    }

    public String getOidcIdTokenMetadata() {
        return oidcIdTokenMetadata;
    }

    public void setOidcIdTokenMetadata(String idTokenMetadata) {
        this.oidcIdTokenMetadata = idTokenMetadata;
    }

    public String getOidcIdTokenClaims() {
        return oidcIdTokenClaims;
    }

    public void setOidcIdTokenClaims(String idTokenClaims) {
        this.oidcIdTokenClaims = idTokenClaims;
    }

    public String getUserCodeValue() {
        return this.userCodeValue;
    }

    public void setUserCodeValue(String userCodeValue) {
        this.userCodeValue = userCodeValue;
    }

    public Instant getUserCodeIssuedAt() {
        return this.userCodeIssuedAt;
    }

    public void setUserCodeIssuedAt(Instant userCodeIssuedAt) {
        this.userCodeIssuedAt = userCodeIssuedAt;
    }

    public Instant getUserCodeExpiresAt() {
        return this.userCodeExpiresAt;
    }

    public void setUserCodeExpiresAt(Instant userCodeExpiresAt) {
        this.userCodeExpiresAt = userCodeExpiresAt;
    }

    public String getUserCodeMetadata() {
        return this.userCodeMetadata;
    }

    public void setUserCodeMetadata(String userCodeMetadata) {
        this.userCodeMetadata = userCodeMetadata;
    }

    public String getDeviceCodeValue() {
        return this.deviceCodeValue;
    }

    public void setDeviceCodeValue(String deviceCodeValue) {
        this.deviceCodeValue = deviceCodeValue;
    }

    public Instant getDeviceCodeIssuedAt() {
        return this.deviceCodeIssuedAt;
    }

    public void setDeviceCodeIssuedAt(Instant deviceCodeIssuedAt) {
        this.deviceCodeIssuedAt = deviceCodeIssuedAt;
    }

    public Instant getDeviceCodeExpiresAt() {
        return this.deviceCodeExpiresAt;
    }

    public void setDeviceCodeExpiresAt(Instant deviceCodeExpiresAt) {
        this.deviceCodeExpiresAt = deviceCodeExpiresAt;
    }

    public String getDeviceCodeMetadata() {
        return this.deviceCodeMetadata;
    }

    public void setDeviceCodeMetadata(String deviceCodeMetadata) {
        this.deviceCodeMetadata = deviceCodeMetadata;
    }
}
