package net.risesoft.permission.cache;

/**
 * 请求缓存
 *
 * @author shidaobang
 * @date 2025/07/21
 */
public interface SubmitCache {

    /**
     * 放入缓存
     *
     * @param key 键
     * @param value 值
     * @param ttl 存活时间，单位毫秒
     */
    void put(String key, String value, long ttl);

    String get(String key);
}
