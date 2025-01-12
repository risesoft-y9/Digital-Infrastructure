package y9.authen;

import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.ServicesManager;
import y9.entity.Y9User;
import org.apereo.cas.web.support.WebUtils;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;
import y9.util.Y9Context;
import y9.util.Y9MessageDigest;
import y9.util.common.AESUtil;
import y9.util.common.Base64Util;
import y9.util.common.RSAUtil;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Y9AuthenticationHandler extends AbstractAuthenticationHandler {
    private final Y9UserService y9UserService;
    private final Y9LoginUserService y9LoginUserService;

    public Y9AuthenticationHandler(String name, ServicesManager servicesManager,
                                   Integer order, Y9UserService y9UserService, Y9LoginUserService y9LoginUserService) {
        super(name, servicesManager, PrincipalFactoryUtils.newPrincipalFactory(), order);
        this.y9UserService = y9UserService;
        this.y9LoginUserService = y9LoginUserService;
    }

    private static void fillCredential(RememberMeUsernamePasswordCredential riseCredential, String username,
                                       String password, String tenantShortName) {
        riseCredential.setUsername(username);
        riseCredential.assignPassword(password);
        if (StringUtils.isNotBlank(tenantShortName)) {
            riseCredential.setTenantShortName(tenantShortName);
        }

        HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext();
        if (null != request) {
            String systemName = request.getParameter("systemName");
            if (StringUtils.isNotBlank(systemName)) {
                riseCredential.setSystemName(systemName);
            }
            riseCredential.setUserAgent(request.getHeader("User-Agent"));
            riseCredential.setClientIp(Y9Context.getIpAddr(request));
        }
    }

    @Override
    public AuthenticationHandlerExecutionResult authenticate(Credential credential, Service service) throws Throwable {
        RememberMeUsernamePasswordCredential riseCredential = (RememberMeUsernamePasswordCredential) credential;
        String loginType = riseCredential.getLoginType();
        String tenantShortName = riseCredential.getTenantShortName();
        String deptId = riseCredential.getDeptId();
        String base64Username = riseCredential.getUsername();
        String encryptedBase64Password = riseCredential.toPassword();
        String base64Password;
        String pwdEcodeType = riseCredential.getPwdEcodeType();

        String plainUsername = "";
        String plainPassword;

        Y9User y9User = null;
        try {
            if (StringUtils.isNotBlank(pwdEcodeType)) {
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

            if (plainUsername.contains("&")) {
                String agentUserName = plainUsername.substring(plainUsername.indexOf("&") + 1);
                String agentTenantShortName = "operation";

                plainUsername = plainUsername.substring(0, plainUsername.indexOf("&"));

                fillCredential(riseCredential, plainUsername, plainPassword, null);

                List<Y9User> agentUsers = getAgentUsers(deptId, agentTenantShortName, agentUserName);
                if (agentUsers == null || agentUsers.isEmpty()) {
                    throw new AccountNotFoundException("没有找到这个代理用户。");
                } else {
                    y9User = agentUsers.getFirst();
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        throw new FailedLoginException("代理用户密码错误。");
                    }
                }

            } else {

                fillCredential(riseCredential, plainUsername, plainPassword, null);

                List<Y9User> users = getUsers(loginType, deptId, tenantShortName, plainUsername);
                if (users == null || users.isEmpty()) {
                    throw new AccountNotFoundException("没有找到这个用户。");
                } else if ("qrCode".equals(loginType)) {
                    y9User = users.getFirst();
                    fillCredential(riseCredential, y9User.getLoginName(), y9User.getPassword(),
                            y9User.getTenantShortName());
                } else {
                    y9User = users.getFirst();
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        throw new FailedLoginException("用户密码错误。");
                    }
                }
            }

            y9LoginUserService.save(riseCredential, "true", "登录成功");

            val attributes = buildAttributes(riseCredential, y9User);
            val principal = this.principalFactory.createPrincipal(plainUsername, attributes);
            return new DefaultAuthenticationHandlerExecutionResult(this, riseCredential, principal);
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

        if ("qrCode".equals(loginType)) {
            String userId = AESUtil.decrypt(username);
            if (StringUtils.isNotBlank(userId)) {
                return y9UserService.findByPersonIdAndOriginal(userId, Boolean.TRUE);
            } else {
                return List.of();
            }
        }

        // users = y9UserDao.findByUserIdAndOriginal(userId, 1);

        if (StringUtils.isNotBlank(deptId)) {
            // 一人多账号
            return y9UserService.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
        } else {
            return y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username, Boolean.TRUE);
        }
    }

    protected Map<String, List<Object>> buildAttributes(RememberMeUsernamePasswordCredential credential, Y9User y9User) {
        String tenantShortName = credential.getTenantShortName();
        String deptId = credential.getDeptId();
        String positionId = credential.getPositionId();
        String username = credential.getUsername();
        String loginType = credential.getLoginType();

        val attributes = new HashMap<String, List<Object>>();
        attributes.put("tenantId", Lists.newArrayList(y9User.getTenantId()));
        attributes.put("tenantShortName", Lists.newArrayList(tenantShortName));
        attributes.put("tenantName", Lists.newArrayList(y9User.getTenantName()));
        attributes.put("personId", Lists.newArrayList(y9User.getPersonId()));
        attributes.put("loginName", Lists.newArrayList(username));
        attributes.put("sex", Lists.newArrayList(y9User.getSex() == null ? 0 : y9User.getSex()));
        attributes.put("caid", Lists.newArrayList(y9User.getCaid() == null ? "" : y9User.getCaid()));
        attributes.put("email", Lists.newArrayList(y9User.getEmail() == null ? "" : y9User.getEmail()));
        attributes.put("mobile", Lists.newArrayList(y9User.getMobile() == null ? "" : y9User.getMobile()));
        attributes.put("guidPath", Lists.newArrayList(y9User.getGuidPath() == null ? "" : y9User.getGuidPath()));
        attributes.put("dn", Lists.newArrayList(y9User.getDn() == null ? "" : y9User.getDn()));
        attributes.put("loginType", Lists.newArrayList(loginType));

        attributes.put("name", Lists.newArrayList(y9User.getName()));
        attributes.put("parentId", Lists.newArrayList(y9User.getParentId() == null ? "" : y9User.getParentId()));
        attributes.put("idNum", Lists.newArrayList(y9User.getIdNum() == null ? "" : y9User.getIdNum()));
        attributes.put("avator", Lists.newArrayList(y9User.getAvator() == null ? "" : y9User.getAvator()));
        attributes.put("personType", Lists.newArrayList(y9User.getPersonType() == null ? "" : y9User.getPersonType()));

        attributes.put("password", Lists.newArrayList(y9User.getPassword() == null ? "" : y9User.getPassword()));
        attributes.put("original", Lists.newArrayList(y9User.getOriginal() == null ? 1 : y9User.getOriginal()));
        attributes.put("originalId", Lists.newArrayList(y9User.getOriginalId() == null ? "" : y9User.getOriginalId()));
        attributes.put("globalManager",
                Lists.newArrayList(y9User.getGlobalManager() != null && y9User.getGlobalManager()));
        attributes.put("managerLevel",
                Lists.newArrayList(y9User.getManagerLevel() == null ? 0 : y9User.getManagerLevel()));
        attributes.put("roles", Lists.newArrayList(y9User.getRoles() == null ? "" : y9User.getRoles()));
        attributes.put("positions", Lists.newArrayList(y9User.getPositions() == null ? "" : y9User.getPositions()));

        if (StringUtils.isNotBlank(positionId)) {
            attributes.put("positionId", Lists.newArrayList(positionId));
        }
        return attributes;
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
