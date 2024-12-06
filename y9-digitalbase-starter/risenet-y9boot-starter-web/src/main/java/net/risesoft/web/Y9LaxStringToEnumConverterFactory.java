package net.risesoft.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

import net.risesoft.enums.ValuedEnum;

/**
 * 字符串转枚举的转换器
 * 
 * @author shidaobang
 * @date 2023/11/13
 * @since 9.6.3
 */
public class Y9LaxStringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum<>(targetType);
    }

    private static class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            for (T enumConstant : enumType.getEnumConstants()) {
                // 根据枚举定义的名称忽略大小写匹配
                if (StringUtils.equalsIgnoreCase(enumConstant.toString(), source)) {
                    return enumConstant;
                }
                // 根据枚举value值转字符串忽略大小写匹配
                if (enumConstant instanceof ValuedEnum
                    && StringUtils.equalsIgnoreCase(((ValuedEnum<?>)enumConstant).getValue().toString(), source)) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + source);
        }
    }
}
