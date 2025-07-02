package y9.authen;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.ServicesManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9User;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;
import y9.util.Y9Context;
import y9.util.Y9MessageDigest;
import y9.util.common.AESUtil;
import y9.util.common.RSAUtil;

@Slf4j
public class Y9AuthenticationHandler extends AbstractAuthenticationHandler {
    private final Y9UserService y9UserService;
    private final Y9LoginUserService y9LoginUserService;

    public Y9AuthenticationHandler(String name, ServicesManager servicesManager, Integer order,
        Y9UserService y9UserService, Y9LoginUserService y9LoginUserService) {
        super(name, servicesManager, PrincipalFactoryUtils.newPrincipalFactory(), order);
        this.y9UserService = y9UserService;
        this.y9LoginUserService = y9LoginUserService;
    }

    private static void updateCredential(HttpServletRequest request, UsernamePasswordCredential riseCredential,
        String username, String password, String tenantShortName) {
        riseCredential.setUsername(username);
        riseCredential.assignPassword(password);
        Map<String, Object> customFields = riseCredential.getCustomFields();
        if (StringUtils.isNotBlank(tenantShortName)) {
            customFields.put("tenantShortName", tenantShortName);
        }
        String systemName = request.getParameter("systemName");
        if (StringUtils.isNotBlank(systemName)) {
            customFields.put("systemName", systemName);
        }
        String loginType = request.getParameter("loginType");
        if (StringUtils.isNotBlank(loginType)) {
            customFields.put("loginType", loginType);
        }
        String deptId = request.getParameter("deptId");
        if (StringUtils.isNotBlank(deptId)) {
            customFields.put("deptId", deptId);
        }
        String rsaPublicKey = request.getParameter("rsaPublicKey");
        if (StringUtils.isNotBlank(rsaPublicKey)) {
            customFields.put("rsaPublicKey", rsaPublicKey);
        }
        String positionId = request.getParameter("positionId");
        if (StringUtils.isNotBlank(positionId)) {
            customFields.put("positionId", positionId);
        }
        String screenDimension = request.getParameter("screenDimension");
        if (StringUtils.isNotBlank(screenDimension)) {
            customFields.put("screenDimension", screenDimension);
        }
        customFields.put("userAgent", request.getHeader("User-Agent"));
        customFields.put("userHostIP", Y9Context.getIpAddr(request));

    }

    @Override
    public AuthenticationHandlerExecutionResult authenticate(Credential credential, Service service)
        throws GeneralSecurityException, PreventedException {
        UsernamePasswordCredential riseCredential = (UsernamePasswordCredential)credential;
        // HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext();
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String loginType = request.getParameter("loginType");
        String tenantShortName = request.getParameter("tenantShortName");
        String positionId = request.getParameter("positionId");
        String deptId = request.getParameter("deptId");
        String systemName = request.getParameter("systemName");
        String screenDimension = request.getParameter("screenDimension");
        // String rsaPublicKey = request.getParameter("rsaPublicKey");

        Map<String, Object> customFields = riseCredential.getCustomFields();
        customFields.put("tenantShortName", tenantShortName);
        customFields.put("noLoginScreen", true);
        customFields.put("deptId", deptId);
        customFields.put("positionId", positionId);
        customFields.put("loginType", loginType);
        customFields.put("screenDimension", screenDimension);
        customFields.put("systemName", systemName);
        riseCredential.setCustomFields(customFields);

        String encryptedUsername = riseCredential.getUsername();
        String encryptedPassword = riseCredential.toPassword();
        Y9User y9User;
        String loginMsg = "登录成功";
        try {
            String rsaPrivateKey = Y9Context.getProperty("y9.rsaPrivateKey");
            String plainUsername = RSAUtil.privateDecrypt(encryptedUsername, rsaPrivateKey);
            String plainPassword = RSAUtil.privateDecrypt(encryptedPassword, rsaPrivateKey);
            if (plainUsername.contains("&")) {
                String agentUserName = plainUsername.substring(plainUsername.indexOf("&") + 1);
                String agentTenantShortName = "operation";

                plainUsername = plainUsername.substring(0, plainUsername.indexOf("&"));

                updateCredential(request, riseCredential, plainUsername, plainPassword, tenantShortName);

                List<Y9User> agentUsers = getAgentUsers(deptId, agentTenantShortName, agentUserName);
                if (agentUsers == null || agentUsers.isEmpty()) {
                    loginMsg = "没有找到这个代理用户。";
                    throw new AccountNotFoundException("没有找到这个代理用户。");
                } else {
                    y9User = agentUsers.get(0);
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        loginMsg = "代理用户密码错误。";
                        throw new FailedLoginException("代理用户密码错误。");
                    }
                }

            } else {
                updateCredential(request, riseCredential, plainUsername, plainPassword, tenantShortName);

                List<Y9User> users = getUsers(loginType, deptId, tenantShortName, plainUsername);
                if (users == null || users.isEmpty()) {
                    loginMsg = "没有找到这个用户。";
                    throw new AccountNotFoundException("没有找到这个用户。");
                } else if ("qrCode".equals(loginType)) {
                    y9User = users.get(0);
                    updateCredential(request, riseCredential, y9User.getLoginName(), y9User.getPassword(),
                        y9User.getTenantShortName());
                } else {
                    y9User = users.get(0);
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.bcryptMatch(plainPassword, hashed)) {
                        loginMsg = "用户密码错误。";
                        throw new FailedLoginException("用户密码错误。");
                    }
                }
            }

