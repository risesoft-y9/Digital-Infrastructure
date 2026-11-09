package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.kafka.Y9AccessLogListener;
import net.risesoft.kafka.Y9FlowableAccessLogListener;
import net.risesoft.kafka.Y9UserLoginInfoListener;
import net.risesoft.y9public.service.Y9logAccessLogService;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;
import net.risesoft.y9public.service.Y9logIpDeptMappingService;
import net.risesoft.y9public.service.Y9logUserHostIpInfoService;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

@Configuration
@ConditionalOnProperty(prefix = "y9.app.log", name = "kafkaEnabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class LogKafkaListenerConfiguration {

    @Bean
    public Y9AccessLogListener y9AccessLogListener(final Y9logAccessLogService y9logAccessLogService) {
        LOGGER.info("LogKafkaConfiguration y9AccessLogListener init ......");
        return new Y9AccessLogListener(y9logAccessLogService);
    }

    @Bean
    public Y9FlowableAccessLogListener
        y9FlowableAccessLogListener(final Y9logFlowableAccessLogService y9logFlowableAccessLogService) {
        LOGGER.info("LogKafkaConfiguration y9FlowableAccessLogListener init ......");
        return new Y9FlowableAccessLogListener(y9logFlowableAccessLogService);
    }

    @Bean
    public Y9UserLoginInfoListener y9UserLoginInfoListener(final Y9logUserLoginInfoService y9logUserLoginInfoService,
        final Y9logUserHostIpInfoService y9logUserHostIpInfoService,
        final Y9logIpDeptMappingService y9logIpDeptMappingService) {
        LOGGER.info("LogKafkaConfiguration y9UserLoginInfoListener init ......");
        return new Y9UserLoginInfoListener(y9logUserLoginInfoService, y9logUserHostIpInfoService,
            y9logIpDeptMappingService);
    }
}
