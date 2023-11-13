package net.risesoft.persistence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.ValuedEnum;
import net.risesoft.y9.util.Y9EnumUtil;

/**
 * 对字段存入数据库之前获取enum的value字段，从数据库中取出转成对应的Enum
 * 
 * @author shidaobang
 * @date 2023/11/14
 * @since 9.6.3
 */
@Converter
@RequiredArgsConstructor
public abstract class AbstractEnumConverter<X extends Enum<X> & ValuedEnum<Y>, Y> implements AttributeConverter<X, Y> {

    private final Class<X> enumType;

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return attribute.getValue();
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return Y9EnumUtil.valueOf(enumType, dbData);
    }
}
