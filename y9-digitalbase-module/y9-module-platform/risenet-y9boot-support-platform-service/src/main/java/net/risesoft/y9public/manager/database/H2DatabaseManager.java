package net.risesoft.y9public.manager.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.y9.db.DbType;

/**
 * h2数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
public class H2DatabaseManager extends AbstractDatabaseManager {
    @Override
    public boolean support(DbType dbType) {
        return DbType.h2.equals(dbType);
    }

    @Override
    protected CreatedDataSource createSchemaInternal(JdbcTemplate jdbcTemplate, String dbName, String originalUrl,
        String originalUsername, String originalPassword) {
        String url = "jdbc:h2:mem:" + dbName;
        return new CreatedDataSource(url, originalUsername, originalPassword);
    }

    @Override
    public void dropSchema(String dbName, JdbcTemplate jdbcTemplate) {

    }

}
