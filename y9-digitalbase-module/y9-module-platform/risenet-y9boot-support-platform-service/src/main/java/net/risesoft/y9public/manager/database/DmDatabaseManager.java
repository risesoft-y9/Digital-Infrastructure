package net.risesoft.y9public.manager.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.db.DbType;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * 达梦数据库管理器
 *
 * @author shidaobang
 * @date 2026/01/15
 */
@Service
@RequiredArgsConstructor
public class DmDatabaseManager extends AbstractDatabaseManager {

    private final Y9PlatformProperties y9PlatformProperties;

    @Override
    public boolean support(DbType dbType) {
        return DbType.dm.equals(dbType);
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
            "create tablespace {} datafile '{}' size 128 autoextend on next 32 CACHE = NORMAL;", tableSpace, dataFile);
        // 创建用户
        String sql2 = Y9StringUtil.format(
            "create user {} identified by \"{}\" password_policy 15 PROFILE \"DEFAULT\" default tablespace \"{}\"",
            createdDataSource.getUsername(), createdDataSource.getPassword(), tableSpace);

        // 给用户授权
        String sql3 = Y9StringUtil.format("grant \"DBA\" to {}", createdDataSource.getUsername());

        jdbcTemplate.update(sql1);
        jdbcTemplate.update(sql2);
        jdbcTemplate.update(sql3);
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
