package net.risesoft.y9public.service.tenant.impl;

import java.util.Objects;
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

import net.risesoft.enums.DataSourceTypeEnum;
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
    private final ConcurrentMap<String, Y9DataSource> y9DataSourceMap = Maps.newConcurrentMap();

    public Y9DataSourceServiceImpl(Y9DataSourceRepository datasourceRepository,
        Y9DataSourceManager y9DataSourceManager) {
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
        return datasourceRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(DataSourceErrorCodeEnum.DATA_SOURCE_NOT_FOUND, id));
    }

    @Override
    public DataSource getDataSource(String id) {
        DataSource dataSource = this.dataSourceMap.get(id);
        Y9DataSource queriedY9DataSource = this.getById(id);
        Y9DataSource cachedY9DataSource = y9DataSourceMap.get(id);

        if (isY9DataSourceModified(cachedY9DataSource, queriedY9DataSource)) {
            Integer type = queriedY9DataSource.getType();
            if (Objects.equals(type, DataSourceTypeEnum.JNDI.getValue())) {
                dataSource = this.dataSourceLookup.getDataSource(queriedY9DataSource.getJndiName());
            } else {
                // druid
                @SuppressWarnings("resource")
                DruidDataSource ds = new DruidDataSource();
                if (StringUtils.isNotBlank(queriedY9DataSource.getDriver())) {
                    ds.setDriverClassName(queriedY9DataSource.getDriver());
                }
                ds.setTestOnBorrow(true);
                ds.setTestOnReturn(true);
                ds.setTestWhileIdle(true);
                ds.setValidationQuery("SELECT 1 FROM DUAL");
                ds.setInitialSize(queriedY9DataSource.getInitialSize());
                ds.setMaxActive(queriedY9DataSource.getMaxActive());
                ds.setMinIdle(queriedY9DataSource.getMinIdle());
                ds.setUrl(queriedY9DataSource.getUrl());
                ds.setUsername(queriedY9DataSource.getUsername());
                ds.setPassword(Y9Base64Util.decode(queriedY9DataSource.getPassword()));

                dataSource = ds;
            }
            this.dataSourceMap.put(id, dataSource);
            this.y9DataSourceMap.put(id, queriedY9DataSource);
        }

        return dataSource;
    }

    private static boolean isY9DataSourceModified(Y9DataSource cachedY9DataSource, Y9DataSource queriedY9DataSource) {
        boolean modified = false;
        if (cachedY9DataSource == null) {
            modified = true;
        } else {
            if (Objects.equals(queriedY9DataSource.getType(), DataSourceTypeEnum.DRUID.getValue())) {
                if (!Objects.equals(cachedY9DataSource.getType(), DataSourceTypeEnum.DRUID.getValue())) {
                    modified = true;
                }
                if (!queriedY9DataSource.getJndiName().equals(cachedY9DataSource.getJndiName())) {
                    modified = true;
                }
            } else {
                // druid
                if (!queriedY9DataSource.getUrl().equals(cachedY9DataSource.getUrl())
                    || !queriedY9DataSource.getInitialSize().equals(cachedY9DataSource.getInitialSize())
                    || !queriedY9DataSource.getMaxActive().equals(cachedY9DataSource.getMaxActive())
                    || !queriedY9DataSource.getMinIdle().equals(cachedY9DataSource.getMinIdle())
                    || !queriedY9DataSource.getUsername().equals(cachedY9DataSource.getUsername())
                    || !queriedY9DataSource.getPassword().equals(cachedY9DataSource.getPassword())
                    || !queriedY9DataSource.getType().equals(cachedY9DataSource.getType())) {
                    modified = true;
                }
            }
        }
        return modified;
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
