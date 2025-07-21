package net.risesoft.permission.cache;

import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import lombok.RequiredArgsConstructor;

/**
 * 请求使用本地缓存
 * 
 * @author shidaobang
 * @date 2025/07/21
 */
@RequiredArgsConstructor
public class LocalSubmitCache implements SubmitCache {

    private final Cache<String, String> cache = Caffeine.newBuilder().expireAfter(new Expiry<String, String>() {
        @Override
        public long expireAfterCreate(@NonNull String key, @NonNull String value, long currentTime) {
            return TimeUnit.MILLISECONDS.toNanos(Long.parseLong(value));
        }

        @Override
        public long expireAfterUpdate(@NonNull String key, @NonNull String value, long currentTime,
            @NonNegative long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(@NonNull String key, @NonNull String value, long currentTime,
            @NonNegative long currentDuration) {
            return currentDuration;
        }
    }).maximumSize(10000).build();

    @Override
    public void put(String key, String value, long ttl) {
        cache.put(key, String.valueOf(ttl));
    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }
}
