package net.risesoft.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9PlatformUtil {
    private static JdbcTemplate jdbcTemplate4Public = null;

    private static JdbcTemplate jdbcTemplate4Tenant = null;

    private static JdbcTemplate getJdbcTemplate4Public() {
        if (jdbcTemplate4Public == null) {
            jdbcTemplate4Public = Y9Context.getBean("jdbcTemplate4Public");
        }
        return jdbcTemplate4Public;
    }

    private static JdbcTemplate getJdbcTemplate4Tenant() {
        if (jdbcTemplate4Tenant == null) {
            jdbcTemplate4Tenant = Y9Context.getBean("jdbcTemplate4Tenant");
        }
        return jdbcTemplate4Tenant;
    }

    public static List<Map<String, Object>> getProductCode() {
        List<Map<String, Object>> list = null;
        try {
            list = getJdbcTemplate4Public().queryForList("select NAME as product_code from Y9_COMMON_SYSTEM");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return list;
    }

    public static Map<String, Object> getSystemById(String systemId) {
        List<Map<String, Object>> systemNameList = getJdbcTemplate4Public().queryForList("select ID,NAME,CN_NAME from Y9_COMMON_SYSTEM t where t.ID = ?", systemId);
        if (!systemNameList.isEmpty()) {
            Map<String, Object> systemName = systemNameList.get(0);
            return systemName;
        } else {
            return Collections.emptyMap();
        }
    }

    public static String getSystemIdByName(String systemName) {
        List<Map<String, Object>> systemIdList = getJdbcTemplate4Public().queryForList("select ID from Y9_COMMON_SYSTEM t where t.NAME = ?", systemName);
        if (!systemIdList.isEmpty()) {
            Map<String, Object> systemId = systemIdList.get(0);
            return systemId.get("ID").toString();
        } else {
            return null;
        }
    }

    public static List<String> getSystemIdByTenantId(String tenantId) {
        String sql = "select t.SYSTEM_ID from Y9_COMMON_TENANT_SYSTEM t where t.TENANT_ID='" + tenantId + "'";
        List<String> systemIds = getJdbcTemplate4Public().queryForList(sql, String.class);
        return systemIds;
    }

    public static String getSystemNameById(String systemId) {
        List<String> systemNameList = getJdbcTemplate4Public().queryForList("select NAME from Y9_COMMON_SYSTEM t where t.ID = ?", String.class, systemId);
        if (!systemNameList.isEmpty()) {
            String systemName = systemNameList.get(0);
            return systemName;
        } else {
            return null;
        }
    }

    public static Y9Tenant getTenantById(String tenantId) {
        return getJdbcTemplate4Public().queryForObject("select * from Y9_COMMON_TENANT t where t.ID=?", new BeanPropertyRowMapper<>(Y9Tenant.class), tenantId);
    }

    public static List<String> getTenantByLoginName(String loginName) {
        String sql = "select id from Y9_COMMON_TENANT t where t.SHORT_NAME='" + loginName + "'";
        List<String> tenantIds = getJdbcTemplate4Public().queryForList(sql, String.class);
        return tenantIds;
    }

    public static List<String> getTenantByName(String name) {
        String sql = "select id from Y9_COMMON_TENANT t where t.NAME='" + name + "'";
        List<String> systemIds = getJdbcTemplate4Public().queryForList(sql, String.class);
        return systemIds;
    }

    public static List<String> getTenantIds() {
        return getJdbcTemplate4Public().queryForList("select ID from Y9_COMMON_TENANT", String.class);
    }

    public static List<String> listPositionIdsByPersonId(String personId) {
        List<String> positionIds = null;
        try {
            positionIds = getJdbcTemplate4Tenant().queryForList("select t.POSITION_ID from Y9_ORG_PERSONS_POSITIONS t where t.PERSON_ID = ? order by t.POSITION_ORDER", String.class, personId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return positionIds;
    }

    public static boolean updateTenant(Y9Tenant y9Tenant) {
        String id = y9Tenant.getId();
        try {
            int count = getJdbcTemplate4Public().queryForObject("select count(ID) from Y9_COMMON_TENANT where ID=?", Integer.class, id);
            if (count == 0) {
                getJdbcTemplate4Public().update("insert into Y9_COMMON_TENANT(ID,NAME,DESCRIPTION,ENABLED,TENANT_TYPE,TAB_INDEX,CREATE_TIME,UPDATE_TIME,LOGO_ICON,FOOTER,DEFAULT_DATA_SOURCE_ID) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", id, y9Tenant.getName(), y9Tenant.getDescription(),
                    y9Tenant.getEnabled(), y9Tenant.getTenantType(), y9Tenant.getTabIndex(), y9Tenant.getCreateTime(), y9Tenant.getUpdateTime(), y9Tenant.getLogoIcon(), y9Tenant.getFooter(), y9Tenant.getDefaultDataSourceId());
            } else {
                getJdbcTemplate4Public().update("update Y9_COMMON_TENANT set NAME=?,DESCRIPTION=?,ENABLED=?,TENANT_TYPE=?,TAB_INDEX=?,CREATE_TIME=?,UPDATE_TIME=?,LOGO_ICON=?,FOOTER=?,DEFAULT_DATA_SOURCE_ID=? where ID=?", y9Tenant.getName(), y9Tenant.getDescription(), y9Tenant.getEnabled(),
                    y9Tenant.getTenantType(), y9Tenant.getTabIndex(), y9Tenant.getCreateTime(), y9Tenant.getUpdateTime(), y9Tenant.getLogoIcon(), y9Tenant.getFooter(), y9Tenant.getDefaultDataSourceId(), id);
            }
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return false;
    }

    private Y9PlatformUtil() {
        throw new IllegalStateException("Y9PlatformUtil class");
    }
}
