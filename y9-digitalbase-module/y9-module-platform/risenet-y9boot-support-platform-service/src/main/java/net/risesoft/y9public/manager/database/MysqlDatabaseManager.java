package net.risesoft.y9public.manager.database;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.y9.db.DbType;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * MySQL数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
public class MysqlDatabaseManager extends AbstractDatabaseManager {
    @Override
    public boolean support(DbType dbType) {
        return Objects.equals(DbType.mysql, dbType) || Objects.equals(DbType.mariadb, dbType);
    }

    @Override
    protected CreatedDataSource createSchemaInternal(JdbcTemplate jdbcTemplate, String dbName, String originalUrl,
        String originalUsername, String originalPassword) {
        String url = replaceDatabaseNameInJdbcUrl(originalUrl, dbName);

        String sql =
            Y9StringUtil.format("CREATE DATABASE IF NOT EXISTS {} DEFAULT CHARACTER SET UTF8 COLLATE UTF8_BIN", dbName);

        jdbcTemplate.update(sql);
        return new CreatedDataSource(url, originalUsername, originalPassword);
    }

    @Override
    public void dropSchema(String dbName, JdbcTemplate jdbcTemplate) {
        String sql = "DROP DATABASE IF EXISTS " + dbName;
        jdbcTemplate.execute(sql);
    }

}
