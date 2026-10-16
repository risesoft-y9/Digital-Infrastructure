package net.risesoft.y9.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.risesoft.enums.ValuedEnum;

/**
 * 枚举工具类
 * 
 * @author shidaobang
 * @date 2023/11/15
 * @since 9.6.3
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9EnumUtil {
    /**
     * 根据枚举值获取对应的枚举常量
     * 
     * @param <T> 枚举类型
     * @param <V> 值类型
     * @param enumType 枚举类型
     * @param value 枚举值
     * @return T 对应的枚举常量
     * @throws IllegalArgumentException 未找到匹配的枚举常量时抛出
     */
    public static <T extends Enum<T> & ValuedEnum<V>, V> T valueOf(Class<T> enumType, V value) {
        for (T enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName());
    }
}
