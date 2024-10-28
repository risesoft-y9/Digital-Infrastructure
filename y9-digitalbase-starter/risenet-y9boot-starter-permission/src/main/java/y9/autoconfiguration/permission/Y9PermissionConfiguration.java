package y9.autoconfiguration.permission;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.api.platform.permission.PersonResourceApi;
import net.risesoft.api.platform.permission.PersonRoleApi;
import net.risesoft.api.platform.permission.PositionResourceApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.permission.aop.advice.HasAuthoritiesAdvice;
import net.risesoft.permission.aop.advice.HasPositionsAdvice;
import net.risesoft.permission.aop.advice.HasRolesAdvice;
import net.risesoft.permission.aop.advice.IsAnyManagerAdvice;
import net.risesoft.permission.aop.advisor.HasAuthoritiesAdvisor;
import net.risesoft.permission.aop.advisor.HasPositionsAdvisor;
import net.risesoft.permission.aop.advisor.HasRolesAdvisor;
import net.risesoft.permission.aop.advisor.IsAnyManagerAdvisor;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.permission.Y9PermissionProperties;

@Configuration
@ConditionalOnProperty(name = "y9.feature.permission.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Y9PermissionProperties.class)
public class Y9PermissionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "y9.feature.permission.has-roles.enabled", havingValue = "true",
        matchIfMissing = true)
    public static class hasRolesConfiguration {
        @Bean
        @ConditionalOnMissingBean(HasRolesAdvice.class)
        public HasRolesAdvice hasRolesAdvice(PersonRoleApi personRoleApi, PositionRoleApi positionRoleApi) {
            return new HasRolesAdvice(personRoleApi, positionRoleApi);
        }

        @Bean
        @ConditionalOnMissingBean(HasRolesAdvisor.class)
        public HasRolesAdvisor hasRolesAdvisor(HasRolesAdvice hasRolesAdvice) {
            HasRolesAdvisor bean = new HasRolesAdvisor();
            bean.setAdvice(hasRolesAdvice);
            return bean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "y9.feature.permission.has-positions.enabled", havingValue = "true",
        matchIfMissing = true)
    public static class hasPositionsConfiguration {
        @Bean
        @ConditionalOnMissingBean(HasPositionsAdvice.class)
        public HasPositionsAdvice hasPositionsAdvice() {
            HasPositionsAdvice bean = new HasPositionsAdvice();
            return bean;
        }

        @Bean
        @ConditionalOnMissingBean(HasPositionsAdvisor.class)
        public HasPositionsAdvisor hasPositionsAdvisor(HasPositionsAdvice hasPositionsAdvice) {
            HasPositionsAdvisor bean = new HasPositionsAdvisor();
            bean.setAdvice(hasPositionsAdvice);
            return bean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "y9.feature.permission.has-authorities.enabled", havingValue = "true",
        matchIfMissing = true)
    public static class hasAuthoritiesConfiguration {
        @Bean
        @ConditionalOnMissingBean(HasAuthoritiesAdvice.class)
        public HasAuthoritiesAdvice hasAuthoritiesAdvice(PersonResourceApi personResourceApi,
            PositionResourceApi positionResourceApi) {
            return new HasAuthoritiesAdvice(personResourceApi, positionResourceApi);
        }

        @Bean
        @ConditionalOnMissingBean(HasAuthoritiesAdvisor.class)
        public HasAuthoritiesAdvisor hasAuthoritiesAdvisor(HasAuthoritiesAdvice hasAuthoritiesAdvice) {
            HasAuthoritiesAdvisor bean = new HasAuthoritiesAdvisor();
            bean.setAdvice(hasAuthoritiesAdvice);
            return bean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "y9.feature.permission.is-any-manager.enabled", havingValue = "true",
        matchIfMissing = true)
    public static class isAnyManagerConfiguration {
        @Bean
        @ConditionalOnMissingBean(IsAnyManagerAdvice.class)
        public IsAnyManagerAdvice isManagerAdvice() {
            return new IsAnyManagerAdvice();
        }

        @Bean
        @ConditionalOnMissingBean(IsAnyManagerAdvisor.class)
        public IsAnyManagerAdvisor isManagerAdvisor(IsAnyManagerAdvice isAnyManagerAdvice) {
            IsAnyManagerAdvisor bean = new IsAnyManagerAdvisor();
            bean.setAdvice(isAnyManagerAdvice);
            return bean;
        }
    }

}
