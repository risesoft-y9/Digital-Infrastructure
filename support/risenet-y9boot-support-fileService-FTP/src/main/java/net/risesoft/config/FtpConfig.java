package net.risesoft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.y9public.ftp.FtpClientFactory;
import net.risesoft.y9public.ftp.FtpClientHelper;
import net.risesoft.y9public.ftp.FtpClientPool;
import net.risesoft.y9public.ftp.FtpPoolConfig;

@Configuration
public class FtpConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "y9.feature.file.ftp")
    public FtpPoolConfig ftpPoolConfig() {
        FtpPoolConfig ftpPoolConfig = new FtpPoolConfig();
        return ftpPoolConfig;
    }
    
    @Bean
    public FtpClientFactory ftpClientFactory(FtpPoolConfig ftpPoolConfig) {
        FtpClientFactory ftpClientFactory = new FtpClientFactory();
        ftpClientFactory.setFtpPoolConfig(ftpPoolConfig);
        return ftpClientFactory;
    }

    @Bean
    public FtpClientPool ftpClientPool(FtpClientFactory clientFactory) {
        FtpClientPool ftpClientPool = new FtpClientPool(clientFactory);
        return ftpClientPool;
    }
    
    @Bean
    public FtpClientHelper ftpClientHelper(FtpClientPool ftpClientPool) {
        FtpClientHelper ftpClientHelper = new FtpClientHelper();
        ftpClientHelper.setFtpClientPool(ftpClientPool);
        return ftpClientHelper;
    }

}
