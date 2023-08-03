package y9.oauth2.resource.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth20IntrospectionAccessTokenResponse {

    private boolean active;

    private String attr;

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

    public OAuth20IntrospectionAccessTokenResponse() {}

    public String getAttr() {
        return attr;
    }

    public String getAud() {
        return aud;
    }

    public String getClientId() {
        return clientId;
    }

    public long getExp() {
        return exp;
    }

    public String getGrantType() {
        return grantType;
    }

    public long getIat() {
        return iat;
    }

    public String getIss() {
        return iss;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getScope() {
        return scope;
    }

    public String getSub() {
        return sub;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUniqueSecurityName() {
        return uniqueSecurityName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setUniqueSecurityName(String uniqueSecurityName) {
        this.uniqueSecurityName = uniqueSecurityName;
    }

    @Override
    public String toString() {
        return "OAuth20IntrospectionAccessTokenResponse [active=" + active + ", attr=" + attr + ", sub=" + sub
            + ", scope=" + scope + ", iat=" + iat + ", exp=" + exp + ", realmName=" + realmName
            + ", uniqueSecurityName=" + uniqueSecurityName + ", tokenType=" + tokenType + ", aud=" + aud + ", iss="
            + iss + ", clientId=" + clientId + ", grantType=" + grantType + "]";
    }

}
