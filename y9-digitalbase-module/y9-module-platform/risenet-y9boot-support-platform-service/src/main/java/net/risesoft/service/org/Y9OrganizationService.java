package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Organization;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OrganizationService {

    /**
     * 更改禁用状态
     *
     * @param id id
     * @return 组织机构实体
     */
    Y9Organization changeDisabled(String id);

    /**
     * 创建组织机构
     *
     * @param organizationName 组织名称
     * @param virtual 是否虚拟组织
     * @return {@link Y9Organization}
     */
    Y9Organization create(String organizationName, Boolean virtual);

    /**
     * 根据主键id删除机构实例
     *
     * @param orgId 组织id
     */
    void delete(String orgId);

    /**
     * 根据id判断组织机构是否存在
     *
     * @param id 组织id
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据id查找组织机构
     *
     * @param id 组织id
     * @return {@code Optional<Y9Organization>}组织机构对象 或 null
     */
    Optional<Y9Organization> findById(String id);

    /**
     * 根据主键id从缓存中获取组织机构实例
     *
     * @param id 组织id
     * @return {@link Y9Organization}组织机构对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Organization getById(String id);

    /**
     * 组织机构列表
     *
     * @return {@code List<Y9Organization>}
     */
    List<Y9Organization> list();

    /**
     * 获取组织架构列表
     *
     * @param virtual 是否虚拟组织
     * @param disabled 是否包含禁用
     * @return {@code List<Y9Organization>}
     */
    List<Y9Organization> list(Boolean virtual, Boolean disabled);

    /**
     * 根据dn查找
     *
     * @param dn 域
     * @return {@code List<Y9Organization>}
     */
    List<Y9Organization> listByDn(String dn);

    /**
     * 根据租户唯一标示查找组织机构列表
     *
     * @param tenantId 租户id
     * @return {@code List<Y9Organization>}
     */
    List<Y9Organization> listByTenantId(String tenantId);

    /**
     * 保存或者更新组织机构基本信息
     *
     * @param org 组织机构对象
     * @return {@link Y9Organization}
     */
    Y9Organization saveOrUpdate(Y9Organization org);

    /**
     * 保存新的序号
     *
     * @param orgIds 机构id数组
     * @return {@code List<Y9Organization>}
     */
    List<Y9Organization> saveOrder(List<String> orgIds);

    /**
     * 保存或者更新组织机构扩展信息
     *
     * @param orgId 组织机构id
     * @param properties 扩展属性
     * @return {@link Y9Organization}
     */
    Y9Organization saveProperties(String orgId, String properties);
}
