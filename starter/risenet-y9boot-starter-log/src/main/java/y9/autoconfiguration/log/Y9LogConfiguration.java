package y9.autoconfiguration.log;

import java.util.concurrent.Executor;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.ttl.threadpool.TtlExecutors;

import net.risesoft.log.aop.RiseLogAdvice;
import net.risesoft.log.aop.RiseLogAdvisor;
import net.risesoft.y9.Y9Context;

/**
 *
 * @author dzj
 * @author shidaobang
 */
@Configuration
@ConditionalOnProperty(name = "y9.feature.log.enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
public class Y9LogConfiguration {

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
    public RiseLogAdvice riseLogAdvice() {
        RiseLogAdvice bean = new RiseLogAdvice();
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(RiseLogAdvisor.class)
    public RiseLogAdvisor riseLogAdvisor() {
        RiseLogAdvisor bean = new RiseLogAdvisor();
        bean.setAdvice(riseLogAdvice());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean("y9KafkaTemplate")
    @ConditionalOnProperty(name = "y9.app.log.kafkaEnabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean(name = {"y9ThreadPoolTaskExecutor"})
    @ConditionalOnMissingBean(name = "y9ThreadPoolTaskExecutor")
    public Executor y9ThreadPoolTaskExecutor(TaskExecutorBuilder builder) {
        ThreadPoolTaskExecutor taskExecutor = builder.build();
        taskExecutor.setMaxPoolSize(4);
        // 核心线程数
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        // 最大线程数
        taskExecutor.setMaxPoolSize(5);
        // 配置队列大小
        taskExecutor.setQueueCapacity(50);
        taskExecutor.initialize();
        return TtlExecutors.getTtlExecutor(taskExecutor);
    }

}