            y9LoginUserService.save(riseCredential, "true", loginMsg);

            val attributes = buildAttributes(riseCredential, y9User);
            val principal = this.principalFactory.createPrincipal(plainUsername, attributes);
            // val principal = this.principalFactory.createPrincipal(plainUsername);
            return new DefaultAuthenticationHandlerExecutionResult(this, riseCredential, principal);
        } catch (GeneralSecurityException e) {
            y9LoginUserService.save(riseCredential, "false", loginMsg);
            throw e;
        } catch (Exception e) {
            y9LoginUserService.save(riseCredential, "false", "登录失败,错误：" + e.getMessage());
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

        if (StringUtils.isNotBlank(deptId)) {
            // 一人多账号
            return y9UserService.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
        } else {
            return y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username, Boolean.TRUE);
        }
    }

    protected Map<String, List<Object>> buildAttributes(UsernamePasswordCredential riseCredential, Y9User y9User) {
        String username = riseCredential.getUsername();
        Map<String, Object> customFields = riseCredential.getCustomFields();
        String tenantShortName = (String)customFields.get("tenantShortName");
        String deptId = (String)customFields.get("deptId");
        String positionId = (String)customFields.get("positionId");
        String loginType = (String)customFields.get("loginType");

        val attributes = new HashMap<String, List<Object>>();
        attributes.put("tenantId", toArrayList(y9User.getTenantId()));
        attributes.put("tenantShortName", toArrayList(tenantShortName));
        attributes.put("tenantName", toArrayList(y9User.getTenantName()));
        attributes.put("personId", toArrayList(y9User.getPersonId()));
        attributes.put("loginName", toArrayList(username));
        attributes.put("sex", toArrayList(y9User.getSex()));
        attributes.put("caid", toArrayList(y9User.getCaid()));
        attributes.put("email", toArrayList(y9User.getEmail()));
        attributes.put("mobile", toArrayList(y9User.getMobile()));
        attributes.put("guidPath", toArrayList(y9User.getGuidPath()));
        attributes.put("dn", toArrayList(y9User.getDn()));
        attributes.put("loginType", toArrayList(loginType));

        attributes.put("name", toArrayList(y9User.getName()));
        attributes.put("parentId", toArrayList(y9User.getParentId()));
        attributes.put("idNum", toArrayList(y9User.getIdNum()));
        attributes.put("avator", toArrayList(y9User.getAvator()));
        attributes.put("personType", toArrayList(y9User.getPersonType()));

        attributes.put("password", toArrayList(y9User.getPassword()));
        attributes.put("original", Lists.newArrayList(y9User.getOriginal() == null || y9User.getOriginal()));
        attributes.put("originalId", toArrayList(y9User.getOriginalId()));
        attributes.put("globalManager",
            Lists.newArrayList(y9User.getGlobalManager() != null && y9User.getGlobalManager()));
        attributes.put("managerLevel", toArrayList(y9User.getManagerLevel()));
        attributes.put("positions", toArrayList(y9User.getPositions()));

        if (!org.springframework.util.StringUtils.hasText(positionId)) {
            if (!org.springframework.util.StringUtils.hasText(y9User.getPositions())) {
                positionId = "";
            } else {
                String positionsStr = y9User.getPositions();
                positionId = positionsStr.split(",")[0];
            }
        }
        attributes.put("positionId", toArrayList(positionId));
        customFields.put("positionId", positionId);

        if (!org.springframework.util.StringUtils.hasText(deptId)) {
            deptId = "";
        }
        attributes.put("deptId", toArrayList(deptId));
        customFields.put("deptId", deptId);

        riseCredential.setCustomFields(customFields);
        return attributes;
    }

    private List<Object> toArrayList(String str) {
        return Lists.newArrayList(StringUtils.isEmpty(str) ? "" : str);
    }

    private List<Object> toArrayList(Integer num) {
        return Lists.newArrayList(num == null ? 0 : num);
    }

    @Override
    public boolean supports(Class<? extends Credential> clazz) {
        boolean t = clazz.isAssignableFrom(UsernamePasswordCredential.class);
        return t;
    }

    @Override
    public boolean supports(final Credential credential) {
        boolean t = credential instanceof UsernamePasswordCredential;
        return t;
    }

}
