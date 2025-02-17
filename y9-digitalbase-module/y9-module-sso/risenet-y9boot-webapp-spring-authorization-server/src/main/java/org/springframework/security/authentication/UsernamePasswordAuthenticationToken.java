package org.springframework.security.authentication;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import net.risesoft.oidc.util.Y9Context;
import net.risesoft.oidc.util.common.Base64Util;
import net.risesoft.oidc.util.common.RSAUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * An {@link org.springframework.security.core.Authentication} implementation that is
 * designed for simple presentation of a username and password.
 * <p>
 * The <code>principal</code> and <code>credentials</code> should be set with an
 * <code>Object</code> that provides the respective property via its
 * <code>Object.toString()</code> method. The simplest such <code>Object</code> to use is
 * <code>String</code>.
 *
 * @author Ben Alex
 * @author Norbert Nowak
 */
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Object principal;

    private Object credentials;

    private Map<String, Object> customFields = new LinkedHashMap<>();

    @SneakyThrows
    private void updateCustomFields(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String loginType = request.getParameter("loginType");
        String tenantShortName = request.getParameter("tenantShortName");
        String deptId = request.getParameter("deptId");
        String pwdEcodeType = request.getParameter("pwdEcodeType");

        Map<String, Object> customFields = new LinkedHashMap<>();
        customFields.put("tenantShortName", tenantShortName);
        customFields.put("noLoginScreen", true);
        customFields.put("deptId", deptId);
        customFields.put("loginType", loginType);
        customFields.put("userAgent", request.getHeader("User-Agent"));
        customFields.put("clientIp", Y9Context.getIpAddr(request));

        String base64Username = (String)this.principal;
        String encryptedBase64Password = (String)this.credentials;
        String base64Password = encryptedBase64Password;
        String plainUsername;
        String plainPassword;

        if (StringUtils.isNotBlank(pwdEcodeType)) {
            String rsaPrivateKey = Y9Context.getProperty("y9.encryptionRsaPrivateKey");
            if (null != rsaPrivateKey) {
                base64Password = RSAUtil.privateDecrypt(encryptedBase64Password, rsaPrivateKey);
            }
        }
        plainPassword = Base64Util.decode(base64Password, "Unicode");
        plainUsername = Base64Util.decode(base64Username, "Unicode");

        this.customFields = customFields;
        this.principal =  tenantShortName + "TTT" + plainUsername;
        this.credentials = plainPassword;
    }

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     */
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);

        updateCustomFields();
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     * @param principal
     * @param credentials
     * @param authorities
     */
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override

        //updateCustomFields();
    }

    /**
     * This factory method can be safely used by any code that wishes to create a
     * unauthenticated <code>UsernamePasswordAuthenticationToken</code>.
     * @param principal
     * @param credentials
     * @return UsernamePasswordAuthenticationToken with false isAuthenticated() result
     *
     * @since 5.7
     */
    public static UsernamePasswordAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new UsernamePasswordAuthenticationToken(principal, credentials);
    }

    /**
     * This factory method can be safely used by any code that wishes to create a
     * authenticated <code>UsernamePasswordAuthenticationToken</code>.
     * @param principal
     * @param credentials
     * @return UsernamePasswordAuthenticationToken with true isAuthenticated() result
     *
     * @since 5.7
     */
    public static UsernamePasswordAuthenticationToken authenticated(Object principal, Object credentials,
                                                                    Collection<? extends GrantedAuthority> authorities) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Map<String, Object> getCustomFields() {return this.customFields;}

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

}
