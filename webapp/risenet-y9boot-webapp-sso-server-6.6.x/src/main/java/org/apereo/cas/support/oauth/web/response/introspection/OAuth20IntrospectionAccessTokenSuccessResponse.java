package org.apereo.cas.support.oauth.web.response.introspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This is {@link OAuth20IntrospectionAccessTokenSuccessResponse}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Getter
@Setter
public class OAuth20IntrospectionAccessTokenSuccessResponse extends BaseOAuth20IntrospectionAccessTokenResponse {
    private static final long serialVersionUID = -7917281748569741345L;

    private String token;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private boolean active;

    private String sub;

    private String scope;

    private long iat;

    private long exp;

    private String realmName;

    private String uniqueSecurityName;

    private String tokenType;

    private String aud;

    private String iss;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("grant_type")
    private String grantType;

    private String attr; // y9 add

    @JsonProperty("cnf")
    private DPopConfirmation dPopConfirmation;

    @Getter
    @RequiredArgsConstructor
    public static class DPopConfirmation {
        private final String jkt;
    }
}
