package org.apereo.cas.web.y9.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.web.y9.service.Y9UserService;
import org.apereo.cas.web.y9.util.Y9MessageDigest;
import org.apereo.cas.web.y9.util.Y9Result;
import org.apereo.cas.web.y9.util.common.Base64Util;
import org.apereo.cas.web.y9.util.common.CheckPassWord;
import org.apereo.cas.web.y9.util.common.RSAUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/api")
@Slf4j
public class CheckController {

    private final Y9UserService y9UserService;

    private final CasRedisTemplate<Object, Object> redisTemplate;

    public CheckController(Y9UserService y9UserService,
        @Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
        this.y9UserService = y9UserService;
        this.redisTemplate = redisTemplate;
        LOGGER.info("CheckController created...");
    }

    private void changeSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // We need to migrate to a new session
            String originalSessionId = session.getId();
            HashMap<String, Object> attributesToMigrate = createMigratedAttributeMap(session);

            session.invalidate();
            session = request.getSession(true); // we now have a new session
            String newSessionId = session.getId();
            LOGGER.debug("Started new session: " + newSessionId);

            // Copy attributes to new session
            if (attributesToMigrate != null) {
                for (Map.Entry<String, Object> entry : attributesToMigrate.entrySet()) {
                    session.setAttribute(entry.getKey(), entry.getValue());
                }
            }

            if (originalSessionId.equals(newSessionId)) {
                LOGGER.warn(
                    "Your servlet container did not change the session ID when a new session was created. You will not be adequately protected against session-fixation attacks");
            }
        }
    }

    @ResponseBody
    @GetMapping(value = "/getRandom")
    public Y9Result<Object> get() {
        Y9Result<Object> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("获取失败");
        y9result.setSuccess(false);
        y9result.setData("");
        try {
            String[] rsaArr = RSAUtil.genKeyPair();
            redisTemplate.opsForValue().set(rsaArr[0], rsaArr[1], 120, TimeUnit.SECONDS);
            y9result.setCode(200);
            y9result.setMsg("获取成功：随机字符串有效期为两分钟。");
            y9result.setSuccess(true);
            y9result.setData(rsaArr[0]);
        } catch (Exception e) {
            y9result.setCode(500);
            e.printStackTrace();
        }
        return y9result;
    }

    @ResponseBody
    @RequestMapping(value = "/checkSsoLoginInfo", method = RequestMethod.POST)
    public Map<String, Object> checkSsoLoginInfo(final RememberMeUsernamePasswordCredential riseCredential,
        final HttpServletRequest request, final HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("msg", "认证成功!");
        changeSessionId(request);
        try {
            String tenantShortName = riseCredential.getTenantShortName();
            String username = riseCredential.getUsername();
            String password = riseCredential.toPassword();
            String pwdEcodeType = riseCredential.getPwdEcodeType();

            username = Base64Util.decode(username, "Unicode");
            if (StringUtils.isNotBlank(pwdEcodeType)) {
                Object obj = redisTemplate.opsForValue().get(pwdEcodeType);
                if (null != obj) {
                    password = RSAUtil.privateDecrypt(password, String.valueOf(obj));
                } else {
                    map.put("msg", "认证失败：随机数已过期，请重新登录!");
                    return map;
                }
            }
            password = Base64Util.decode(password, "Unicode");
            if (username.contains("&")) {
                username = username.substring(username.indexOf("&") + 1, username.length());
                tenantShortName = "operation";
            }
            riseCredential.setUsername(username);
            String loginType = riseCredential.getLoginType();

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
            if (!Y9MessageDigest.checkpw(password, hashed)) {
                map.put("msg", "密码错误!");
                map.put("success", false);
                return map;
            }
            boolean isSimplePassWord = CheckPassWord.isSimplePassWord(password);
            if (isSimplePassWord) {
                map.put("msg", "密码过于简单,请重新设置密码！");
            }

        } catch (Exception e) {
            map.put("success", false);
            map.put("msg", "认证失败!");
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    private HashMap<String, Object> createMigratedAttributeMap(HttpSession session) {
        HashMap<String, Object> attributesToMigrate = new HashMap<String, Object>();
        Enumeration<String> enumer = session.getAttributeNames();
        while (enumer.hasMoreElements()) {
            String key = enumer.nextElement();
            attributesToMigrate.put(key, session.getAttribute(key));
        }
        return attributesToMigrate;
    }

}
