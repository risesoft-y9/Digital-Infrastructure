package org.apereo.cas.authentication;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.web.support.WebUtils;

import lombok.extern.slf4j.Slf4j;

import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;
import y9.util.Y9Context;
import y9.util.Y9MessageDigest;
import y9.util.common.Base64Util;
import y9.util.common.RSAUtil;

@Slf4j
public class RiseAuthenticationHandler extends AbstractAuthenticationHandler {
    private final Y9UserService y9UserService;
    private final Y9LoginUserService y9LoginUserService;

    public RiseAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
        Integer order, Y9UserService y9UserService, Y9LoginUserService y9LoginUserService) {
        super(name, servicesManager, principalFactory, order);
        this.y9UserService = y9UserService;
        this.y9LoginUserService = y9LoginUserService;
    }

    @Override
    public AuthenticationHandlerExecutionResult authenticate(Credential credential, Service service)
        throws PreventedException, Throwable {
        RememberMeUsernamePasswordCredential riseCredential = (RememberMeUsernamePasswordCredential)credential;
        String loginType = riseCredential.getLoginType();
        String tenantShortName = riseCredential.getTenantShortName();
        String deptId = riseCredential.getDeptId();
        String base64Username = riseCredential.getUsername();
        String encryptedBase64Password = riseCredential.toPassword();
        String base64Password;
        String pwdEcodeType = riseCredential.getPwdEcodeType();

        String plainUsername;
        String plainPassword;
        try {
            if (StringUtils.isNotBlank(pwdEcodeType)) {
                // Object obj = redisTemplate.opsForValue().get(pwdEcodeType);
                String rsaPrivateKey = Y9Context.getProperty("y9.login.encryptionRsaPrivateKey");
                if (null != rsaPrivateKey) {
                    base64Password = RSAUtil.privateDecrypt(encryptedBase64Password, rsaPrivateKey);
                } else {
                    throw new FailedLoginException("认证失败：解密私钥未配置!");
                }
            } else {
                base64Password = encryptedBase64Password;
            }
            plainPassword = Base64Util.decode(base64Password, "Unicode");
            plainUsername = Base64Util.decode(base64Username, "Unicode");

            riseCredential.setUsername(plainUsername);
            riseCredential.assignPassword(plainPassword);

            HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext();
            if (null != request) {
                String systemName = request.getParameter("systemName");
                if (StringUtils.isNotBlank(systemName)) {
                    riseCredential.setSystemName(systemName);
                }
                riseCredential.setUserAgent(request.getHeader("User-Agent"));
                riseCredential.setClientIp(Y9Context.getIpAddr(request));
            }

            if (plainUsername.contains("&")) {
                String agentUserName = plainUsername.substring(plainUsername.indexOf("&") + 1);
                String agentTenantShortName = "operation";

                plainUsername = plainUsername.substring(0, plainUsername.indexOf("&"));
                riseCredential.setUsername(plainUsername);

                List<Y9User> agentUsers = getAgentUsers(deptId, agentTenantShortName, agentUserName);
                if (agentUsers == null || agentUsers.isEmpty()) {
                    throw new AccountNotFoundException("没有找到这个代理用户。");
                } else {
                    Y9User y9User = agentUsers.get(0);
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        throw new FailedLoginException("代理用户密码错误。");
                    }
                }

            } else {
                List<Y9User> users = getUsers(loginType, deptId, tenantShortName, plainUsername);
                if (users == null || users.isEmpty()) {
                    throw new AccountNotFoundException("没有找到这个用户。");
                } else {
                    Y9User y9User = users.get(0);
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        throw new FailedLoginException("用户密码错误。");
                    }
                }
            }

            y9LoginUserService.save(riseCredential, "true", "登录成功");
            return new DefaultAuthenticationHandlerExecutionResult(this, riseCredential,
                principalFactory.createPrincipal(plainUsername));

        } catch (GeneralSecurityException e) {
            y9LoginUserService.save(riseCredential, "false", "登录失败");
            throw e;
        } catch (Exception e) {
            y9LoginUserService.save(riseCredential, "false", "登录失败");
            throw new FailedLoginException(e.getMessage());
        }

    }

    private List<Y9User> getAgentUsers(String deptId, String agentTenantShortName, String agentUserName) {
        if (StringUtils.isNotBlank(deptId)) {
            return y9UserService.findByTenantShortNameAndMobileAndParentId(agentTenantShortName, agentUserName, deptId);
        } else {
            return y9UserService.findByTenantShortNameAndLoginNameAndOriginal(agentTenantShortName, agentUserName,
                Boolean.TRUE);
        }
    }

    private List<Y9User> getUsers(String loginType, String deptId, String tenantShortName, String username) {
        if ("mobile".equals(loginType)) {
            if (StringUtils.isNotBlank(deptId)) {
                return y9UserService.findByTenantShortNameAndMobileAndParentId(tenantShortName, username, deptId);
            } else {
                return y9UserService.findByTenantShortNameAndMobileAndOriginal(tenantShortName, username, Boolean.TRUE);
            }
        }

        if ("loginMobileName".equals(loginType)) {
            if (StringUtils.isNotBlank(deptId)) {
                return y9UserService.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
            } else {
                return y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username,
                    Boolean.TRUE);
            }
        }

        if (StringUtils.isNotBlank(deptId)) {
            // 一人多账号
            return y9UserService.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
        } else {
            return y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username, Boolean.TRUE);
        }
    }

    @Override
    public boolean supports(Class<? extends Credential> clazz) {
        return RememberMeUsernamePasswordCredential.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof RememberMeUsernamePasswordCredential;
    }

}
