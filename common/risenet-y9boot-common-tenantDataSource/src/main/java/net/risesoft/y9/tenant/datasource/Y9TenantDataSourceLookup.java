package net.risesoft.y9.tenant.datasource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.y9.util.base64.Y9Base64Util;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Slf4j
@RequiredArgsConstructor
public class Y9TenantDataSourceLookup implements DataSourceLookup {

    private final HikariDataSource publicDataSource;
    private final String systemName;

    /** 已加载的租户id和数据源Map：目前包括默认租户和租用了当前系统的租户 */
    private final Map<String, HikariDataSource> loadedTenantIdDataSourceMap = new ConcurrentHashMap<>();
    private final JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
    private boolean loaded = false;

    private void createDefaultTenantDataSource(JdbcTemplate publicJdbcTemplate) {
        List<Map<String, Object>> defaultTenant = publicJdbcTemplate.queryForList(
            "SELECT ID, DEFAULT_DATA_SOURCE_ID FROM Y9_COMMON_TENANT WHERE ID=?", DefaultIdConsts.TENANT_ID);
        List<Map<String, Object>> defaultDataSource = publicJdbcTemplate
            .queryForList("SELECT * FROM Y9_COMMON_DATASOURCE T WHERE T.ID = ?", DefaultIdConsts.DATASOURCE_ID);
        if (!defaultTenant.isEmpty() && !defaultDataSource.isEmpty()) {
            createOrUpdateDataSource(defaultDataSource.get(0), null, DefaultIdConsts.TENANT_ID);
        }
    }

