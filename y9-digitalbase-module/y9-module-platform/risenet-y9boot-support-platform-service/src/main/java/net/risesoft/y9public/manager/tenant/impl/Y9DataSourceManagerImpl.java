package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.exception.DataSourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.db.DbType;
import net.risesoft.y9.db.DbUtil;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.manager.database.AbstractDatabaseManager;
import net.risesoft.y9public.manager.database.CreatedDataSource;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.repository.tenant.Y9DataSourceRepository;

import cn.hutool.core.util.RandomUtil;

/**
 * 数据源 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Slf4j
public class Y9DataSourceManagerImpl implements Y9DataSourceManager {

    private final JdbcTemplate jdbcTemplate4Public;
    private final Y9DataSourceRepository y9DataSourceRepository;

    private final List<AbstractDatabaseManager> databaseManagerList;

    public Y9DataSourceManagerImpl(
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        Y9PlatformProperties y9PlatformProperties,
        Y9DataSourceRepository y9DataSourceRepository,
        List<AbstractDatabaseManager> databaseManagerList) {
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.y9DataSourceRepository = y9DataSourceRepository;
        this.databaseManagerList = databaseManagerList;
    }

    @Override
    public Y9DataSource getById(String id) {
        return y9DataSourceRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(DataSourceErrorCodeEnum.DATA_SOURCE_NOT_FOUND, id));
    }

    private String buildDataSourceName(String tenantShortName, String systemName) {
        String dataSourceName = tenantShortName;
        if (!"default".equals(tenantShortName)) {
            dataSourceName = "yt_" + tenantShortName + "_" + RandomUtil.randomStringUpper(4);
        }
        if (StringUtils.isNotBlank(systemName)) {
            dataSourceName = dataSourceName + "_" + systemName;
        }
        return dataSourceName;
    }

    @Override
    public Y9DataSource createDataSourceIfNotExists(String tenantShortName, String systemName, String specifyId) {
        String dataSourceName = this.buildDataSourceName(tenantShortName, systemName);
        return this.createDataSourceIfNotExists(dataSourceName, specifyId);
    }

    @Override
    public Y9DataSource createDataSourceIfNotExists(String dbName, String specifyId) {
        if (StringUtils.isNotBlank(specifyId)) {
            Optional<Y9DataSource> y9DataSourceOptional = y9DataSourceRepository.findById(specifyId);
            if (y9DataSourceOptional.isPresent()) {
                return y9DataSourceOptional.get();
            }
        }

        Optional<Y9DataSource> y9DataSourceOptional = y9DataSourceRepository.findByJndiName(dbName);
        if (y9DataSourceOptional.isPresent()) {
            return y9DataSourceOptional.get();
        }

        DruidDataSource dds = (DruidDataSource)jdbcTemplate4Public.getDataSource();
        DbType dbType = DbUtil.getDbType(jdbcTemplate4Public.getDataSource());

        CreatedDataSource createdDataSource = null;
        AbstractDatabaseManager databaseManager =
            databaseManagerList.stream().filter(dm -> dm.support(dbType)).findFirst().orElse(null);
        if (databaseManager != null) {
            createdDataSource = databaseManager.createSchema(dbName, jdbcTemplate4Public);
        }

        if (createdDataSource == null) {
            throw new Y9BusinessException(DataSourceErrorCodeEnum.DATABASE_NOT_FULLY_SUPPORTED.getCode(),
                DataSourceErrorCodeEnum.DATABASE_NOT_FULLY_SUPPORTED.getDescription());
        }

        Y9DataSource y9DataSource = new Y9DataSource();
        y9DataSource.setJndiName(dbName);
        y9DataSource.setUrl(createdDataSource.getUrl());
        y9DataSource.setType(DataSourceTypeEnum.DRUID);
        y9DataSource.setUsername(createdDataSource.getUsername());
        y9DataSource.setPassword(createdDataSource.getPassword());
        y9DataSource.setInitialSize(dds.getInitialSize());
        y9DataSource.setMaxActive(dds.getMaxActive());
        y9DataSource.setMinIdle(dds.getMinIdle());
        y9DataSource.setId(Optional.ofNullable(specifyId).orElse(Y9IdGenerator.genId(IdType.SNOWFLAKE)));
        return this.save(y9DataSource);
    }

    @Override
    public void delete(String id) {
        y9DataSourceRepository.deleteById(id);
    }

    @Override
    public void dropTenantDefaultDataSource(String dataSourceId) {
        Y9DataSource y9DataSource = getById(dataSourceId);
        String dbName = y9DataSource.getJndiName();

        if (StringUtils.isNotBlank(dbName)) {
            DbType dbType = DbUtil.getDbType(jdbcTemplate4Public.getDataSource());
            databaseManagerList.stream()
                .filter(dm -> dm.support(dbType))
                .findFirst()
                .ifPresent(dm -> dm.dropSchema(dbName, jdbcTemplate4Public));

        }
    }

    @Override
    public Y9DataSource save(Y9DataSource y9DataSource) {
        if (StringUtils.isBlank(y9DataSource.getId())) {
            y9DataSource.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (y9DataSource.getPassword() != null) {
            y9DataSource.setPassword(Y9Base64Util.encode(y9DataSource.getPassword()));
        }
        return y9DataSourceRepository.save(y9DataSource);
    }

}
