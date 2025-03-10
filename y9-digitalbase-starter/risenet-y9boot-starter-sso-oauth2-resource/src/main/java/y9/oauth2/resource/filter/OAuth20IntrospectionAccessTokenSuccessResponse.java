package y9.oauth2.resource.filter;

import java.io.Serializable;

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
public class OAuth20IntrospectionAccessTokenSuccessResponse implements Serializable {

    private static final long serialVersionUID = 9173277430150840002L;

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
