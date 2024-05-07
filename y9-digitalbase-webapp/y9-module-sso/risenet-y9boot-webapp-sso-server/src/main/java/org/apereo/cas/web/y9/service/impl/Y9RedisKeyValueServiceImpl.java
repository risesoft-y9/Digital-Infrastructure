package org.apereo.cas.web.y9.service.impl;

import java.util.concurrent.TimeUnit;

import org.apereo.cas.web.y9.service.Y9KeyValueService;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Y9RedisKeyValueServiceImpl implements Y9KeyValueService {

    private final RedisTemplate<Object, Object> y9RedisTemplate;

    @Override
    public void cleanUpExpiredKeyValue() {
        // 由 redis 处理即可
    }

    @Override
    public String get(String key) {
        Object value = y9RedisTemplate.opsForValue().get(key);
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public void put(String key, String value, long minutes) {
        y9RedisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }
}