    private void createOrUpdateDataSource(Map<String, Object> record, HikariDataSource ds, String tenantId) {
        Integer type = Integer.valueOf(record.get("TYPE").toString());
        String jndiName = (String)record.get("JNDI_NAME");
        // jndi
        if (type == 1) {
            try {
                if (ds == null) {
                    ds = (HikariDataSource)this.jndiDataSourceLookup.getDataSource(jndiName);
                    this.loadedTenantIdDataSourceMap.put(tenantId, ds);
                } else {
                    // 用旧的还是新的？
                    // ds = (HikariDataSource) this.jndiDataSourceLookup.getDataSource(jndiName);
                    // this.dataSources.put(tenantId, ds);
                }
            } catch (DataSourceLookupFailureException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else { // hikari
            String url = (String)record.get("URL");
            String username = (String)record.get("USERNAME");
            String password = (String)record.get("PASSWORD");
            String driver = record.get("DRIVER") != null ? (String)record.get("DRIVER") : "";
            password = Y9Base64Util.decode(password);

            Integer initialSize = Integer.valueOf(record.get("INITIAL_SIZE").toString());
            Integer maxActive = Integer.valueOf(record.get("MAX_ACTIVE").toString());
            Integer minIdle = Integer.valueOf(record.get("MIN_IDLE").toString());

            if (ds == null) {
                ds = new HikariDataSource();
                ds.setMaximumPoolSize(maxActive);
                ds.setMinimumIdle(minIdle);
                if (driver.length()>0) {
                    ds.setDriverClassName(driver);
                }
                
                ds.setJdbcUrl(url);
                ds.setUsername(username);
                ds.setPassword(password);
                this.loadedTenantIdDataSourceMap.put(tenantId, ds);
            } else {
                // 可能连接池的参数调整了
                // url,username,password等属性在HikariDataSource初始化完成后不允许更改，否则抛出异常?
                boolean needCreate = false;
                if (!ds.getJdbcUrl().equals(url)) {
                    needCreate = true;
                }
                if (!ds.getUsername().equals(username)) {
                    needCreate = true;
                }
                if (!ds.getPassword().equals(password)) {
                    needCreate = true;
                }

                if (needCreate) {
                    ds = new HikariDataSource();
                    ds.setMaximumPoolSize(maxActive);
                    ds.setMinimumIdle(minIdle);
                    if (!"".equals(driver)) {
                        ds.setDriverClassName(driver);
                    }                    
                    ds.setJdbcUrl(url);
                    ds.setUsername(username);
                    ds.setPassword(password);
                } else {
                	ds.setMaximumPoolSize(maxActive);
                    ds.setMinimumIdle(minIdle);
                }
                this.loadedTenantIdDataSourceMap.put(tenantId, ds);
            }
        }
    }

    @Override
    public DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException {
        if (!loaded) {
            loadDataSources();
            loaded = true;
        }

        Assert.notNull(dataSourceName, "DataSource name must not be null");
        return this.loadedTenantIdDataSourceMap.get(dataSourceName);
    }

    /**
     * Y9TenantHibernateInfoHolder.schemaUpdate()用到本方法
     *
     * @return
     */
    public Map<String, HikariDataSource> getDataSources() {
        if (!loaded) {
            loadDataSources();
            loaded = true;
        }
        return Collections.unmodifiableMap(this.loadedTenantIdDataSourceMap);
    }

    public DataSource getPublicDataSource() {
        return publicDataSource;
    }

    public String getSystemName() {
        return systemName;
    }

    /**
     * 加载数据源 在系统启动时或收到同步租户数据源的事件时调用
     */
    public void loadDataSources() {
        JdbcTemplate publicJdbcTemplate = new JdbcTemplate(this.publicDataSource);

        // 1 移除不存在的租户的连接池
        List<Map<String, Object>> allTenants =
            publicJdbcTemplate.queryForList("select ID, DEFAULT_DATA_SOURCE_ID FROM Y9_COMMON_TENANT");
        Set<String> loadedTenantIdSet = this.loadedTenantIdDataSourceMap.keySet();
        for (String loadedTenantId : loadedTenantIdSet) {
            boolean removed = true;
            for (Map<String, Object> tenant : allTenants) {
                String tenantId = (String)tenant.get("ID");
                if (tenantId.equals(loadedTenantId)) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
            	HikariDataSource ds = this.loadedTenantIdDataSourceMap.get(loadedTenantId);
                this.loadedTenantIdDataSourceMap.remove(loadedTenantId);
                ds.close();
            }
        }

        // 2 重新设置租户的连接池
        List<Map<String, Object>> systems =
            publicJdbcTemplate.queryForList("SELECT ID FROM Y9_COMMON_SYSTEM WHERE NAME=?", this.systemName);

        // 2.1 系统存在(已在开源内核的应用系统管理添加系统),重新设置租户的连接池
        if (systems.size() > 0) {
            Map<String, Object> systemMap = systems.get(0);
            String systemId = (String)systemMap.get("ID");
            List<Map<String, Object>> tenantSystems = publicJdbcTemplate.queryForList(
                "SELECT TENANT_ID, TENANT_DATA_SOURCE FROM Y9_COMMON_TENANT_SYSTEM WHERE SYSTEM_ID = ?", systemId);

            // 2.1.1 有租户租用系统
            if (!tenantSystems.isEmpty()) {
                Set<String> systemTenantIds = new HashSet<>();
                for (Map<String, Object> tenantSystem : tenantSystems) {
                    String tenantId = (String)tenantSystem.get("TENANT_ID");
                    HikariDataSource ds = this.loadedTenantIdDataSourceMap.get(tenantId);
                    systemTenantIds.add(tenantId);

                    String tenantDataSource = (String)tenantSystem.get("TENANT_DATA_SOURCE");
                    List<Map<String, Object>> dataSources = publicJdbcTemplate
                        .queryForList("SELECT * FROM Y9_COMMON_DATASOURCE WHERE ID = ?", tenantDataSource);
                    if (!dataSources.isEmpty()) {
                        Map<String, Object> dsMap = dataSources.get(0);
                        createOrUpdateDataSource(dsMap, ds, tenantId);
                    }
                }
                // FIXME 这段代码有待优化
                /*for (Map<String, Object> tenant : allTenants) {
                    String tenantId = (String)tenant.get("id");
                    if (systemTenantIds.contains(tenantId)) {
                        // 如果给租户租用系统时指定了数据源则配置优先
                        continue;
                    }
                
                    HikariDataSource ds = this.dataSources.get(tenantId);
                    String defaultDataSourceId = (String)tenant.get("DEFAULT_DATA_SOURCE_ID");
                    if (StringUtils.isEmpty(defaultDataSourceId)) {
                        Assert.notNull(defaultDataSourceId, "tenant defaultDataSourceId must not be null");
                    }
                
                    List<Map<String, Object>> list3 = publicJdbcTemplate.queryForList("select * from Y9_COMMON_DATASOURCE t where t.ID = ?", defaultDataSourceId);
                    if (list3.size() > 0) {
                        Map<String, Object> dsMap = list3.get(0);
                        createOrUpdateDataSource(dsMap, ds, tenantId);
                    }
                }*/
            } else {
                // 2.1.1 没有租户租用系统,设置默认租户的连接池
                createDefaultTenantDataSource(publicJdbcTemplate);
            }
        } else {
            // 2.2 系统不存在(未在开源内核的应用系统管理添加系统),设置默认租户的连接池
            createDefaultTenantDataSource(publicJdbcTemplate);
        }

        // 3 初始化租户的数据库连接池
        Collection<HikariDataSource> hikariDataSources = loadedTenantIdDataSourceMap.values();
        if (!hikariDataSources.isEmpty()) {
            for (HikariDataSource ds : hikariDataSources) {
                try {
                    //ds.init();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
    }
}
