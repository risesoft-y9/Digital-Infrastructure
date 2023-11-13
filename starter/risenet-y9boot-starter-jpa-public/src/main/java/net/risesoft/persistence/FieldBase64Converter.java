package net.risesoft.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.y9.util.base64.Y9Base64Util;

/**
 * 对字段存入数据库之前 Base64 编码，从数据库中取出后解码
 *
 * @author shidaobang
 * @date 2022/08/04
 */
@Converter
public class FieldBase64Converter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (StringUtils.isNotBlank(attribute)) {
            return Y9Base64Util.encode(attribute);
        }
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            return Y9Base64Util.decode(dbData);
        }
        return dbData;
    }

}
