package net.risesoft.y9.tenant.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Slf4j
public class Y9TenantDataSource extends AbstractDataSource {

    private DruidDataSource defaultDataSource;

    private DataSourceLookup dataSourceLookup;

    public DruidDataSource determineTargetDataSource() {
        String lookupKey = Y9LoginUserHolder.getTenantId();
        if (StringUtils.hasText(lookupKey)) {
            DruidDataSource dataSource = (DruidDataSource)this.dataSourceLookup.getDataSource(lookupKey);
            if (dataSource == null) {
                LOGGER.error("未找到租户【" + lookupKey + "】的数据源。");
                dataSource = this.defaultDataSource;
            }
            if (dataSource == null) {
                throw new IllegalStateException("没设置缺省数据源 defaultDataSource!!!");
            }
            return dataSource;
        } else {
            LOGGER.error("当前线程中租户ID为空!!!");
            // throw new IllegalStateException("当前线程中租户ID为空!!!");
            return this.defaultDataSource;
        }

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

    public DataSourceLookup getDataSourceLookup() {
        return dataSourceLookup;
    }

    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = dataSourceLookup;
    }

    public void setDefaultDataSource(DruidDataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

}
