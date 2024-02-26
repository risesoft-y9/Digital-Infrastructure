package net.risesoft.y9public.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * ftp配置参数对象 继承自GenericObjectPoolConfig
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class FtpPoolConfig extends GenericObjectPoolConfig<FTPClient> {

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
    /** 传输数据格式 2表binary二进制数据 */
    private int fileType = 2;
    /***/
    private int dataTimeout = 1200000;
    /** 是否启用IPV4 */
    private boolean useEPSVwithIPv4 = false;
    /** 是否启用被动模式 */
    private boolean passiveMode = true;

    public int getBufferSize() {
        return bufferSize;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public int getFileType() {
        return fileType;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public boolean isUseEPSVwithIPv4() {
        return useEPSVwithIPv4;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUseEPSVwithIPv4(boolean EPSVwithIPv4) {
        this.useEPSVwithIPv4 = useEPSVwithIPv4;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
