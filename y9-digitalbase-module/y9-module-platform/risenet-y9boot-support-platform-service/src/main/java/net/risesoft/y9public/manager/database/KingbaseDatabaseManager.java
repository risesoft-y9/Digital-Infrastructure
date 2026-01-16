package net.risesoft.y9public.manager.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.y9.db.DbType;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * 人大金仓数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
public class KingbaseDatabaseManager extends AbstractDatabaseManager {
    @Override
    public boolean support(DbType dbType) {
        return DbType.kingbase.equals(dbType);
    }

    @Override
    protected CreatedDataSource createSchemaInternal(JdbcTemplate jdbcTemplate, String dbName, String originalUrl,
        String originalUsername, String originalPassword) {
        String url = replaceSchemaNameInJdbcUrlParam(originalUrl, dbName);
        // 创建模式
        String sql1 = Y9StringUtil.format("CREATE SCHEMA {}", dbName);
        jdbcTemplate.update(sql1);
        // url = replaceDatabaseNameInJdbcUrl(dds.getUrl(), dbName);
        // username = dds.getUsername();
        // password = dds.getPassword();
        // String sql1 = Y9StringUtil.format(
        // "CREATE DATABASE {} WITH OWNER = \"{}\" ENCODING 'UTF8' TEMPLATE template0 lc_ctype = 'C' lc_collate =
        // 'C';",
        // dbName, username);
        // jdbcTemplate.update(sql1);
        return new CreatedDataSource(url, originalUsername, originalPassword);
    }

    @Override
    public void dropSchema(String dbName, JdbcTemplate jdbcTemplate) {
        String username = dbName.toUpperCase();
        String sql1 = "DROP SCHEMA " + username + " CASCADE;";
        jdbcTemplate.execute(sql1);
    }

}
