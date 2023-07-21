package y9.autoconfiguration.dbcomment;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;

import y9.dbcomment.Y9DatabaseCommentEndpoint;

@Configuration(proxyBeanMethods = false)
public class Y9DbCommentConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public Y9DatabaseCommentEndpoint y9DatabaseCommentEndpoint(@Qualifier("y9PublicDS") DataSource y9PublicDs, @Qualifier("y9TenantDataSource") Y9TenantDataSource y9TenantDataSource) {
        return new Y9DatabaseCommentEndpoint(y9PublicDs, y9TenantDataSource);
    }

}
