package net.risesoft.dao;

import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.TenantApp;
import net.risesoft.model.platform.TenantSystem;

/**
 * 多租户数据库操作
 *
 * @author shidaobang
 * @date 2024/05/13
 */
@RequiredArgsConstructor
public class MultiTenantDao {

    private final JdbcTemplate publicJdbcTemplate;

    public Integer countTenantSystem(String systemId, String dataSourceId) {
        return publicJdbcTemplate.queryForObject(
            "SELECT count(ID) FROM Y9_COMMON_TENANT_SYSTEM WHERE SYSTEM_ID = ? AND TENANT_DATA_SOURCE = ?",
            Integer.class, systemId, dataSourceId);
    }

    public List<String> getDataSourceIdList(Date updateTime) {
        return publicJdbcTemplate.queryForList("SELECT ID FROM Y9_COMMON_DATASOURCE WHERE UPDATE_TIME > ? ",
            String.class, updateTime);
    }

    public String getSystemId(String systemName) {
        try {
            return publicJdbcTemplate.queryForObject("SELECT ID FROM Y9_COMMON_SYSTEM WHERE NAME=?", String.class,
                systemName);
        } catch (EmptyResultDataAccessException ignoreException) {
            return null;
        }
    }

    public List<TenantSystem> getTenantSystemList(String systemId) {
        return publicJdbcTemplate.query("SELECT * FROM Y9_COMMON_TENANT_SYSTEM WHERE SYSTEM_ID = ?",
            new BeanPropertyRowMapper<>(TenantSystem.class), systemId);
    }

    public List<TenantApp> getUninitializedTenantAppList(String systemId) {
        return publicJdbcTemplate.query("SELECT * FROM Y9_COMMON_TENANT_APP WHERE SYSTEM_ID = ? AND INITIALIZED = 0",
            new BeanPropertyRowMapper<>(TenantApp.class), systemId);
    }

    public List<TenantSystem> getUninitializedTenantSystemList(String systemId) {
        return publicJdbcTemplate.query("SELECT * FROM Y9_COMMON_TENANT_SYSTEM WHERE SYSTEM_ID = ? AND INITIALIZED = 0",
            new BeanPropertyRowMapper<>(TenantSystem.class), systemId);
    }

    public void setTenantAppInitialized(String id) {
        publicJdbcTemplate.update("UPDATE Y9_COMMON_TENANT_APP SET INITIALIZED = 1 WHERE ID = ?", id);
    }

    public void setTenantSystemInitialized(String id) {
        publicJdbcTemplate.update("UPDATE Y9_COMMON_TENANT_SYSTEM SET INITIALIZED = 1 WHERE ID = ?", id);
    }

}
