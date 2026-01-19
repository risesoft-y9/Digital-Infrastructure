package net.risesoft.liquibase;

import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

/**
 * @author shidaobang
 * @date 2023/10/18
 * @since 9.6.3
 */
@RequiredArgsConstructor
@Slf4j
public class Y9MultiTenantSpringLiquibase {

    private final Y9LiquibaseProperties properties;
    private final ResourceLoader resourceLoader;

    public void update(HikariDataSource dataSource) {
        try {
            SpringLiquibase liquibase =
                LiquibaseUtil.getSpringLiquibase(dataSource, this.properties, this.resourceLoader, true);
            liquibase.afterPropertiesSet();
        } catch (LiquibaseException e) {
            LOGGER.warn("更新表结构异常", e);
        }
    }

}
