package net.risesoft.y9public.ftp;

import java.io.IOException;

import net.risesoft.y9.configuration.feature.file.ftp.FtpPoolConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    private FtpPoolConfig ftpPoolConfig;

    /**
     * 新建对象
     */
    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpPoolConfig.getConnectTimeOut());
        // 必须在ftpClient.connect方法之前设置才起作用。
        ftpClient.setControlEncoding(ftpPoolConfig.getControlEncoding());
        // ftpClient.setAutodetectUTF8(true);

        try {

            LOGGER.info("连接 ftp 服务器 {}:{}", ftpPoolConfig.getHost(), ftpPoolConfig.getPort());
            ftpClient.connect(ftpPoolConfig.getHost(), ftpPoolConfig.getPort());

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LOGGER.error("ftp 服务器拒绝连接");
                return null;
            }

            boolean result = ftpClient.login(ftpPoolConfig.getUsername(), ftpPoolConfig.getPassword());
            if (!result) {
                LOGGER.error("ftpClient 登录失败!");
                throw new Exception("ftpClient 登录失败! userName:" + ftpPoolConfig.getUsername() + ", password:"
                    + ftpPoolConfig.getPassword());
            }

            ftpClient.setBufferSize(ftpPoolConfig.getBufferSize());
            ftpClient.setFileType(ftpPoolConfig.getFileType());
            ftpClient.setDataTimeout(ftpPoolConfig.getDataTimeout());
            ftpClient.setUseEPSVwithIPv4(ftpPoolConfig.isUseEPSVwithIPv4());
            if (ftpPoolConfig.isPassiveMode()) {
                LOGGER.info("进入 ftp 被动模式");
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            LOGGER.error("ftp 服务器连接失败：", e);
        }
        return ftpClient;
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        ftpClient.logout();
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            LOGGER.error("ftpClient 登出失败", io);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                LOGGER.error("ftpClient 断开连接失败", io);
            }
        }
    }

    public FtpPoolConfig getFtpPoolConfig() {
        return ftpPoolConfig;
    }

    public void setFtpPoolConfig(FtpPoolConfig ftpPoolConfig) {
        this.ftpPoolConfig = ftpPoolConfig;
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
        } catch (IOException e) {
            LOGGER.warn("验证ftp连接对象失败");
            // LOGGER.warn("验证ftp连接对象失败", e);
        }
        return connect;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

}