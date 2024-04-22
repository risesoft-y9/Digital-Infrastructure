package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.y9public.ftp.FtpClientFactory;
import net.risesoft.y9public.ftp.FtpClientHelper;
import net.risesoft.y9public.ftp.FtpClientPool;
import net.risesoft.y9public.ftp.FtpPoolConfig;
import net.risesoft.y9public.service.StoreService;
import net.risesoft.y9public.service.impl.FtpStoreServiceImpl;

@Configuration
@ConditionalOnProperty(name = "y9.feature.file.ftp.enabled", havingValue = "true", matchIfMissing = false)
public class FtpStoreConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "y9.feature.file.ftp")
    public FtpPoolConfig ftpPoolConfig() {
        return new FtpPoolConfig();
    }

    @Bean
    public FtpClientFactory ftpClientFactory(FtpPoolConfig ftpPoolConfig) {
        FtpClientFactory ftpClientFactory = new FtpClientFactory();
        ftpClientFactory.setFtpPoolConfig(ftpPoolConfig);
        return ftpClientFactory;
    }

    @Bean
    public FtpClientPool ftpClientPool(FtpClientFactory clientFactory) {
        return new FtpClientPool(clientFactory);
    }

    @Bean
    public FtpClientHelper ftpClientHelper(FtpClientPool ftpClientPool) {
        FtpClientHelper ftpClientHelper = new FtpClientHelper();
        ftpClientHelper.setFtpClientPool(ftpClientPool);
        return ftpClientHelper;
    }

    @Bean
    public StoreService ftpStoreService(FtpClientHelper ftpClientHelper) {
        return new FtpStoreServiceImpl(ftpClientHelper);
    }

}
