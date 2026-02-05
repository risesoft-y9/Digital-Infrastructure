package y9.oauth2.client.filter;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TokenResponse implements Serializable {
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

    //private String attr; // y9 add

    @JsonProperty("cnf")
    private Confirmation confirmation = new Confirmation();

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Confirmation implements Serializable {
        @Serial
        private static final long serialVersionUID = 5434898952283549630L;

        private String jkt;

        @JsonProperty("x5t#S256")
        private String x5t;
    }
    
    public boolean isActive() {
    	    return this.active;
    }

}
