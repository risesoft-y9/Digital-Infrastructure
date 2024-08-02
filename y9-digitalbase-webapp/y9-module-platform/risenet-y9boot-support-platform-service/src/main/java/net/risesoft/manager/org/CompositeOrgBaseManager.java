package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface CompositeOrgBaseManager {

    /**
     * 递归得到 guid路径
     *
     * @param y9OrgBase 组织节点
     * @return
     */
    String buildGuidPath(Y9OrgBase y9OrgBase);

    /**
     * 递归向上获取排序序列号
     *
     * @param y9OrgBase 组织节点
     * @return
     */
    String buildOrderedPath(Y9OrgBase y9OrgBase);

    void checkAllDescendantsDisabled(String orgUnitId);

    /**
     * 根据指定id获取ORGBase对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Optional<Y9OrgBase> findOrgUnit(String orgUnitId);

    /**
     * 根据id获取作为父节点的组织节点（只可能是组织机构和部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Optional<Y9OrgBase> findOrgUnitAsParent(String orgUnitId);

    /**
     * 根据组织节点id获取所在委办局（可能是组织机构或部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Optional<Y9OrgBase> findOrgUnitBureau(String orgUnitId);

    /**
     * 根据组织节点id，获取其父节点(只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Optional<Y9OrgBase> findOrgUnitParent(String orgUnitId);

    /**
     * 根据id获得组织节点对象（人员或岗位）
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Optional<Y9OrgBase> findOrgUnitPersonOrPosition(String orgUnitId);

    /**
     * 根据父节点id获取子节点的下一个 tabIndex 值（获取已有子节点最大的 tabIndex + 1）
     *
     * @param parentId
     * @return {@link Integer}
     */
    Integer getNextSubTabIndex(String parentId);

    /**
     * 根据指定id获取ORGBase对象(可以是org的任意类型)
     *
     * @param orgUnitId
     * @return
     */
    Y9OrgBase getOrgUnit(String orgUnitId);

    /**
     * 根据id获取作为父节点的组织节点（只可能是组织机构和部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Y9OrgBase getOrgUnitAsParent(String orgUnitId);

    /**
     * 根据组织节点id获取所在委办局
     *
     * @param orgUnitId
     * @return
     */
    Y9OrgBase getOrgUnitBureau(String orgUnitId);

    /**
     * 根据组织节点id，获取父节点(parent只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Y9OrgBase getOrgUnitParent(String orgUnitId);

    /**
     * 根据父节点id,递归获取其下所有人员
     *
     * @param parentId
     * @return
     */
    List<Y9Person> listAllDescendantPersons(String parentId);

    /**
     * 根据父节点id,递归获取其下所有没有禁用/禁用人员
     *
     * @param parentId
     * @param disabled
     * @return
     */
    List<Y9Person> listAllDescendantPersons(String parentId, Boolean disabled);

    /**
     * 根据父节点id，递归获取其下所有岗位
     *
     * @param parentId
     * @return
     */
    List<Y9Position> listAllDescendantPositions(String parentId);

}
