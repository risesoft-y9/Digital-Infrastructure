package net.risesoft.y9.configuration.feature.file.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * ftp配置参数对象 继承自GenericObjectPoolConfig
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.file.ftp", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class FtpPoolConfig extends GenericObjectPoolConfig<FTPClient> {

    /** 是否启用 */
    private boolean enabled = false;

    /** 主机名 */
    private String host;
    /** 端口 */
    private int port = 21;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

    /** ftp 连接超时时间 毫秒 */
    private int connectTimeOut = 50000;
    /** 字符集 */
    private String controlEncoding = "utf-8";
    /** 缓冲区大小 */
    private int bufferSize = 10240;
    /** 传输数据格式 2表示binary二进制数据 */
    private int fileType = 2;
    /** 数据传输超时时间 */
    private int dataTimeout = 1200000;
    /** 是否启用IPV4 */
    private boolean useEPSVwithIPv4 = false;
    /** 是否启用被动模式 */
    private boolean passiveMode = true;

}
