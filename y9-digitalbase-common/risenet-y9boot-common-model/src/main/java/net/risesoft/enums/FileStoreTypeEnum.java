package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储类型
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum FileStoreTypeEnum implements ValuedEnum<Integer> {
    /** ftp */
    FTP("ftp", 1),
    /** wps */
    WPS("wps", 2),
    /** http 方式请求 fileManager */
    REST("rest", 3),
    /** nfs */
    NFS("nfs", 4),
    /** samba */
    SAMBA("samba", 5);

    private final String name;
    private final Integer value;
}
