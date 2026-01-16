package net.risesoft.y9public.manager.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.y9.db.DbType;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * postgreSQL数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
public class PostgreSqlDatabaseManager extends AbstractDatabaseManager {
    @Override
    public boolean support(DbType dbType) {
        return DbType.postgresql.equals(dbType);
    }

    @Override
    protected CreatedDataSource createSchemaInternal(JdbcTemplate jdbcTemplate, String dbName, String originalUrl,
        String originalUsername, String originalPassword) {
        String url = replaceDatabaseNameInJdbcUrl(originalUrl, dbName);
        dbName = dbName.toLowerCase();

        String sql1 =
            Y9StringUtil.format("CREATE DATABASE {} WITH ENCODING = 'UTF8' OWNER = {}", dbName, originalUsername);
        String sql2 = Y9StringUtil.format("GRANT ALL PRIVILEGES ON DATABASE {} TO {}", dbName, originalUsername);
        jdbcTemplate.update(sql1);
        jdbcTemplate.update(sql2);
        return new CreatedDataSource(url, originalUsername, originalPassword);
    }

    @Override
    public void dropSchema(String dbName, JdbcTemplate jdbcTemplate) {
        String username = dbName.toUpperCase();
        String sql1 = "DROP DATABASE IF EXISTS " + username;
        jdbcTemplate.execute(sql1);
    }

}
