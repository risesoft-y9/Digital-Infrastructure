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
    public static <T extends Enum<T> & ValuedEnum<V>, V> T valueOf(Class<T> enumType, V value) {
        for (T enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName());
    }
}
