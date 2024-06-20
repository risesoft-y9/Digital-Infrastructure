package net.risesoft.y9public.manager.tenant.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.exception.DataSourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.repository.tenant.Y9DataSourceRepository;

/**
 * 数据源 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@Slf4j
public class Y9DataSourceManagerImpl implements Y9DataSourceManager {

    private final JdbcTemplate jdbcTemplate4Public;
    private final Y9Properties y9config;
    private final Y9DataSourceRepository datasourceRepository;

    public Y9DataSourceManagerImpl(@Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        Y9Properties y9config, Y9DataSourceRepository datasourceRepository) {
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.y9config = y9config;
        this.datasourceRepository = datasourceRepository;
    }

    /**
     * 生成四位随机字符串
     *
     * @return {@link String}
     */
    public static String generateRandomString() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int length = 4;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @Override
    public String buildDataSourceName(String shortName, TenantTypeEnum tenantType, String systemName) {
        String dataSourceName = shortName;
        if (Objects.equals(tenantType, TenantTypeEnum.ISV)) {
            dataSourceName = "isv_" + shortName;
        }
        if (Objects.equals(tenantType, TenantTypeEnum.TENANT) && !"default".equals(shortName)) {
            dataSourceName = "yt_" + generateRandomString() + "_" + shortName;
        }
        if (StringUtils.isNotBlank(systemName)) {
            dataSourceName = dataSourceName + "_" + systemName;
        }
        return dataSourceName;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource createTenantDefaultDataSource(String shortName, TenantTypeEnum tenantType, String systemName) {
        String dataSourceName = this.buildDataSourceName(shortName, tenantType, systemName);
        return this.createTenantDefaultDataSource(dataSourceName, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource createTenantDefaultDataSource(String dbName) {
        return this.createTenantDefaultDataSource(dbName, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource createTenantDefaultDataSource(String dbName, String specifyId) {
        if (StringUtils.isNotBlank(specifyId)) {
            Optional<Y9DataSource> y9DataSourceOptional = datasourceRepository.findById(specifyId);
            if (y9DataSourceOptional.isPresent()) {
                return y9DataSourceOptional.get();
            }
        }

        Optional<Y9DataSource> y9DataSourceOptional = datasourceRepository.findByJndiName(dbName);
        if (y9DataSourceOptional.isPresent()) {
            return y9DataSourceOptional.get();
        }

        DruidDataSource dds = (DruidDataSource)jdbcTemplate4Public.getDataSource();
        String dbType = dds.getDbType();

        String url = null;
        String username = null;
        String password = null;

        if (Objects.equals(DbType.mysql.name(), dbType) || Objects.equals(DbType.mariadb.name(), dbType)) {
            url = replaceDatabaseNameInJdbcUrl(dds.getUrl(), dbName);
            username = dds.getUsername();
            password = dds.getPassword();

            String sql = Y9StringUtil
                .format("CREATE DATABASE IF NOT EXISTS {} DEFAULT CHARACTER SET UTF8 COLLATE UTF8_BIN", dbName);

            jdbcTemplate4Public.update(sql);
        }

        if (DbType.oracle.name().equals(dbType)) {
            url = dds.getUrl();
            username = dbName.toUpperCase();
            password = dds.getPassword();

            String tableSpace = username + "_DATA";
            String dataFile = y9config.getApp().getPlatform().getNewTableSpacePath() + tableSpace + ".DBF";

            // 创建表空间
            String sql1 = Y9StringUtil.format(
                "CREATE TABLESPACE {} DATAFILE '{}' SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL",
                tableSpace, dataFile);
            // 创建用户
            String sql2 = Y9StringUtil.format(
                "CREATE USER {} IDENTIFIED BY {} ACCOUNT UNLOCK DEFAULT TABLESPACE {} TEMPORARY TABLESPACE TEMP PROFILE DEFAULT",
                username, password, tableSpace);
            // 给用户授权
            String sql3 = Y9StringUtil.format("GRANT DBA TO {} WITH ADMIN OPTION", username);
            // 修改权限
            String sql4 = Y9StringUtil.format("ALTER USER {} DEFAULT ROLE DBA", username);

            jdbcTemplate4Public.update(sql1);
            jdbcTemplate4Public.update(sql2);
            jdbcTemplate4Public.update(sql3);
            jdbcTemplate4Public.update(sql4);
        }

        if (DbType.postgresql.name().equals(dbType)) {
            url = replaceDatabaseNameInJdbcUrl(dds.getUrl(), dbName);
            username = dds.getUsername();
            password = dds.getPassword();
            dbName = dbName.toLowerCase();

            String sql1 = Y9StringUtil.format("CREATE DATABASE {} WITH ENCODING = 'UTF8' OWNER = {}", dbName, username);
            String sql2 = Y9StringUtil.format("GRANT ALL PRIVILEGES ON DATABASE {} TO {}", dbName, username);
            jdbcTemplate4Public.update(sql1);
            jdbcTemplate4Public.update(sql2);
        }

        if (DbType.dm.equals(dbType)) {
            url = dds.getUrl();
            String upperCaseDbName = dbName.toUpperCase();
            username = upperCaseDbName;
            password = dds.getPassword();

            String tableSpace = upperCaseDbName + "_DATA";
            String dataFile = y9config.getApp().getPlatform().getNewTableSpacePath() + tableSpace + ".DBF";

            // 创建表空间
            String sql1 =
                Y9StringUtil.format("create tablespace {} datafile '{}' size 32 autoextend on next 32 CACHE = NORMAL;",
                    tableSpace, dataFile);
            // 创建用户
            String sql2 = Y9StringUtil.format(
                "create user {} identified by {} password_policy 0 PROFILE \"DEFAULT\" default tablespace {}", username,
                password, tableSpace);

            // 给用户授权
            String sql3 = Y9StringUtil.format("grant \"DBA\" to {}", username);

            jdbcTemplate4Public.update(sql1);
            jdbcTemplate4Public.update(sql2);
            jdbcTemplate4Public.update(sql3);
        }

        if (DbType.kingbase.name().equals(dbType)) {
            url = replaceSchemaNameInJdbcUrlParam(dds.getUrl(), dbName);
            username = dds.getUsername();
            password = dds.getPassword();
            // 创建模式
            String sql1 = Y9StringUtil.format("CREATE SCHEMA {}", dbName);
            jdbcTemplate4Public.update(sql1);
            // url = replaceDatabaseNameInJdbcUrl(dds.getUrl(), dbName);
            // username = dds.getUsername();
            // password = dds.getPassword();
            // String sql1 = Y9StringUtil.format(
            // "CREATE DATABASE {} WITH OWNER = \"{}\" ENCODING 'UTF8' TEMPLATE template0 lc_ctype = 'C' lc_collate =
            // 'C';",
            // dbName, username);
            // jdbcTemplate4Public.update(sql1);
        }

        if (DbType.h2.name().equals(dbType)) {
            url = "jdbc:h2:mem:" + dbName;
            username = dds.getUsername();
            password = dds.getPassword();
        }

        if (url == null) {
            throw new Y9BusinessException(DataSourceErrorCodeEnum.DATABASE_NOT_FULLY_SUPPORTED.getCode(),
                DataSourceErrorCodeEnum.DATABASE_NOT_FULLY_SUPPORTED.getDescription());
        }

        Y9DataSource y9DataSource = new Y9DataSource();
        y9DataSource.setJndiName(dbName);
        y9DataSource.setUrl(url);
        y9DataSource.setType(DataSourceTypeEnum.DRUID);
        y9DataSource.setUsername(username);
        y9DataSource.setPassword(password);
        y9DataSource.setInitialSize(dds.getInitialSize());
        y9DataSource.setMaxActive(dds.getMaxActive());
        y9DataSource.setMinIdle(dds.getMinIdle());
        y9DataSource.setId(Optional.ofNullable(specifyId).orElse(Y9IdGenerator.genId(IdType.SNOWFLAKE)));
        return this.save(y9DataSource);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        datasourceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void dropTenantDefaultDataSource(String dataSourceId, String dbName) {
        if (StringUtils.isNotBlank(dataSourceId)) {
            this.delete(dataSourceId);
        }
        if (StringUtils.isNotBlank(dbName)) {
            DruidDataSource dds = (DruidDataSource)jdbcTemplate4Public.getDataSource();
            if (dds != null) {
                if (DbType.mysql.name().equals(dds.getDbType())) {
                    String sql = "DROP DATABASE IF EXISTS " + dbName;
                    jdbcTemplate4Public.execute(sql);
                } else if (DbType.oracle.name().equals(dds.getDbType())) {
                    String username = dbName.toUpperCase();
                    String sql1 = "DROP USER " + username + " CASCADE";
                    String sql2 = "DROP TABLESPACE " + username + "_DATA INCLUDING CONTENTS AND DATAFILES";
                    jdbcTemplate4Public.execute(sql1);
                    jdbcTemplate4Public.execute(sql2);
                } else if (DbType.kingbase.name().equals(dds.getDbType())) {
                    String username = dbName.toUpperCase();
                    String sql1 = "DROP SCHEMA " + username + " CASCADE;";
                    jdbcTemplate4Public.execute(sql1);
                } else if (DbType.postgresql.name().equals(dds.getDbType())) {
                    String username = dbName.toUpperCase();
                    String sql1 = "DROP DATABASE IF EXISTS " + username;
                    jdbcTemplate4Public.execute(sql1);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataSource save(Y9DataSource y9DataSource) {
        if (StringUtils.isBlank(y9DataSource.getId())) {
            y9DataSource.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (y9DataSource.getPassword() != null) {
            y9DataSource.setPassword(Y9Base64Util.encode(y9DataSource.getPassword()));
        }
        return datasourceRepository.save(y9DataSource);
    }

    /**
     * 替换 jdbc url 中的数据库名称 <br/>
     * 
     * 例如：replaceDatabaseNameInJdbcUrl("jdbc:mysql://localhost:3306/y9_public?allowPublicKeyRetrieval=true",
     * "y9_default") -> "jdbc:mysql://localhost:3306/y9_default?allowPublicKeyRetrieval=true"
     *
     * @param originalJdbcUrl 原始 jdbc url
     * @param newDatabaseName 新数据库名称
     * @return {@code String }
     */
    private String replaceDatabaseNameInJdbcUrl(String originalJdbcUrl, String newDatabaseName) {
        int dbNameStart = originalJdbcUrl.lastIndexOf("/") + 1;
        int dbNameEnd = originalJdbcUrl.indexOf("?");

        if (dbNameStart > 0) {
            String oldDatabaseName;
            if (dbNameEnd > dbNameStart) {
                // jdbc url 后不带参数
                oldDatabaseName = originalJdbcUrl.substring(dbNameStart, dbNameEnd);
            } else {
                oldDatabaseName = originalJdbcUrl.substring(dbNameStart);
            }
            return originalJdbcUrl.replace(oldDatabaseName, newDatabaseName);
        } else {
            // 如果无法提取数据库名称部分或者替换失败，返回原始的 JDBC URL
            return originalJdbcUrl;
        }
    }

    private static String replaceSchemaNameInJdbcUrlParam(String originalJdbcUrl, String newSchemaName) {
        int paramStart = originalJdbcUrl.indexOf("?") + 1;

        if (paramStart > 0) {
            String jdbcUrl = originalJdbcUrl.substring(0, paramStart);
            String originalParams = originalJdbcUrl.substring(paramStart);
            String newParams = Arrays.stream(StringUtils.split(originalParams, "&")).map(param -> {
                if (param.startsWith("currentSchema=")) {
                    return "currentSchema=" + newSchemaName;
                } else {
                    return param;
                }
            }).collect(Collectors.joining("&"));
            return jdbcUrl + newParams;
        } else {
            return originalJdbcUrl + "?currentSchema=" + newSchemaName;
        }
    }

}
