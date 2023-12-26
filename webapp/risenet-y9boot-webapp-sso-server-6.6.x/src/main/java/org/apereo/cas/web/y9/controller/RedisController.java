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
    public Y9Result<Boolean> delete(String key, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(key)) {
                return Y9Result.failure("删除失败：key不能为空。");
            }
            key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
            boolean b = redisTemplate.delete(key);
            return Y9Result.success(b, "删除成功");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("删除失败");
        }
    }

    @PostMapping(value = "/get")
    public Y9Result<Object> get(String key, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(key)) {
                return Y9Result.failure("获取失败：key不能为空。");
            }
            key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
            Object value = redisTemplate.opsForValue().get(key);
            if (null != value) {
                return Y9Result.success(value, "获取成功");
            } else {
                return Y9Result.failure("获取失败,未找到值");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("获取失败," + e.getMessage());
        }
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
        try {
            if (StringUtils.isEmpty(key)) {
                return Y9Result.failure("刷新失败：key不能为空。");
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
                return Y9Result.successMsg("获取成功");
            } else {
                return Y9Result.failure("刷新失败：session已过期自动清除。");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("刷新失败," + e.getMessage());
        }
    }

    @PostMapping(value = "/save")
    public Y9Result<String> save(String key, String value, Long timeout, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                return Y9Result.failure("保存失败：key或者value不能为空。");
            } else {
                if (null == timeout) {
                    timeout = Long.valueOf(3600);
                }
                key = "y9vue_client_session:" + getIpAddr(request) + ":" + request.getHeader("User-Agent") + key;
                redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("保存失败," + e.getMessage());
        }
    }
}
