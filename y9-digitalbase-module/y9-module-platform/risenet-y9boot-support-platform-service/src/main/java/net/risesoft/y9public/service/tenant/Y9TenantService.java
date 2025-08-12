package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9TenantService {

    /**
     * 修改租户的默认数据源
     *
     * @param id 租户id
     * @param datasourceId 数据源id
     * @return {@link Y9Tenant}
     */
    Y9Tenant changDefaultDataSourceId(String id, String datasourceId);

    /**
     * 修改租户的禁用状态
     *
     * @param id 租户id
     * @return {@link Y9Tenant}
     */
    Y9Tenant changeDisabled(String id);

    /**
     * 根据 shortName 获取数量
     *
     * @param shortName 租户英文名
     * @return long
     */
    long countByShortName(String shortName);

    /**
     * 根据shortName查询id不是tenantId的数量
     *
     * @param shortName 租户英文名
     * @param tenantId 租户唯一标识
     * @return long
     */
    long countByShortNameAndIdIsNot(String shortName, String tenantId);

    /**
     * 创建租户
     *
     * @param tenantName 租户中文名称
     * @param tenantShortName 租户英文名称
     * @param dataSourceId 默认租户数据源
     * @return {@link Y9Tenant}
     */
    Y9Tenant createTenant(String tenantName, String tenantShortName, String dataSourceId);

    /**
     * 根据id删除租户
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据id获取租户对象
     *
     * @param id 唯一标识
     * @return {@code Optional<}{@link Y9Tenant}{@code >}租户对象 或 null
     */
    Optional<Y9Tenant> findById(String id);

    /**
     * 根据租户英文名查找租户列表
     *
     * @param shortName 租户英文名称
     * @return {@code Optional<}{@link Y9Tenant}{@code >}
     */
    Optional<Y9Tenant> findByShortName(String shortName);

    /**
     * 根据租户名称查找租户列表
     *
     * @param tenantName 租户名称
     * @return {@code Optional<}{@link Y9Tenant}{@code >}
     */
    Optional<Y9Tenant> findByTenantName(String tenantName);

    /**
     * 根据id查找租户
     *
     * @param id 租户主键id
     * @return 租户对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Tenant getById(String id);

    /**
     * 获取所有租户列表
     *
     * @return {@code List<}{@link Y9Tenant}{@code >}
     */
    List<Y9Tenant> listAll();

    /**
     * 根据guidPath查找租户列表
     *
     * @param guidPath id路径
     * @return {@code List<}{@link Y9Tenant}{@code >}
     */
    List<Y9Tenant> listByGuidPathLike(String guidPath);

    /**
     * 根据父节点id和租户类型，获取租户列表
     *
     * @param parentId 父节点id
     * @return {@code List<}{@link Y9Tenant}{@code >}
     */
    List<Y9Tenant> listByParentIdAndTenantType(String parentId);

    /**
     * 移动租户
     *
     * @param id ID
     * @param parentId 父ID
     */
    void move(String id, String parentId);

    /**
     * 保存租户实体类
     *
     * @param entity 租户实体
     * @return {@link Y9Tenant}
     */
    Y9Tenant saveOrUpdate(Y9Tenant entity);

    /**
     * 保存租户信息
     *
     * @param y9Tenant 实体类
     * @return Tenant
     */
    Y9Tenant saveAndInitDataSource(Y9Tenant y9Tenant);

    /**
     * 保存租户排序信息
     *
     * @param tenantIds 租户id列表
     */
    void saveTenantOrders(String[] tenantIds);

}
