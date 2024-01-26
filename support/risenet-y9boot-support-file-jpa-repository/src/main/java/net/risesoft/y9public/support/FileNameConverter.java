package net.risesoft.y9public.support;

import javax.persistence.AttributeConverter;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.base64.Y9Base64Util;

public class FileNameConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        String encodeValue = attribute;
        String base64FileName = Y9Context.getProperty("y9.feature.file.base64FileName", "false");
        if ("true".equalsIgnoreCase(base64FileName)) {
            encodeValue = Y9Base64Util.encode(attribute);
        }
        return encodeValue;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        String decodeValue = dbData;
        String base64FileName = Y9Context.getProperty("y9.feature.file.base64FileName", "false");
        if ("true".equalsIgnoreCase(base64FileName)) {
            int i = dbData.indexOf("."); // 数据库里存储的如果是已经base64编码的数据，应该没有扩展名
            if (i == -1) {
                decodeValue = Y9Base64Util.decode(dbData);
            }
        }
        return decodeValue;
    }
}
