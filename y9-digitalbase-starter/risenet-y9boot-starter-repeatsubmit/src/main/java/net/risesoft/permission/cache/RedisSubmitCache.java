package net.risesoft.permission.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;

/**
 * 请求使用 redis 缓存
 *
 * @author shidaobang
 * @date 2025/07/21
 */
@RequiredArgsConstructor
public class RedisSubmitCache implements SubmitCache {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void put(String key, String value, long tll) {
        redisTemplate.opsForValue().set(key, String.valueOf(value), tll, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
