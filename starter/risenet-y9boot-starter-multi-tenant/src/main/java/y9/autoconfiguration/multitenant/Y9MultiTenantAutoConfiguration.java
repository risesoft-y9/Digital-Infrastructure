package y9.autoconfiguration.multitenant;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantDataInitializer;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.schema.JpaSchemaUpdater;
import net.risesoft.schema.LiquibaseSchemaUpdater;
import net.risesoft.schema.SchemaUpdater;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import y9.autoconfiguration.jpa.JpaPublicConfiguration;
import y9.autoconfiguration.tenant.SpringMultiTenantConfiguration;

/**
 * @author shidaobang
 * @date 2023/12/05
 * @since 9.6.3
 */
@Configuration
@AutoConfiguration(after = {SpringMultiTenantConfiguration.class, JpaPublicConfiguration.class},
    before = {LiquibaseAutoConfiguration.class})
@EnableConfigurationProperties(Y9Properties.class)
@RequiredArgsConstructor
@Slf4j
public class Y9MultiTenantAutoConfiguration {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean
    @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
    public SchemaUpdater liquibaseDbUpdater(Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer,
        KafkaTemplate<String, String> y9KafkaTemplate) {
        return new LiquibaseSchemaUpdater(y9TenantDataSourceLookup, y9KafkaTemplate, y9MultiTenantSpringLiquibase,
            tenantDataInitializer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
    public SchemaUpdater jpaDbUpdater(@Autowired(required = false) TenantDataInitializer tenantDataInitializer,
        KafkaTemplate<String, String> y9KafkaTemplate) {
        return new JpaSchemaUpdater(y9TenantDataSourceLookup, y9KafkaTemplate, tenantDataInitializer);
    }

    @KafkaListener(topics = {"y9_common_event"})
    public void messageCommonListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value();
        try {
            Y9MessageCommon msg = Y9JsonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();

            if (Y9CommonEventConst.TENANT_DATASOURCE_SYNC.equals(eventType)) {
                this.y9TenantDataSourceLookup.loadDataSources();
                LOGGER.info(this.y9TenantDataSourceLookup.getSystemName() + ", 同步租户数据源信息, 成功！");
                return;
            }

            if ((Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType)
                || Y9CommonEventConst.TENANT_SYSTEM_INITIALIZED.equals(eventType))
                && !Objects.equals(Y9Context.getSystemName(), msg.getEventTarget())) {
                // 对于非当前引入的系统的消息不处理
                return;
            }

            Y9EventCommon event = new Y9EventCommon();
            event.setEventType(msg.getEventType());
            event.setEventObject(msg.getEventObject());
            Y9Context.publishEvent(event);
            LOGGER.info("[common]将消息中间件发过来的消息转换成spring的事件后发送：{}", event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
