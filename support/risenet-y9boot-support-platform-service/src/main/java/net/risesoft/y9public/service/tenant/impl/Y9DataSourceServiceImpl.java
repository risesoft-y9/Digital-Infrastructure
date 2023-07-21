package net.risesoft.y9public.service.tenant.impl;

import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;

import net.risesoft.exception.DataSourceErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.repository.tenant.Y9DataSourceRepository;
import net.risesoft.y9public.service.tenant.Y9DataSourceService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "dataSourceService")
public class Y9DataSourceServiceImpl implements Y9DataSourceService {

    private final Y9DataSourceRepository datasourceRepository;
    private final Y9DataSourceManager y9DataSourceManager;

    private final DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    private final ConcurrentMap<String, DataSource> dataSourceMap = Maps.newConcurrentMap();
    private final ConcurrentMap<String, Y9DataSource> dsMap = Maps.newConcurrentMap();

    public Y9DataSourceServiceImpl(Y9DataSourceRepository datasourceRepository, Y9DataSourceManager y9DataSourceManager) {
        this.datasourceRepository = datasourceRepository;
        this.y9DataSourceManager = y9DataSourceManager;
    }

    @Override
    public String buildTenantDataSourceName(String shortName, Integer tenantType) {
        return y9DataSourceManager.buildTenantDataSourceName(shortName, tenantType);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource createTenantDefaultDataSource(String dbName) {
        return y9DataSourceManager.createTenantDefaultDataSource(dbName);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource createTenantDefaultDataSource(String shortName, Integer tenantType, String systemName) {
        String dataSourceName = buildTenantDataSourceName(shortName, tenantType);
        String dbName = dataSourceName;
        if (StringUtils.isNotBlank(systemName)) {
            dbName = dataSourceName + "_" + systemName;
        }
        return createTenantDefaultDataSource(dbName);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9DataSourceManager.delete(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void dropTenantDefaultDataSource(String dataSourceId, String dbName) {
        y9DataSourceManager.dropTenantDefaultDataSource(dataSourceId, dbName);
    }

    @Override
    public Y9DataSource findById(String id) {
        return datasourceRepository.findById(id).orElse(null);
    }

    @Override
    public Y9DataSource findByJndiName(String jndiName) {
        return datasourceRepository.findByJndiName(jndiName);
    }

    @Override
    public Y9DataSource getById(String id) {
        return datasourceRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(DataSourceErrorCodeEnum.DATA_SOURCE_NOT_FOUND, id));
    }

    @Override
    public DataSource getDataSource(String id) {
        DataSource dataSource = this.dataSourceMap.get(id);
        Y9DataSource df = this.getById(id);
        Y9DataSource df1 = dsMap.get(id);
        boolean modified = false;
        if (df1 == null) {
            modified = true;
        } else {
            if (df != null) {
                // jndi
                if (df.getType() == 1) {
                    if (df1.getType() != 1) {
                        modified = true;
                    }
                    if (!df.getJndiName().equals(df1.getJndiName())) {
                        modified = true;
                    }
                } else {
                    // druid
                    if (!df.getUrl().equals(df1.getUrl())) {
                        modified = true;
                    }
                    if (!df.getInitialSize().equals(df1.getInitialSize())) {
                        modified = true;
                    }
                    if (!df.getMaxActive().equals(df1.getMaxActive())) {
                        modified = true;
                    }
                    if (!df.getMinIdle().equals(df1.getMinIdle())) {
                        modified = true;
                    }
                    if (!df.getUsername().equals(df1.getUsername())) {
                        modified = true;
                    }
                    if (!df.getPassword().equals(df1.getPassword())) {
                        modified = true;
                    }
                    if (!df.getType().equals(df1.getType())) {
                        modified = true;
                    } else {
                        if (df.getType() == 1 && !df.getJndiName().equals(df1.getJndiName())) {
                            modified = true;
                        }
                    }
                }
            }
        }
        if (modified) {
            Integer type = df.getType();
            if (type == 1) { // jndi
                dataSource = this.dataSourceLookup.getDataSource(df.getJndiName());
            } else { // druid
                @SuppressWarnings("resource")
                DruidDataSource ds = new DruidDataSource();
                if (StringUtils.isNotBlank(df.getDriver())) {
                    ds.setDriverClassName(df.getDriver());
                }
                ds.setTestOnBorrow(true);
                ds.setTestOnReturn(true);
                ds.setTestWhileIdle(true);
                ds.setValidationQuery("SELECT 1 FROM DUAL");
                ds.setInitialSize(df.getInitialSize());
                ds.setMaxActive(df.getMaxActive());
                ds.setMinIdle(df.getMinIdle());
                ds.setUrl(df.getUrl());
                ds.setUsername(df.getUsername());
                String pwd = df.getPassword();
                ds.setPassword(Y9Base64Util.decode(pwd));

                dataSource = ds;
            }
            this.dataSourceMap.put(id, dataSource);
            this.dsMap.put(id, df);
        }

        return dataSource;
    }

    @Override
    public Page<Y9DataSource> page(int page, int rows) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows);
        return datasourceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource save(Y9DataSource y9DataSource) {
        return y9DataSourceManager.save(y9DataSource);
    }

}
