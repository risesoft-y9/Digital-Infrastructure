package y9;

import lombok.val;
import org.apereo.cas.config.CasJpaServiceRegistryAutoConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.Y9LoginUser;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// CasJpaServiceRegistryAutoConfiguration
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableTransactionManagement(proxyTargetClass = false)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.ServiceRegistry, module = "jpa")
@AutoConfiguration(before = {CasJpaServiceRegistryAutoConfiguration.class})
public class Y9AutoConfigJpa {
    private static final BeanCondition CONDITION =
            BeanCondition.on("cas.service-registry.jpa.enabled").isTrue().evenIfMissing();

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public JpaPersistenceProviderConfigurer jpaServicePersistenceProviderConfigurer(final ConfigurableApplicationContext applicationContext) {
        return BeanSupplier.of(JpaPersistenceProviderConfigurer.class)
                .when(CONDITION.given(applicationContext.getEnvironment())).supply(() -> context -> {
                    String s1 = JpaRegisteredServiceEntity.class.getName();
                    String s2 = Y9LoginUser.class.getName();
                    String s3 = Y9User.class.getName();
                    val entities = CollectionUtils.wrapList(s1, s2, s3);
                    context.getIncludeEntityClasses().addAll(entities);
                }).otherwiseProxy().get();
    }
}
