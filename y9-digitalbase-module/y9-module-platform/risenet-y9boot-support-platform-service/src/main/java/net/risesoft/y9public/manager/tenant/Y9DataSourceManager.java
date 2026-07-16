package net.risesoft.y9public.manager.tenant;

import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9DataSource;

/**
 * 数据源 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9DataSourceManager {

    /**
     * 根据id获取数据源信息
     *
     * @param id 数据源id
     * @return {@link Y9DataSource}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9DataSource getById(String id);

    /**
     * 创建租户系统数据源（不存在时）
     *
     * @param tenantShortName 租户短名称
     * @param systemName 系统名称
     * @param specifyId 指定的数据源id
     * @return {@link Y9DataSource}
     * @throws Y9BusinessException 当前数据库类型不支持创建数据源的情况
     */
    Y9DataSource createDataSourceIfNotExists(String tenantShortName, String systemName, String specifyId);

    /**
     * 创建数据源（不存在时）
     *
     * @param dbName 数据库名称
     * @param specifyId 指定的数据源id
     * @return {@link Y9DataSource}
     * @throws Y9BusinessException 当前数据库类型不支持创建数据源的情况
     */
    Y9DataSource createDataSourceIfNotExists(String dbName, String specifyId);

    /**
     * 根据id删除数据源
     *
     * @param id 数据源id
     */
    void delete(String id);

    /**
     * 删除租户默认数据源
     *
     * @param dataSourceId 数据源id
     * @throws Y9NotFoundException dataSourceId 对应的记录不存在的情况
     */
    void dropTenantDefaultDataSource(String dataSourceId);

    /**
     * 保存数据源
     *
     * @param y9DataSource 数据源信息
     * @return {@link Y9DataSource}
     */
    Y9DataSource save(Y9DataSource y9DataSource);

}
