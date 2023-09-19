package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

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
     * @return Tenant
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
     * @return 租户对象 或 null
     */
    Optional<Y9Tenant> findById(String id);

    /**
     * 根据id查找租户
     *
     * @param id 租户主键id
     * @return 租户对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Tenant getById(String id);

    /**
     * 返回最大的排序号
     *
     * @return Integer
     */
    Integer getMaxTableIndex();

    /**
     * 获取所有租户列表
     *
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listAll();

    /**
     * 根据guidPath查找租户列表
     *
     * @param guidPath id路径
     * @return {@link List}<{@link Y9Tenant}>
     */
    List<Y9Tenant> listByGuidPathLike(String guidPath);

    /**
     * 根据父节点id和租户类型，获取租户列表
     *
     * @param parentId 父节点id
     * @param tenantType 租户类型 {@link net.risesoft.enums.TenantTypeEnum}
     * @return {@link List}<{@link Y9Tenant}>
     */
    List<Y9Tenant> listByParentIdAndTenantType(String parentId, Integer tenantType);

    /**
     * 根据租户英文名查找租户列表
     *
     * @param shortName 租户英文名称
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listByShortName(String shortName);

    /**
     * 根据租户名称查找租户列表
     *
     * @param tenantName 租户名称
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listByTenantName(String tenantName);

    /**
     * 根据租户类型(TenantType)获取租户
     *
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listByTenantType(Integer tenantType);

    /**
     * 根据租户名称和类型获取租户数
     *
     * @param name 租户名称
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listByTenantType(String name, Integer tenantType);

    /**
     * 根据租户类型，查找租户列表
     *
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @param tenantType2 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return List&lt;Tenant&gt;
     */
    List<Y9Tenant> listByTenantTypeIn(Integer tenantType, Integer tenantType2);

    /**
     * 分页查询租户列表
     *
     * @param page 页数
     * @param rows 每页行数
     * @return Page&lt;Tenant&gt;
     */
    Page<Y9Tenant> page(int page, int rows);

    /**
     * 分页查询租户列表
     *
     * @param page 页数
     * @param rows 每页行数
     * @param name 租户名称
     * @param enabled 是否禁用
     * @return Page&lt;Tenant&gt;
     */
    Page<Y9Tenant> pageByNameAndEnabled(int page, int rows, String name, Integer enabled);

    /**
     * 保存租户实体类
     *
     * @param entity 租户实体
     * @param parentId 父节点id
     * @return {@link Y9Tenant}
     */
    Y9Tenant save(Y9Tenant entity, String parentId);

    /**
     * 保存租户信息
     *
     * @param y9Tenant 实体类
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return Tenant
     */
    Y9Tenant saveOrUpdate(Y9Tenant y9Tenant, Integer tenantType);

    /**
     * 保存租户排序信息
     *
     * @param tenantIds 租户id列表
     */
    void saveTenantOrders(String[] tenantIds);

}
