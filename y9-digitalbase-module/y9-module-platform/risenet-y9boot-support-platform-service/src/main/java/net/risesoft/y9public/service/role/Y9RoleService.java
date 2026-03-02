package net.risesoft.y9public.service.role;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.Role;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9RoleService {

    /**
     * 根据主键id移除角色节点实例(如果是角色，移除角色内的/机构/部门/用户组/岗位/人员)
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 对于流程角色节点下面的角色customId为taskDefineKey 同一个流程角色下customId是唯一的， 但是不同的流程角色下，customId可能会有多个
     *
     * @param customId 自定义id
     * @param parentId 父节点id
     * @return {@code Optional<}{@link Role}{@code >}
     */
    Optional<Role> findByCustomIdAndParentId(String customId, String parentId);

    /**
     * 根据主键获取角色节点对象
     *
     * @param id 唯一标识
     * @return {@code Optional<}{@link Role}{@code >}角色对象 或 null
     */
    Optional<Role> findById(String id);

    /**
     * 根据id获取角色
     *
     * @param roleId role id
     * @return {@link Role}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Role getById(String roleId);

    /**
     * 根据 id 列表获取角色
     *
     * @param ids IDs
     * @return {@code List<Role> }
     */
    List<Role> listByIds(List<String> ids);

    /**
     * 根据角色名称查找
     *
     * @param name 角色名
     * @return {@code List<}{@link Role}{@code >}
     */
    List<Role> listByName(String name);

    /**
     * 根据父节点id,获取本层级的角色节点列表
     *
     * @param parentId 父节点id
     * @return {@code List<}{@link Role}{@code >}
     */
    List<Role> listByParentId(String parentId);

    List<Role> listByParentId4Tenant(String parentId, String tenantId);

    /**
     * 保存移动后的角色信息
     *
     * @param id 唯一标识
     * @param newParentId 新的父节点id
     */
    void move(String id, String newParentId);

    /**
     * 新增或修改角色
     *
     * @param role 角色对象
     * @return {@link Role}
     */
    Role saveOrUpdate(Role role);

    /**
     * 保存新的角色节点排序
     *
     * @param ids id数组
     */
    void saveOrder(List<String> ids);

    /**
     * 保存或者更新角色节点扩展信息
     *
     * @param id 唯一标识
     * @param properties 扩展熟悉
     * @return {@link Role}
     */
    Role saveProperties(String id, String properties);

    List<Role> treeSearch(String name, String parentId);

    /**
     * 根据name查询角色节点
     *
     * @param name 角色名
     * @return {@code List<}{@link Role}{@code >}
     */
    List<Role> treeSearch(String name);
}
