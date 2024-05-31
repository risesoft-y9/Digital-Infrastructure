package net.risesoft.y9public.service.role;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9RoleService {

    /**
     * 新建角色
     *
     * @param y9Role 角色对象
     * @return {@link Y9Role}
     */
    Y9Role createRole(Y9Role y9Role);

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
     * @return {@link Y9Role}
     */
    Optional<Y9Role> findByCustomIdAndParentId(String customId, String parentId);

    /**
     * 根据主键获取角色节点对象
     *
     * @param id 唯一标识
     * @return 角色对象 或 null
     */
    Optional<Y9Role> findById(String id);

    /**
     * 根据给定节点id，查找顶级节点
     *
     * @param id 唯一标识
     * @return {@link Y9Role}
     */
    Y9Role findTopByRoleId(String id);

    /**
     * 根据id获取角色
     *
     * @param roleId role id
     * @return {@link Y9Role}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Role getById(String roleId);

    /**
     * 获取所有角色
     *
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listAll();

    /**
     * 根据角色名称查找
     *
     * @param name 角色名
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByName(String name);

    /**
     * 根据orgUnitId获取角色列表
     *
     * @param orgUnitId 组织节点id
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByOrgUnitId(String orgUnitId);

    /**
     * 根据orgUnitId获取拥有正权限关联的角色列表
     *
     * @param orgUnitId 组织节点id
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByOrgUnitIdWithoutNegative(String orgUnitId);

    /**
     * 根据父节点id,获取本层级的角色节点列表
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByParentId(String parentId);

    /**
     * 根据父节点id和角色名称，获取角色列表
     *
     * @param parentId 父节点id
     * @param roleName 角色名称
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByParentIdAndName(String parentId, String roleName);

    /**
     * 查询parentId为null的角色集合
     *
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listByParentIdIsNull();

    /**
     * 从给定节点开始，向上递归，返回递归链上所有的节点id(如果是人员，会包括所在组，岗位id)
     *
     * @param orgUnitId
     * @return
     */
    List<String> listOrgUnitIdRecursively(String orgUnitId);

    /**
     * 根据组织节点id获取所有关联的角色列表（已排除掉负关联） 对于组织机构到组织根节点及所有中间组织节点关联的角色也包含在内
     *
     * @param orgUnitId 组织节点id
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listOrgUnitRelatedWithoutNegative(String orgUnitId);

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
     * @param y9Role 角色对象
     * @return {@link Y9Role}
     */
    Y9Role saveOrUpdate(Y9Role y9Role);

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
     * @return {@link Y9Role}
     */
    Y9Role saveProperties(String id, String properties);

    List<Y9Role> treeSearch(String name, String parentId);

    /**
     * 根据name查询角色节点
     *
     * @param name 角色名
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> treeSearch(String name);

}
