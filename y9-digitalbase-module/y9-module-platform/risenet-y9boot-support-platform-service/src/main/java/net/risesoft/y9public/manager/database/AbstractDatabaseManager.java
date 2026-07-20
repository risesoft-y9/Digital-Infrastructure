package net.risesoft.y9public.manager.database;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

import net.risesoft.y9.db.DbType;

/**
 * 数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
public abstract class AbstractDatabaseManager {

    abstract public boolean support(DbType dbType);

    abstract protected CreatedDataSource buildInternal(String dbName, String originalUrl, String originalUsername,
        String originalPassword);

    abstract protected void createInternal(JdbcTemplate jdbcTemplate, CreatedDataSource createdDataSource,
        String dbName);

    abstract public void dropSchema(String dbName, JdbcTemplate jdbcTemplate);

    public CreatedDataSource buildAndCreateSchema(String dbName, JdbcTemplate jdbcTemplate,
        boolean createSchemaEnabled) {
        DruidDataSource dds = (DruidDataSource)jdbcTemplate.getDataSource();
        CreatedDataSource createdDataSource = buildInternal(dbName, dds.getUrl(), dds.getUsername(), dds.getPassword());
        if (createSchemaEnabled) {
            createInternal(jdbcTemplate, createdDataSource, dbName);
        }
        return createdDataSource;
    }

    /**
     * 替换 jdbc url 中的数据库名称 <br>
     *
     * 例如：replaceDatabaseNameInJdbcUrl("jdbc:mysql://localhost:3306/y9_public?allowPublicKeyRetrieval=true",
     * "y9_default") -> "jdbc:mysql://localhost:3306/y9_default?allowPublicKeyRetrieval=true"
     *
     * @param originalJdbcUrl 原始 jdbc url
     * @param newDatabaseName 新数据库名称
     * @return {@code String }
     */
    protected String replaceDatabaseNameInJdbcUrl(String originalJdbcUrl, String newDatabaseName) {
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

    protected String replaceSchemaNameInJdbcUrlParam(String originalJdbcUrl, String newSchemaName) {
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
