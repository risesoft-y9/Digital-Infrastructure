package org.apereo.cas.web.y9.config;

import javax.sql.DataSource;

import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.Y9KeyValue;
import org.apereo.cas.services.Y9LoginUser;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.integration.transaction.PseudoTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.val;

@Configuration(proxyBeanMethods = false)
public class Y9JpaConfiguration {
    private static final BeanCondition CONDITION =
        BeanCondition.on("cas.service-registry.jpa.enabled").isTrue().evenIfMissing();
    private static final BeanCondition CONDITION2 =
        BeanCondition.on("cas.ticket.registry.jpa.enabled").isTrue().evenIfMissing();

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public JpaPersistenceProviderConfigurer
        jpaServicePersistenceProviderConfigurer(final ConfigurableApplicationContext applicationContext) {
        return BeanSupplier.of(JpaPersistenceProviderConfigurer.class)
            .when(CONDITION.given(applicationContext.getEnvironment())).supply(() -> context -> {
                String s1 = JpaRegisteredServiceEntity.class.getName();
                String s2 = Y9LoginUser.class.getName();
                String s3 = Y9User.class.getName();
                String s4 = Y9KeyValue.class.getName();
                val entities = CollectionUtils.wrapList(s1, s2, s3, s4);
                context.getIncludeEntityClasses().addAll(entities);
            }).otherwiseProxy().get();
    }

    @Primary
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PlatformTransactionManager jdbcLockTransactionManager(
        final ConfigurableApplicationContext applicationContext,
        @Qualifier("dataSourceTicket") DataSource dataSourceTicket) {
        return BeanSupplier.of(PlatformTransactionManager.class)
            .when(CONDITION2.given(applicationContext.getEnvironment()))
            .supply(() -> new DataSourceTransactionManager(dataSourceTicket)).otherwise(PseudoTransactionManager::new)
            .get();
    }

    /**
     * @Bean
     * @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT) public BeanContainer<String> jpaServicePackagesToScan() {
     *                         String s1 = JpaRegisteredServiceEntity.class.getPackage().getName(); String s2 =
     *                         Y9LoginUser.class.getPackage().getName(); String s3 =
     *                         Y9User.class.getPackage().getName(); return BeanContainer.of(CollectionUtils.wrapSet(s1,
     *                         s2, s3)); }
     */

}
