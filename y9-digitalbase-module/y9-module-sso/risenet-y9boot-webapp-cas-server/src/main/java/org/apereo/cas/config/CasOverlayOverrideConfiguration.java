package org.apereo.cas.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class CasOverlayOverrideConfiguration {

    /*@Configuration
    public static class Y9JpaConfig {
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
                        String s4 = Y9KeyValue.class.getName();
                        val entities = CollectionUtils.wrapList(s1, s2, s3, s4);
                        context.getIncludeEntityClasses().addAll(entities);
                    }).otherwiseProxy().get();
        }
    }*/

}
