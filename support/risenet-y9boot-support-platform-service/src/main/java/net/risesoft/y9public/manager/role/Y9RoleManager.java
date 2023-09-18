package net.risesoft.y9public.manager.role;

import java.util.List;

import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 角色管理 Y9RoleManager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9RoleManager {

    void delete(String id);

    void deleteByApp(String appId);

    Y9Role findById(String id);

    /**
     * 根据主键获取角色节点对象
     *
     * @param id 唯一标识
     * @return 角色对象 或 null
     */
    Y9Role getById(String id);

    List<Y9Role> listByAppIdAndParentId(String appId, String parentId);

    /**
     * 从给定节点开始，向上递归，返回递归链上所有的节点id（已去重）(如果是人员，会包括所在组，岗位id)
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

    Y9Role save(Y9Role y9Role);
}
