package net.risesoft.y9.configuration.feature.file.samba;

import lombok.Getter;
import lombok.Setter;

/**
 * samba 存储文件配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9SambaProperties {

    /**
     * 主机名
     */
    private String hostname;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 共享名称
     */
    private String shareName = "Y9FileStore";
    /**
     * 缓冲区大小
     */
    private Integer bufferSize = 1024 * 1024;
    /**
     * socket 超时时间
     */
    private Integer socketTimeout = 0;
    /**
     * 超时时间
     */
    private Integer timeout = 60;

}
