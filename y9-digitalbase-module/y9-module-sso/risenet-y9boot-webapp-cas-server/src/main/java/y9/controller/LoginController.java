package y9.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.rest.BadRestRequestException;
import y9.entity.Y9User;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import y9.service.Y9UserService;
import y9.util.Y9Context;
import y9.util.Y9MessageDigest;
import y9.util.common.Base64Util;
import y9.util.common.CheckPassWord;
import y9.util.common.RSAUtil;

import javax.security.auth.login.FailedLoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequiredArgsConstructor
public class LoginController {

    private final CentralAuthenticationService centralAuthenticationService;
    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;
    private final AuthenticationSystemSupport authenticationSystemSupport;
    private final ServiceFactory webApplicationServiceFactory;

    private final Y9UserService y9UserService;

    public Map<String, Object> checkSsoLoginInfo(String tenantShortName, String username, String password,
        String pwdEcodeType, String loginType, final HttpServletRequest request, final HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            username = Base64Util.decode(username, "Unicode");
            if (StringUtils.isNotBlank(pwdEcodeType)) {
                String privateKey = Y9Context.getProperty("y9.login.encryptionRsaPrivateKey");
                password = RSAUtil.privateDecrypt(password, privateKey);
            }
            password = Base64Util.decode(password, "Unicode");
            if (username.contains("&")) {
                username = username.substring(username.indexOf("&") + 1);
                tenantShortName = "operation";
            }
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

            Y9User y9User = users.get(0);
            String hashed = y9User.getPassword();
            if (!Y9MessageDigest.bcryptMatch(password, hashed)) {
                map.put("msg", "密码错误!");
                map.put("success", false);
                return map;
            }

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
        final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("success", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (credential == null) {
                throw new BadRestRequestException(
                    "No credentials are provided or extracted to authenticate the REST request");
            }
            retMap =
                checkSsoLoginInfo(credential.getTenantShortName(), credential.getUsername(), credential.toPassword(),
                    request.getParameter("pwdEcodeType"), credential.getLoginType(), request, response);
            if (retMap.get("success").toString().equals("false")) {
                return new ResponseEntity<>(retMap, headers, HttpStatus.UNAUTHORIZED);
            }

            final Service service = this.webApplicationServiceFactory.createService(request);
            val authenticationResult =
                authenticationSystemSupport.finalizeAuthenticationTransaction(service, credential);
            if (authenticationResult == null) {
                throw new FailedLoginException("Authentication failed");
            }

            Ticket tgt = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
            String tgtId = tgt.getId();
            ticketGrantingTicketCookieGenerator.addCookie(request, response, tgtId);
            final Ticket serviceTicket =
                this.centralAuthenticationService.grantServiceTicket(tgtId, service, authenticationResult);

            retMap.put("success", true);
            retMap.put("msg", serviceTicket.getId());
            return new ResponseEntity<>(retMap, headers, HttpStatus.OK);
        } catch (final Exception e) {
            retMap.put("success", false);
            retMap.put("msg", e.getMessage());
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(retMap, headers, HttpStatus.UNAUTHORIZED);
        }
    }

}
