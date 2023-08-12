package org.apereo.cas.web.y9.controller;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.web.y9.util.Y9Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/session")
@Slf4j
public class RedisController {

    private final CasRedisTemplate<Object, Object> redisTemplate;

    public RedisController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping(value = "/delete")
    public Y9Result<String> delete(String key, HttpServletRequest request) {
        Y9Result<String> y9result = new Y9Result<String>();
        y9result.setCode(200);
        y9result.setMsg("删除失败");
        y9result.setSuccess(false);
        try {
            if (StringUtils.isEmpty(key)) {
                y9result.setMsg("删除失败：key不能为空。");
                return y9result;
            }
            key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
            boolean b = redisTemplate.delete(key);
            y9result.setCode(200);
            y9result.setMsg("删除成功");
            y9result.setSuccess(b);
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }

    @PostMapping(value = "/get")
    public Y9Result<Object> get(String key, HttpServletRequest request) {
        Y9Result<Object> y9result = new Y9Result<>();
        y9result.setCode(200);
        y9result.setMsg("获取失败");
        y9result.setSuccess(false);
        y9result.setData("");
        try {
            if (StringUtils.isEmpty(key)) {
                y9result.setMsg("获取失败：key不能为空。");
                return y9result;
            }
            key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
            Object value = redisTemplate.opsForValue().get(key);
            if (null != value) {
                y9result.setCode(200);
                y9result.setMsg("获取成功");
                y9result.setSuccess(true);
                y9result.setData(value);
            }
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }

    private String getIpAddr(HttpServletRequest request) {
        String addr = null;
        String[] ADDR_HEADER = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : ADDR_HEADER) {
            if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
                addr = request.getHeader(header);
            } else {
                break;
            }
        }
        if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
            addr = request.getRemoteAddr();
        } else {
            int i = addr.indexOf(",");
            if (i > 0) {
                addr = addr.substring(0, i);
            }
        }
        return addr;
    }

    @PostMapping(value = "/refresh")
    public Y9Result<String> refresh(String key, Long timeout, HttpServletRequest request) {
        Y9Result<String> y9result = new Y9Result<String>();
        y9result.setCode(200);
        y9result.setMsg("刷新失败");
        y9result.setSuccess(false);
        try {
            if (StringUtils.isEmpty(key)) {
                y9result.setMsg("刷新失败：key不能为空");
                return y9result;
            }
            key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                if (null == timeout) {
                    timeout = Long.valueOf(3600);
                }
                redisTemplate.opsForValue().set(
                    "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key, obj,
                    timeout, TimeUnit.SECONDS);
                y9result.setCode(200);
                y9result.setMsg("刷新成功");
                y9result.setSuccess(true);
            } else {
                y9result.setCode(200);
                y9result.setMsg("刷新失败：session已过期自动清除。");
                y9result.setSuccess(false);
                return y9result;
            }
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }

    @PostMapping(value = "/save")
    public Y9Result<String> save(String key, String value, Long timeout, HttpServletRequest request) {
        Y9Result<String> y9result = new Y9Result<String>();
        y9result.setCode(200);
        y9result.setMsg("保存失败");
        y9result.setSuccess(false);
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                y9result.setMsg("保存失败：key或者value不能为空。");
            } else {
                if (null == timeout) {
                    timeout = Long.valueOf(3600);
                }
                key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
                redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
                y9result.setCode(200);
                y9result.setMsg("保存成功");
                y9result.setSuccess(true);
            }
        } catch (Exception e) {
            y9result.setCode(500);
            LOGGER.warn(e.getMessage(), e);
        }
        return y9result;
    }
}
