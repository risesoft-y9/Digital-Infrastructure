package net.risesoft.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.boot.sql.DialectMapper;
import org.javers.spring.boot.sql.JaversSqlAutoConfiguration;
import org.javers.spring.boot.sql.JaversSqlProperties;
import org.javers.spring.jpa.TransactionalJpaJaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

import y9.autoconfiguration.jpa.JpaPublicConfiguration;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {JaversSqlProperties.class, JpaProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class, JpaPublicConfiguration.class})
@AutoConfigureBefore(JaversSqlAutoConfiguration.class)
@ComponentScan(
    basePackages = {"net.risesoft.repository", "net.risesoft.y9public.repository", "org.javers.spring.repository"})
@Slf4j
public class JaversSpringJpaApplicationConfig {

    @Autowired(required = false)
    private List<JaversBuilderPlugin> plugins = new ArrayList<>();

    @Bean
    public DialectName y9javersSqlDialectName(
        @Qualifier(value = "rsPublicEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        Dialect hibernateDialect = sessionFactory.getJdbcServices().getDialect();
        LOGGER.info("detected Hibernate dialect: {}", hibernateDialect.getClass().getSimpleName());
        DialectMapper dialectMapper = new DialectMapper();
        return dialectMapper.map(hibernateDialect);
    }

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers(JaversSqlRepository sqlRepository,
        @Qualifier("rsPublicTransactionManager") PlatformTransactionManager transactionManager,
        JaversSqlProperties javersSqlProperties) {
        JaversBuilder javersBuilder = TransactionalJpaJaversBuilder.javers().withTxManager(transactionManager)
            .registerJaversRepository(sqlRepository)
            .withObjectAccessHook(javersSqlProperties.createObjectAccessHookInstance())
            .withProperties(javersSqlProperties);

        plugins.forEach(plugin -> plugin.beforeAssemble(javersBuilder));

        return javersBuilder.build();
    }

    @Bean(name = "JaversSqlRepositoryFromStarter")
    @ConditionalOnMissingBean
    public JaversSqlRepository javersSqlRepository(ConnectionProvider connectionProvider,
        JaversSqlProperties javersSqlProperties, DialectName y9javersSqlDialectName) {
        return SqlRepositoryBuilder.sqlRepository().withSchema(javersSqlProperties.getSqlSchema())
            .withConnectionProvider(connectionProvider).withDialect(y9javersSqlDialectName)
            .withSchemaManagementEnabled(javersSqlProperties.isSqlSchemaManagementEnabled())
            .withGlobalIdCacheDisabled(javersSqlProperties.isSqlGlobalIdCacheDisabled())
            .withGlobalIdTableName(javersSqlProperties.getSqlGlobalIdTableName())
            .withCommitTableName(javersSqlProperties.getSqlCommitTableName())
            .withSnapshotTableName(javersSqlProperties.getSqlSnapshotTableName())
            .withCommitPropertyTableName(javersSqlProperties.getSqlCommitPropertyTableName()).build();
    }

    @Bean(name = "JpaHibernateConnectionProvider")
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider() {
        return new Y9JpaHibernateConnectionProvider();
    }
}
