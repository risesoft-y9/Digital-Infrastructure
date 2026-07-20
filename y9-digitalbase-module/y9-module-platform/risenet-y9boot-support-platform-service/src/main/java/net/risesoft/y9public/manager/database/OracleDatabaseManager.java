package net.risesoft.y9public.manager.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.db.DbType;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * Oracle数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
@RequiredArgsConstructor
public class OracleDatabaseManager extends AbstractDatabaseManager {

    private final Y9PlatformProperties y9PlatformProperties;

    @Override
    public boolean support(DbType dbType) {
        return DbType.oracle.equals(dbType);
    }

    @Override
    protected CreatedDataSource buildInternal(String dbName, String originalUrl, String originalUsername,
        String originalPassword) {
        return new CreatedDataSource(originalUrl, dbName.toUpperCase(), originalPassword);
    }

    @Override
    protected void createInternal(JdbcTemplate jdbcTemplate, CreatedDataSource createdDataSource, String dbName) {
        String tableSpace = createdDataSource.getUsername() + "_DATA";
        String dataFile = y9PlatformProperties.getInit().getNewTableSpacePath() + tableSpace + ".DBF";

        // 创建表空间
        String sql1 = Y9StringUtil.format(
            "CREATE TABLESPACE {} DATAFILE '{}' SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL",
            tableSpace, dataFile);
        // 创建用户
        String sql2 = Y9StringUtil.format(
            "CREATE USER {} IDENTIFIED BY {} ACCOUNT UNLOCK DEFAULT TABLESPACE {} TEMPORARY TABLESPACE TEMP PROFILE DEFAULT",
            createdDataSource.getUsername(), createdDataSource.getPassword(), tableSpace);
        // 给用户授权
        String sql3 = Y9StringUtil.format("GRANT DBA TO {} WITH ADMIN OPTION", createdDataSource.getUsername());
        // 修改权限
        String sql4 = Y9StringUtil.format("ALTER USER {} DEFAULT ROLE DBA", createdDataSource.getUsername());

        jdbcTemplate.update(sql1);
        jdbcTemplate.update(sql2);
        jdbcTemplate.update(sql3);
        jdbcTemplate.update(sql4);
    }

    @Override
    public void dropSchema(String dbName, JdbcTemplate jdbcTemplate) {
        String username = dbName.toUpperCase();
        String sql1 = "DROP USER " + username + " CASCADE";
        String sql2 = "DROP TABLESPACE " + username + "_DATA INCLUDING CONTENTS AND DATAFILES";
        jdbcTemplate.execute(sql1);
        jdbcTemplate.execute(sql2);
    }

}
