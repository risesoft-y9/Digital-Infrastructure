package org.apereo.cas.authentication;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.metadata.BasicCredentialMetaData;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.cas.web.y9.service.SaveLoginInfoService;
import org.apereo.cas.web.y9.util.Y9Context;
import org.apereo.cas.web.y9.util.Y9MessageDigest;
import org.apereo.cas.web.y9.util.common.Base64Util;
import org.apereo.cas.web.y9.util.common.RSAUtil;
import org.apereo.cas.web.y9.y9user.Y9User;
import org.apereo.cas.web.y9.y9user.Y9UserDao;
import org.springframework.dao.DataAccessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RiseAuthenticationHandler extends AbstractAuthenticationHandler {

    private Y9UserDao y9UserDao;
    private SaveLoginInfoService saveLoginInfoService;
    private CasRedisTemplate<Object, Object> redisTemplate;

    public RiseAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
        Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    public AuthenticationHandlerExecutionResult authenticate(Credential credential, Service service)
        throws GeneralSecurityException, PreventedException {
        if (y9UserDao == null) {
            y9UserDao = Y9Context.getBean(Y9UserDao.class);
            saveLoginInfoService = Y9Context.getBean(SaveLoginInfoService.class);
            redisTemplate = Y9Context.getBean("y9RedisTemplate");
        }
        RememberMeUsernamePasswordCredential riseCredential = (RememberMeUsernamePasswordCredential)credential;
        String loginType = riseCredential.getLoginType();
        String tenantShortName = riseCredential.getTenantShortName();
        String deptId = riseCredential.getDeptId();
        String username = riseCredential.getUsername();
        String password = riseCredential.toPassword();
        String pwdEcodeType = riseCredential.getPwdEcodeType();

        String oldTenantname = tenantShortName;
        String oldUsername = username;

        try {
            username = Base64Util.decode(username, "Unicode");
            if (StringUtils.isNotBlank(pwdEcodeType)) {
                Object obj = redisTemplate.opsForValue().get(pwdEcodeType);
                if (null != obj) {
                    password = RSAUtil.privateDecrypt(password, String.valueOf(obj));
                } else {
                    throw new FailedLoginException("认证失败：随机数已过期，请重新登录!");
                }
            }
            password = Base64Util.decode(password, "Unicode");
            // 特殊登录处理
            if (username.contains("&")) {
                oldUsername = username;
                String agentTenantShortName = "operation";
                String agentUserName = username.substring(username.indexOf("&") + 1, username.length());
                username = agentUserName;
                tenantShortName = agentTenantShortName;

                List<Y9User> agentUsers = null;
                if (StringUtils.isNotBlank(deptId)) {
                    agentUsers = y9UserDao.findByTenantShortNameAndMobileAndParentId(agentTenantShortName,
                        agentUserName, deptId);
                } else {
                    agentUsers = y9UserDao.findByTenantShortNameAndLoginNameAndOriginal(agentTenantShortName,
                        agentUserName, Boolean.TRUE);
                }
                if (agentUsers == null || agentUsers.isEmpty()) {
                    throw new FailedLoginException("没有找到这个【代理】用户。");
                }
            }
            riseCredential.setUsername(username);
            riseCredential.assignPassword(password);

            HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext();
            if (null != request) {
                String systemName = request.getParameter("systemName");
                if (StringUtils.isNotBlank(systemName)) {
                    riseCredential.setSystemName(systemName);
                }
                riseCredential.setUserAgent(request.getHeader("User-Agent"));
                riseCredential.setClientIp(Y9Context.getIpAddr(request));
            }
        } catch (Exception e) {
            throw new FailedLoginException(e.getMessage());
        }

        try {
            if ("caid".equals(loginType)) {
                AuthenticationHandlerExecutionResult hr = new DefaultAuthenticationHandlerExecutionResult(this,
                    new BasicCredentialMetaData(riseCredential), principalFactory.createPrincipal(username));
                return hr;
            } else {
                List<Y9User> users = null;
                if ("mobile".equals(loginType)) {
                    if (StringUtils.isNotBlank(deptId)) {
                        users = y9UserDao.findByTenantShortNameAndMobileAndParentId(tenantShortName, username, deptId);
                    } else {
                        users = y9UserDao.findByTenantShortNameAndMobileAndOriginal(tenantShortName, username,
                            Boolean.TRUE);
                    }
                } else if ("loginMobileName".equals(loginType)) {
                    if (StringUtils.isNotBlank(deptId)) {
                        users =
                            y9UserDao.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
                    } else {
                        users = y9UserDao.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username,
                            Boolean.TRUE);
                    }
                } else {
                    if (StringUtils.isNotBlank(deptId)) {
                        users =
                            y9UserDao.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
                    } else {
                        users = y9UserDao.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username,
                            Boolean.TRUE);
                    }
                }

                if (users != null && !users.isEmpty()) {
                    Y9User y9User = users.get(0);
                    String hashed = y9User.getPassword();
                    if (!Y9MessageDigest.checkpw(password, hashed)) {
                        throw new FailedLoginException("密码错误。");
                    }
                    // 特殊处理登录成功后还原登录账号
                    if (oldUsername.contains("&")) {
                        username = oldUsername.substring(0, oldUsername.indexOf("&"));
                        riseCredential.setUsername(username);
                        riseCredential.setTenantShortName(oldTenantname);
                    }
                    try {
                        saveLoginInfoService.SaveLoginInfo(riseCredential, "true", "登录成功");
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    AuthenticationHandlerExecutionResult hr = new DefaultAuthenticationHandlerExecutionResult(this,
                        new BasicCredentialMetaData(riseCredential), principalFactory.createPrincipal(username));
                    return hr;
                } else {
                    throw new FailedLoginException("没有找到这个用户。");
                }
            }
        } catch (DataAccessException e) {
            throw new FailedLoginException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<? extends Credential> clazz) {
        return RememberMeUsernamePasswordCredential.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean supports(final Credential credential) {
        boolean b = credential instanceof RememberMeUsernamePasswordCredential;
        return b;
    }

}
