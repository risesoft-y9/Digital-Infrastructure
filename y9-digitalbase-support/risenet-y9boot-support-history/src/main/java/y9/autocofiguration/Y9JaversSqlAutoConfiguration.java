package y9.autocofiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;

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
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect;
import org.javers.spring.boot.sql.DialectMapper;
import org.javers.spring.boot.sql.JaversSqlAutoConfiguration;
import org.javers.spring.boot.sql.JaversSqlProperties;
import org.javers.spring.jpa.TransactionalJpaJaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.JaversProperties;
import net.risesoft.config.Y9JpaHibernateConnectionProvider;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9StringUtil;

import y9.autoconfiguration.jpa.JpaPublicConfiguration;

@ConditionalOnProperty(name = "javers.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfiguration(before = JaversSqlAutoConfiguration.class,
    after = {HibernateJpaAutoConfiguration.class, JpaPublicConfiguration.class})
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {JaversSqlProperties.class, JpaProperties.class, JaversProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
@Slf4j
public class Y9JaversSqlAutoConfiguration {

    @Autowired(required = false)
    private List<JaversBuilderPlugin> plugins = new ArrayList<>();

    @Bean
    public DialectName javersSqlDialectName(
        @Qualifier(value = "rsPublicEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        JaversProperties javersProperties) {
        if (JaversProperties.Dialect.AUTO.equals(javersProperties.getDialect())) {
            SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
            Dialect hibernateDialect = sessionFactory.getJdbcServices().getDialect();
            LOGGER.info("detected Hibernate dialect: {}", hibernateDialect.getClass().getSimpleName());
            DialectMapper dialectMapper = new DialectMapper();
            return dialectMapper.map(hibernateDialect);
        }
        return DialectName.valueOf(javersProperties.getDialect().name());
    }

    @Bean(name = "JaversSqlRepositoryFromStarter")
    @ConditionalOnMissingBean
    public JaversSqlRepository javersSqlRepository(ConnectionProvider connectionProvider,
        JaversSqlProperties javersSqlProperties, DialectName y9javersSqlDialectName) {
        return SqlRepositoryBuilder.sqlRepository()
            .withSchema(javersSqlProperties.getSqlSchema())
            .withConnectionProvider(connectionProvider)
            .withDialect(y9javersSqlDialectName)
            .withSchemaManagementEnabled(javersSqlProperties.isSqlSchemaManagementEnabled())
            .withGlobalIdCacheDisabled(javersSqlProperties.isSqlGlobalIdCacheDisabled())
            .withGlobalIdTableName(javersSqlProperties.getSqlGlobalIdTableName())
            .withCommitTableName(javersSqlProperties.getSqlCommitTableName())
            .withSnapshotTableName(javersSqlProperties.getSqlSnapshotTableName())
            .withCommitPropertyTableName(javersSqlProperties.getSqlCommitPropertyTableName())
            .build();
    }

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers(JaversSqlRepository sqlRepository,
        @Qualifier("rsPublicTransactionManager") PlatformTransactionManager transactionManager,
        JaversSqlProperties javersSqlProperties) {
        JaversBuilder javersBuilder = TransactionalJpaJaversBuilder.javers()
            .withTxManager(transactionManager)
            .registerJaversRepository(sqlRepository)
            .withObjectAccessHook(javersSqlProperties.createObjectAccessHookInstance())
            .withProperties(javersSqlProperties);

        plugins.forEach(plugin -> plugin.beforeAssemble(javersBuilder));

        return javersBuilder.build();
    }

    @Bean
    public AuthorProvider authorProvider() {
        return () -> {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (userInfo != null) {
                return userInfo.getLoginName() + "@" + userInfo.getTenantShortName();
            }
            return "";
        };
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                Map<String, String> map = new HashMap<String, String>();
                String personId = Y9LoginUserHolder.getPersonId();
                String deptId = Y9LoginUserHolder.getDeptId();
                String tenantId = Y9LoginUserHolder.getTenantId();
                map.put("ids", Y9StringUtil.format("personId:{},deptId:{},tenantId:{}", personId, deptId, tenantId));
                String userHostIp = "";
                try {
                    ServletRequestAttributes sra =
                        (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                    if (sra != null) {
                        HttpServletRequest request = sra.getRequest();
                        userHostIp = Y9Context.getIpAddr(request);
                        if (userHostIp != null && userHostIp.contains(":")) {
                            userHostIp = "127.0.0.1";
                        }
                        map.put("hostIp", userHostIp);
                    }
                } catch (Exception e) {
                }
                return map;
            }
        };
    }

    @Bean(name = "JpaHibernateConnectionProvider")
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider() {
        return new Y9JpaHibernateConnectionProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider,
        CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true",
        matchIfMissing = true)
    public JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers,
        AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
