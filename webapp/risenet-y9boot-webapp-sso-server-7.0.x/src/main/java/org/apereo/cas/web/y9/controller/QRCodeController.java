package org.apereo.cas.web.y9.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.web.y9.util.Y9Base64;
import org.apereo.cas.web.y9.util.Y9QRCode;
import org.apereo.cas.web.y9.util.Y9Result;
import org.apereo.cas.web.y9.util.common.Y9Util;
import org.apereo.cas.web.y9.util.json.Y9JacksonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/api")
@Slf4j
public class QRCodeController {

    @Value("${cas.server.name}")
    private String name;
    
    private final CasRedisTemplate<Object, Object> redisTemplate;

    public QRCodeController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @ResponseBody
    @RequestMapping(value = "/getQRCode", method = RequestMethod.POST)
    public Map<String, Object> getQRCode() {
        Map<String, Object> map = new HashMap<>();
        map.put("img", "");
        map.put("uuid", "");
        map.put("success", false);
        try {
            name = name.endsWith("/") ? name : name + "/";
            String uuid = UUID.randomUUID().toString();
            String url = name + "sso/login?url=" + name + "sso/api/saveScanResult&uuid=" + uuid;
            InputStream imgis = this.getClass().getClassLoader().getResourceAsStream("static/y9static/y9new/img/qrCodeLogo.png");
            String img = Y9QRCode.encode(url, 512, 512, imgis);
            redisTemplate.opsForValue().set("QRCode:" + uuid, 2, 120, TimeUnit.SECONDS);
            map.put("img", img);
            map.put("uuid", uuid);
            map.put("success", true);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/getScanResult", method = RequestMethod.POST)
    public Map<String, Object> getQRCode(String uuid) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("userId", "");
        map.put("msg", "获取扫描结果失败");
        try {
            Object value = redisTemplate.opsForValue().get("QRCode:" + uuid);
            if (null != value) {
                String val = String.valueOf(value);
                if (val.contains("$")) {
                    String valArr[] = val.split("\\$");
                    map.put("scanResult", valArr[0]);
                    map.put("userId", valArr[1]);
                    map.put("success", true);
                } else {
                    map.put("success", true);
                    map.put("scanResult", val);
                }
                map.put("msg", "获取扫描结果成功");
            } else {
                map.put("scanResult", 0);
                map.put("msg", "uuid对应的二维码已过期");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/saveScanResult")
    public void saveScanResult(String uuid, String userId, HttpServletResponse response) {
        Y9Result<String> y9result = new Y9Result<String>();
        y9result.setCode(400);
        y9result.setMsg("扫码失败");
        y9result.setSuccess(false);
        try {
            if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(userId)) {
                y9result.setMsg("请求参数有问题：uuid或userId不能为空");
                Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(y9result));
                return;
            }
            userId = Y9Base64.decode(userId);
            Object obj = redisTemplate.opsForValue().get("QRCode:" + uuid);
            if (null != obj) {
                String val = String.valueOf(obj);
                if (val.contains("1$")) {
                    y9result.setCode(419);
                    y9result.setMsg("二维码已过期：已被扫描。");
                    Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(y9result));
                    return;
                } else {
                    redisTemplate.opsForValue().set("QRCode:" + uuid, 1 + "$" + userId, 300, TimeUnit.SECONDS);
                    y9result.setCode(200);
                    y9result.setMsg("扫码成功");
                    y9result.setSuccess(true);
                }
            } else {
                y9result.setCode(419);
                y9result.setMsg("二维码已过期：uuid已过期自动清除。");
                Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(y9result));
                return;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(y9result));
    }
}