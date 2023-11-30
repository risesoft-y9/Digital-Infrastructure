package org.apereo.cas.support.oauth.web.response.introspection.success;

import org.apereo.cas.support.oauth.web.response.introspection.BaseOAuth20IntrospectionAccessTokenResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * This is {@link OAuth20IntrospectionAccessTokenSuccessResponse}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Getter
@Setter
public class OAuth20IntrospectionAccessTokenSuccessResponse2 extends BaseOAuth20IntrospectionAccessTokenResponse {
    @Serial
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
    private Confirmation confirmation = new Confirmation();

    @Data
    public static class Confirmation implements Serializable {
        @Serial
        private static final long serialVersionUID = 5434898952283549630L;

        private String jkt;

        @JsonProperty("x5t#S256")
        private String x5t;
    }
}
