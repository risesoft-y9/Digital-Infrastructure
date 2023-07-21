package net.risesoft.y9.configuration.feature.file.ftp;


import lombok.Getter;
import lombok.Setter;

/**
 * ftp 存储文件配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9FtpProperties {

    /**
     * 主机名
     */
    private String host = "localhost";
    /**
     * 端口
     */
    private int port = 21;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private int password;
    /**
     * 连接超时时间(毫秒)
     */
    private int connectTimeOut = 50000;
    /**
     * 对象池耗尽时是否阻塞
     */
    private boolean blockWhenExhausted = true;
    /**
     * 编码
     */
    private String controlEncoding = "utf-8";
    /**
     * 缓冲区大小
     */
    private int bufferSize = 10240;
    /**
     * 传输数据格式
     * 2=binary二进制数据
     */
    private int fileType = 2;
    /**
     * 数据超时时间
     */
    private int dataTimeout = 1200000;
    /**
     * 是否使用 epsv ipv4
     */
    private boolean useEPSVwithIPv4 = false;
    /**
     * 是否启用被动模式
     */
    private boolean passiveMode = true;
    /**
     * 从连接池中拿对象时是否测试
     */
    private boolean testOnBorrow = false;
    /**
     * 创建连接对象时是否测试
     */
    private boolean testOnCreate = false;
    /**
     * 归还连接对象到连接池时是否测试
     */
    private boolean testOnReturn = false;
    /**
     * 连接对象空闲时是否测试
     */
    private boolean testWhileIdle = true;
    /**
     * 最大空闲值
     */
    private int maxIdle = 10;
    /**
     * 总数
     */
    private int maxTotal = 50;
    /**
     * 最大毫秒值
     */
    private int maxWaitMillis = 5400000;
    /**
     * 最小空闲值
     */
    private int minIdle = 2;

}
