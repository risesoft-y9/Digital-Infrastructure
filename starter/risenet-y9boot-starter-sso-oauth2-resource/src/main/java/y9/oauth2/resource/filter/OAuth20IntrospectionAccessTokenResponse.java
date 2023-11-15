package y9.oauth2.resource.filter;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth20IntrospectionAccessTokenResponse implements Serializable {
	public record DPopConfirmation(String jkt) {
    }

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
    private DPopConfirmation dPopConfirmation;

}
