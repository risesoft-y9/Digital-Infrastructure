package net.risesoft.y9public.service.tenant;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.data.domain.Page;

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9DataSource;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9DataSourceService {

    String buildTenantDataSourceName(String shortName, Integer tenantType);

    /**
     * 修改密码
     *
     * @param id ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String id, String oldPassword, String newPassword);

    Y9DataSource createTenantDefaultDataSource(String dbName);

    /**
     * 创建租户默认数据库和数据源
     *
     * @param shortName 租户名
     * @param tenantType 租户类型
     * @param systemName 系统名称
     * @return String 数据源id
     */
    Y9DataSource createTenantDefaultDataSource(String shortName, Integer tenantType, String systemName);

    /**
     * 根据id删除数据源
     *
     * @param id ：数据源主键id
     */
    void delete(String id);

    /**
     * 创建租户发生异常，删除对应的数据源和数据库
     *
     * @param dataSourceId 数据源id
     * @param dbName 数据库名称
     */
    void dropTenantDefaultDataSource(String dataSourceId, String dbName);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源主键id
     * @return 数据源对象 或 null
     */
    Optional<Y9DataSource> findById(String id);

    /**
     * 根据 jndi数据源名称 查找
     *
     * @param jndiName jndi数据源名称
     * @return {@link Y9DataSource}
     */
    Optional<Y9DataSource> findByJndiName(String jndiName);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源主键id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9DataSource getById(String id);

    /**
     * 获取根据ID获取要测试的数据源
     *
     * @param id 唯一标识
     * @return {@link DataSource}
     * @throws Exception 异常
     */
    HikariDataSource getDataSource(String id);

    /**
     * 查询租户数据源分页列表
     *
     * @param page 页数
     * @param rows 每页的行数
     * @return {@link Page}<{@link Y9DataSource}>
     */
    Page<Y9DataSource> page(int page, int rows);

    /**
     * 重置默认密码
     *
     * @param id ID
     */
    void resetDefaultPassword(String id);

    /**
     * 保存租户数据源
     *
     * @param y9DataSource 数据源对象
     * @return {@link Y9DataSource}
     */
    Y9DataSource save(Y9DataSource y9DataSource);
}
