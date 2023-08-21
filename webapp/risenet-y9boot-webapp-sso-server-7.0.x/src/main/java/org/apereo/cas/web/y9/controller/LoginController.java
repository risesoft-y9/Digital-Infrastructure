package org.apereo.cas.web.y9.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.rest.BadRestRequestException;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.y9.service.Y9UserService;
import org.apereo.cas.web.y9.util.Y9MessageDigest;
import org.apereo.cas.web.y9.util.common.Base64Util;
import org.apereo.cas.web.y9.util.common.CheckPassWord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dingzhaojun
 * @author mengjuhua
 * @author shidaobang
 * @author qinman
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class LoginController {

    private final CentralAuthenticationService centralAuthenticationService;
    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;
    private final AuthenticationSystemSupport authenticationSystemSupport;
    private final ServiceFactory webApplicationServiceFactory;

    private final Y9UserService y9UserService;

    public LoginController(CentralAuthenticationService centralAuthenticationService,
        @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
        @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
        @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
        Y9UserService y9UserService) {
        this.centralAuthenticationService = centralAuthenticationService;
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.webApplicationServiceFactory = webApplicationServiceFactory;
        this.y9UserService = y9UserService;
    }

    public Map<String, Object> checkSsoLoginInfo(String tenantShortName, String username, String password,
        String pwdEcodeType, String loginType, final HttpServletRequest request, final HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean validation = false;
        try {
            username = Base64Util.decode(username, "Unicode");
            if (StringUtils.isBlank(pwdEcodeType) || !"SHA1".equals(pwdEcodeType)) {
                password = Base64Util.decode(password, "Unicode");
                // password = Y9MessageDigest.hashpw(password);
            }
            if (username.contains("&")) {
                username = username.substring(username.indexOf("&") + 1, username.length());
                tenantShortName = "isv";
            }
            String encryptedPassword = password;
            List<Y9User> users = null;
            if ("mobile".equals(loginType)) {
                users = y9UserService.findByTenantShortNameAndMobile(tenantShortName, username);
            } else {
                users = y9UserService.findByTenantShortNameAndLoginName(tenantShortName, username);
            }
            if (users.isEmpty()) {
                map.put("msg", "该账号不存在，请检查账号输入是否正确！");
                map.put("success", false);
                return map;
            }
            // 检验该账号是否已经被锁定
            // checkLoginThrottle(map, username);
            if (!users.isEmpty()) {
                validation = true;
            }

            if (!validation) {
                // processLoginThrottle(map, username);
                map.put("success", false);
                map.put("msg", "登录名或者密码输入错误!");
                return map;
            }
            Y9User y9User = users.get(0);
            String hashed = y9User.getPassword();
            if (!Y9MessageDigest.checkpw(password, hashed)) {
                map.put("msg", "密码错误!");
                map.put("success", false);
                return map;
            }
            // mongoTemplate.remove(new Query(Criteria.where("name").is(username)),
            // LoginThrottle.class);
            boolean isSimplePassWord = CheckPassWord.isSimplePassWord(password);
            if (isSimplePassWord) {
                map.put("msg", "密码过于简单,请重新设置密码！");
            }
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            map.put("msg", "认证失败!");
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @PostMapping(value = "/logon", consumes = MediaType.ALL_VALUE)
    public final ResponseEntity<Map<String, Object>> logon(RememberMeUsernamePasswordCredential credential,
        @RequestBody(required = false) final MultiValueMap<String, String> requestBody,
        final HttpServletRequest request, final HttpServletResponse response) {
        Map<String, Object> ret_map = new HashMap<String, Object>();
        ret_map.put("success", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (credential == null) {
                throw new BadRestRequestException(
                    "No credentials are provided or extracted to authenticate the REST request");
            }
            ret_map =
                checkSsoLoginInfo(credential.getTenantShortName(), credential.getUsername(), credential.toPassword(),
                    request.getParameter("pwdEcodeType"), credential.getLoginType(), request, response);
            if (ret_map.get("success").toString().equals("false")) {
                return new ResponseEntity<Map<String, Object>>(ret_map, headers, HttpStatus.UNAUTHORIZED);
            }

            final Service service = this.webApplicationServiceFactory.createService(request);
            val authenticationResult =
                authenticationSystemSupport.finalizeAuthenticationTransaction(service, credential);
            if (authenticationResult == null) {
                throw new FailedLoginException("Authentication failed");
            }

            TicketGrantingTicket tgt = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
            String tgtId = tgt.getId();
            ticketGrantingTicketCookieGenerator.addCookie(request, response, tgtId);
            final ServiceTicket serviceTicket =
                this.centralAuthenticationService.grantServiceTicket(tgtId, service, authenticationResult);

            ret_map.put("success", true);
            ret_map.put("msg", serviceTicket.getId());
            return new ResponseEntity<Map<String, Object>>(ret_map, headers, HttpStatus.OK);
        } catch (final Exception e) {
            ret_map.put("success", false);
            ret_map.put("msg", e.getMessage());
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Map<String, Object>>(ret_map, headers, HttpStatus.UNAUTHORIZED);
        }
    }

}
