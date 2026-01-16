package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9TenantService {

    /**
     * 修改租户的禁用状态
     *
     * @param id 租户id
     * @return {@link Tenant}
     */
    Tenant changeDisabled(String id);

    /**
     * 创建租户
     *
     * @param tenantName 租户中文名称
     * @param tenantShortName 租户英文名称
     * @param dataSourceId 默认租户数据源
     * @return {@link Tenant}
     */
    Tenant createTenant(String tenantName, String tenantShortName, String dataSourceId);

    /**
     * 根据id删除租户
     *
     * @param id 唯一标识
     */
    void delete(String id);

    void deleteAfterCheck(String id);

    /**
     * 根据id获取租户对象
     *
     * @param id 唯一标识
     * @return {@code Optional<}{@link Tenant}{@code >}租户对象 或 null
     */
    Optional<Tenant> findById(String id);

    /**
     * 根据租户英文名查找租户列表
     *
     * @param shortName 租户英文名称
     * @return {@code Optional<}{@link Tenant}{@code >}
     */
    Optional<Tenant> findByShortName(String shortName);

    /**
     * 根据租户名称查找租户列表
     *
     * @param tenantName 租户名称
     * @return {@code Optional<}{@link Tenant}{@code >}
     */
    Optional<Tenant> findByTenantName(String tenantName);

    /**
     * 根据id查找租户
     *
     * @param id 租户主键id
     * @return 租户对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Tenant getById(String id);

    /**
     * 获取所有租户列表
     *
     * @return {@code List<}{@link Tenant}{@code >}
     */
    List<Tenant> listAll();

    /**
     * 根据guidPath查找租户列表
     *
     * @param guidPath id路径
     * @return {@code List<}{@link Tenant}{@code >}
     */
    List<Tenant> listByGuidPathLike(String guidPath);

    /**
     * 根据父节点id和租户类型，获取租户列表
     *
     * @param parentId 父节点id
     * @return {@code List<}{@link Tenant}{@code >}
     */
    List<Tenant> listByParentIdAndTenantType(String parentId);

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
     * @param tenant 租户实体
     * @return {@link Tenant}
     */
    Tenant saveOrUpdate(Tenant tenant);

    /**
     * 保存租户信息
     *
     * @param y9Tenant 实体类
     * @return Tenant
     */
    Tenant saveAndInitDataSource(Tenant y9Tenant);

    /**
     * 保存租户排序信息
     *
     * @param tenantIds 租户id列表
     */
    void saveTenantOrders(String[] tenantIds);

    /**
     * 租户一键生成
     *
     * @param cnName 中文名
     * @param enName 英文名
     */
    void register(String cnName, String enName);
}
