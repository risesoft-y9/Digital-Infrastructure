package net.risesoft.log;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.ttl.TransmittableThreadLocal;

import lombok.NoArgsConstructor;

/**
 * y9日志上下文
 * 
 * 存储日志上下文变量，用于 spring-el 解析或者直接存储
 *
 * @author shidaobang
 * @date 2026/01/22
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Y9LogContext {

    private static final TransmittableThreadLocal<Map<String, Object>> MAP_HOLDER = new TransmittableThreadLocal<>();

    public static void clear() {
        if (MAP_HOLDER.get() != null) {
            MAP_HOLDER.get().clear();
        }
    }

    public static Map<String, Object> getMap() {
        Map<String, Object> map = MAP_HOLDER.get();
        return map == null ? new HashMap<>() : map;
    }

    public static void put(String key, Object object) {
        if (MAP_HOLDER.get() == null) {
            MAP_HOLDER.set(new HashMap<>());
        }
        MAP_HOLDER.get().put(key, object);
    }

}
