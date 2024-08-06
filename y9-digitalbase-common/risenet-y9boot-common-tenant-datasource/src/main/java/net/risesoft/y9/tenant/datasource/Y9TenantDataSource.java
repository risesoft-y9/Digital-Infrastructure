package net.risesoft.y9.tenant.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 多租户数据源
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class Y9TenantDataSource extends AbstractDataSource {

    private final DruidDataSource defaultDataSource;
    private final Y9TenantDataSourceLookup dataSourceLookup;

    public DruidDataSource determineTargetDataSource() {
        DruidDataSource dataSource = defaultDataSource;

        String lookupKey = Y9LoginUserHolder.getTenantId();
        if (StringUtils.hasText(lookupKey)) {
            DruidDataSource tenantDataSource = (DruidDataSource)this.dataSourceLookup.getDataSource(lookupKey);
            if (tenantDataSource == null) {
                LOGGER.warn("租户[{}]未租用系统[{}]，将使用默认数据源", lookupKey, this.dataSourceLookup.getSystemName());
            } else {
                dataSource = tenantDataSource;
            }
        } else {
            LOGGER.warn("当前线程中租户ID为空，将使用默认数据源");
        }

        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        DruidDataSource ds = determineTargetDataSource();
        return ds.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        DruidDataSource ds = determineTargetDataSource();
        return ds.getConnection(username, password);
    }

}
