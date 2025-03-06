package y9.autoconfiguration.log;

import java.util.concurrent.Executor;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.RequestContextFilter;

import com.alibaba.ttl.threadpool.TtlExecutors;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.FilterOrderConsts;
import net.risesoft.log.LogFilter;
import net.risesoft.log.aop.RiseLogAdvice;
import net.risesoft.log.aop.RiseLogAdvisor;
import net.risesoft.log.service.AccessLogReporter;
import net.risesoft.log.service.impl.AccessLogApiReporter;
import net.risesoft.log.service.impl.AccessLogKafkaReporter;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.log.Y9LogProperties;

/**
 * @author dzj
 * @author shidaobang
 */
@Configuration
@ConditionalOnProperty(name = "y9.feature.log.enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
@EnableConfigurationProperties({Y9Properties.class, Y9LogProperties.class})
public class Y9LogConfiguration {

    // https://github.com/spring-projects/spring-boot/issues/2637
    // https://github.com/spring-projects/spring-boot/issues/4331
    @Bean
    @ConditionalOnMissingFilterBean(RequestContextFilter.class)
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(RiseLogAdvice.class)
    @DependsOn({"y9Context"})
    public RiseLogAdvice riseLogAdvice(AccessLogReporter accessLogReporter) {
        return new RiseLogAdvice(accessLogReporter);
    }

    @Bean
    @ConditionalOnMissingBean(RiseLogAdvisor.class)
    public RiseLogAdvisor riseLogAdvisor(RiseLogAdvice riseLogAdvice) {
        RiseLogAdvisor bean = new RiseLogAdvisor();
        bean.setAdvice(riseLogAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Configuration
    @AutoConfigureAfter(KafkaAutoConfiguration.class)
    @ConditionalOnProperty(value = "y9.feature.log.reportMethod", havingValue = "kafka", matchIfMissing = true)
    @Slf4j
    static class Y9LogKafkaConfiguration {

        @Bean("y9KafkaTemplate")
        @ConditionalOnMissingBean(name = "y9KafkaTemplate")
        public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
            LOGGER.info("Y9LogKafkaConfiguration y9KafkaTemplate init ......");
            return new KafkaTemplate<>(kafkaProducerFactory);
        }

        @Bean
        public AccessLogReporter accessLogKafkaPusher(KafkaTemplate<String, Object> y9KafkaTemplate) {
            return new AccessLogKafkaReporter(y9KafkaTemplate);
        }

    }

    @Configuration
    @ConditionalOnProperty(value = "y9.feature.log.reportMethod", havingValue = "api")
    static class Y9LogApiConfiguration {

        @Bean
        public AccessLogReporter accessLogApiPusher(Y9Properties y9Properties) {
            return new AccessLogApiReporter(y9Properties);
        }

    }

    @Bean
    @ConditionalOnMissingBean(name = "y9LogFilter")
    public FilterRegistrationBean<LogFilter> y9LogFilter(AccessLogReporter accessLogReporter) {
        final FilterRegistrationBean<LogFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new LogFilter(accessLogReporter));
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/*");
        filterBean.setOrder(FilterOrderConsts.LOG_ORDER);

        return filterBean;
    }

    @Bean(name = {"y9ThreadPoolTaskExecutor"})
    @ConditionalOnMissingBean(name = "y9ThreadPoolTaskExecutor")
    public Executor y9ThreadPoolTaskExecutor(TaskExecutorBuilder builder) {
        ThreadPoolTaskExecutor taskExecutor = builder.build();
        // 核心线程数
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        // 最大线程数
        taskExecutor.setMaxPoolSize(20);
        // 配置队列大小
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("y9Log-");
        taskExecutor.initialize();
        return TtlExecutors.getTtlExecutor(taskExecutor);
    }
}
