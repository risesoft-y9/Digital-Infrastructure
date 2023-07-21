package org.apereo.cas.web.y9.y9user;

import java.util.List;

import org.apereo.cas.web.y9.util.common.Y9Util;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class Y9UserDao {
    private JdbcTemplate y9UserJdbcTemplate;

    public Y9UserDao(JdbcTemplate y9UserJdbcTemplate) {
        this.y9UserJdbcTemplate = y9UserJdbcTemplate;
    }

    public List<Y9User> findByLoginNameAndOriginal(String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where LOGIN_NAME=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), loginName, original);
        return users;
    }

    public List<Y9User> findByLoginNameContainingAndOriginalOrderByTenantShortName(String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where LOGIN_NAME like concat('%',?,'%') and ORIGINAL=? order by TENANT_SHORT_NAME", new BeanPropertyRowMapper<>(Y9User.class), loginName, original);
        return users;
    }

    public List<Y9User> findByMobileAndOriginal(String mobile, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where MOBILE=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), mobile, original);
        return users;
    }

    public List<Y9User> findByOriginalAndLoginNameStartingWith(Boolean original, String loginName) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where ORIGINAL=? and LOGIN_NAME like concat(?,'%')", new BeanPropertyRowMapper<>(Y9User.class), original, loginName);
        return users;
    }

    public List<Y9User> findByTenantIDAndLoginNameAndOriginal(String tenantId, String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_ID=? and LOGIN_NAME=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), tenantId, loginName, original);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndLoginName(String tenantShortName, String loginName) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and LOGIN_NAME=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, loginName);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndLoginNameAndOriginal(String tenantShortName, String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and LOGIN_NAME=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, loginName, original);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndLoginNameAndParentId(String tenantShortName, String loginName, String parentId) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and LOGIN_NAME=? and PARENT_ID=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, loginName, parentId);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndLoginNameAndPassword(String tenantShortName, String loginName, String password) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and LOGIN_NAME=? and PASSWORD=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, loginName, password);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndLoginNameAndPasswordAndOriginal(String tenantShortName, String loginName, String password, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and LOGIN_NAME=? and PASSWORD=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, loginName, password, original);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndMobile(String tenantShortName, String mobile) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and MOBILE=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, mobile);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndMobileAndOriginal(String tenantShortName, String mobile, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and MOBILE=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, mobile, original);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndMobileAndParentId(String tenantShortName, String mobile, String parentId) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and MOBILE=? and PARENT_ID=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, mobile, parentId);
        return users;
    }

    public List<Y9User> findByTenantShortNameAndMobileAndPassword(String tenantShortName, String mobile, String password) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME=? and MOBILE=? and PASSWORD=?", new BeanPropertyRowMapper<>(Y9User.class), tenantShortName, mobile, password);
        return users;
    }

    public List<Y9User> findByTenantShortNameNotInAndLoginNameAndOriginal(List<String> tenantlist, String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_SHORT_NAME not in " + Y9Util.ListToSQLIN(tenantlist) + " and LOGIN_NAME=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), loginName, original);
        return users;
    }

    public List<Y9User> findByTenantNameAndLoginNameAndOriginal(String tenantName, String loginName, Boolean original) {
        List<Y9User> users = y9UserJdbcTemplate.query("select * from Y9_COMMON_ACCOUNT where TENANT_NAME=? and LOGIN_NAME=? and ORIGINAL=?", new BeanPropertyRowMapper<>(Y9User.class), tenantName, loginName, original);
        return users;
    }

    public JdbcTemplate getY9UserJdbcTemplate() {
        return y9UserJdbcTemplate;
    }

    public void setY9UserJdbcTemplate(JdbcTemplate y9UserJdbcTemplate) {
        this.y9UserJdbcTemplate = y9UserJdbcTemplate;
    }

}
