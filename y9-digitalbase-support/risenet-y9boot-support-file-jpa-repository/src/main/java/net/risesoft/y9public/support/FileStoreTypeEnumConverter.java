package net.risesoft.y9public.support;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.persistence.AbstractEnumConverter;

/**
 * 文件存储类型转换器
 *
 * @author shidaobang
 * @date 2025/10/29
 */
public class FileStoreTypeEnumConverter extends AbstractEnumConverter<FileStoreTypeEnum, Integer> {
    public FileStoreTypeEnumConverter() {
        super(FileStoreTypeEnum.class);
    }
}