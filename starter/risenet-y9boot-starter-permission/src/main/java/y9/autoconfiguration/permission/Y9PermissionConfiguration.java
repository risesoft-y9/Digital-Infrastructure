package y9.autoconfiguration.permission;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.api.permission.PersonResourceApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.permission.PositionResourceApi;
import net.risesoft.api.permission.PositionRoleApi;
import net.risesoft.permission.aop.advice.HasAuthoritiesAdvice;
import net.risesoft.permission.aop.advice.HasPositionsAdvice;
import net.risesoft.permission.aop.advice.HasRolesAdvice;
import net.risesoft.permission.aop.advice.IsAuditManagerAdvice;
import net.risesoft.permission.aop.advice.IsSecurityManagerAdvice;
import net.risesoft.permission.aop.advice.IsSystemManagerAdvice;
import net.risesoft.permission.aop.advisor.HasAuthoritiesAdvisor;
import net.risesoft.permission.aop.advisor.HasPositionsAdvisor;
import net.risesoft.permission.aop.advisor.HasRolesAdvisor;
import net.risesoft.permission.aop.advisor.IsAuditManagerAdvisor;
import net.risesoft.permission.aop.advisor.IsSecurityManagerAdvisor;
import net.risesoft.permission.aop.advisor.IsSystemManagerAdvisor;
import net.risesoft.y9.Y9Context;

@Configuration
@ConditionalOnProperty(name = "y9.feature.permission.enabled", havingValue = "true", matchIfMissing = true)
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

    @Bean
    @ConditionalOnMissingBean(HasAuthoritiesAdvice.class)
    public HasAuthoritiesAdvice hasAuthoritiesAdvice(PersonResourceApi personResourceApi, PositionResourceApi positionResourceApi) {
        return new HasAuthoritiesAdvice(personResourceApi, positionResourceApi);
    }

    @Bean
    @ConditionalOnMissingBean(HasAuthoritiesAdvisor.class)
    public HasAuthoritiesAdvisor hasAuthoritiesAdvisor(HasAuthoritiesAdvice hasAuthoritiesAdvice) {
        HasAuthoritiesAdvisor bean = new HasAuthoritiesAdvisor();
        bean.setAdvice(hasAuthoritiesAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsAuditManagerAdvice.class)
    public IsAuditManagerAdvice isAuditManagerAdvice() {
        IsAuditManagerAdvice bean = new IsAuditManagerAdvice();
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsAuditManagerAdvisor.class)
    public IsAuditManagerAdvisor isAuditManagerAdvisor(IsAuditManagerAdvice isAuditManagerAdvice) {
        IsAuditManagerAdvisor bean = new IsAuditManagerAdvisor();
        bean.setAdvice(isAuditManagerAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsSecurityManagerAdvice.class)
    public IsSecurityManagerAdvice isSecurityManagerAdvice() {
        IsSecurityManagerAdvice bean = new IsSecurityManagerAdvice();
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsSecurityManagerAdvisor.class)
    public IsSecurityManagerAdvisor isSecurityManagerAdvisor(IsSecurityManagerAdvice isSecurityManagerAdvice) {
        IsSecurityManagerAdvisor bean = new IsSecurityManagerAdvisor();
        bean.setAdvice(isSecurityManagerAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsSystemManagerAdvice.class)
    public IsSystemManagerAdvice isSystemManagerAdvice() {
        IsSystemManagerAdvice bean = new IsSystemManagerAdvice();
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(IsSystemManagerAdvisor.class)
    public IsSystemManagerAdvisor isSystemManagerAdvisor(IsSystemManagerAdvice isSystemManagerAdvice) {
        IsSystemManagerAdvisor bean = new IsSystemManagerAdvisor();
        bean.setAdvice(isSystemManagerAdvice);
        return bean;
    }

}
