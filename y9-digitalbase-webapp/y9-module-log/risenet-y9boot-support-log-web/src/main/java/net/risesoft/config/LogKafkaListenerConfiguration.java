package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.kafka.Y9AccessLogListener;
import net.risesoft.kafka.Y9UserLoginInfoListener;
import net.risesoft.y9public.service.Y9logAccessLogService;
import net.risesoft.y9public.service.Y9logIpDeptMappingService;
import net.risesoft.y9public.service.Y9logUserHostIpInfoService;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "y9.app.y9public", name = "kafkaEnabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class LogKafkaListenerConfiguration {

    @Bean
    public Y9AccessLogListener y9AccessLogListener(final Y9logAccessLogService y9logAccessLogService) {
        LOGGER.info("LogKafkaConfiguration y9AccessLogListener init ......");
        return new Y9AccessLogListener(y9logAccessLogService);
    }

    @Bean("y9KafkaTemplate")
    @Primary
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        LOGGER.info("LogKafkaConfiguration y9KafkaTemplate init ......");
        return new KafkaTemplate<>(kafkaProducerFactory);
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
