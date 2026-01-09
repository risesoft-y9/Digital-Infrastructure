package net.risesoft.service.org;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;

/**
 * 组合的组织节点 service
 *
 * @author shidaobang
 * @date 2023/07/31
 * @since 9.6.3
 */
public interface CompositeOrgBaseService {

    /**
     * 根据组织树类型和ID路径计数成员（可能是岗位和人员计数）
     *
     * @param guidPath GUID路径
     * @param orgTreeTypeEnum 树型
     * @return long
     */
    long countByGuidPath(String guidPath, OrgTreeTypeEnum orgTreeTypeEnum);

    /**
     * 根据指定id获取组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnit(String orgUnitId);

    /**
     * 根据id获取作为父节点的组织节点（只可能是组织机构和部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnitAsParent(String orgUnitId);

    /**
     * 根据组织节点id获取所在委办局（可能是组织机构或部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnitBureau(String orgUnitId);

    /**
     * 根据指定id获取已删除的组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnitDeleted(String orgUnitId);

    /**
     * 获取组织节点所在组织机构
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<Organization> findOrgUnitOrganization(String orgUnitId);

    /**
     * 根据组织节点id，获取其父节点(只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnitParent(String orgUnitId);

    /**
     * 根据id获得组织节点对象（人员或岗位）
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<OrgUnit>}
     */
    Optional<OrgUnit> findOrgUnitPersonOrPosition(String orgUnitId);

    /**
     * 根据指定id获取组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link OrgUnit}
     */
    OrgUnit getOrgUnit(String orgUnitId);

    /**
     * 根据id获取作为父节点的组织节点（只可能是组织机构和部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link OrgUnit}
     */
    OrgUnit getOrgUnitAsParent(String orgUnitId);

    /**
     * 根据组织节点id获取所在委办局（可能是组织机构或部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link OrgUnit}
     */
    OrgUnit getOrgUnitBureau(String orgUnitId);

    /**
     * 根据指定id获取已删除的组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link OrgUnit}
     */
    OrgUnit getOrgUnitDeleted(String orgUnitId);

    /**
     * 获取组织节点所在组织机构
     *
     * @param orgUnitId 组织节点id
     * @return {@link Organization}
     */
    Organization getOrgUnitOrganization(String orgUnitId);

    /**
     * 根据组织节点id，获取其父节点(只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return {@link OrgUnit}
     */
    OrgUnit getOrgUnitParent(String orgUnitId);

    /**
     * 根据syncId,orgType,needRecursion进行查找
     *
     * @param syncId 同步的组织机构id
     * @param orgType 机构类型：Organization（组织），Department（部门），Group（用户组），Position（岗位），Person（人员），PersonLink（人员）
     * @param needRecursion 是否递归
     * @return {@code  HashMap<String, Serializable>}
     */
    HashMap<String, Serializable> getSyncMap(String syncId, OrgTypeEnum orgType, Integer needRecursion);

    SyncOrgUnits getSyncOrgUnits(String organizationId, OrgTypeEnum orgTypeEnum, boolean recursionRequired);

    /**
     * 获取机构树子节点
     *
     * @param id 组织节点id
     * @param treeType 树类型
     * @param disabled 节点是否禁用
     * @return {@code List<OrgUnit>}
     */
    List<OrgUnit> getTree(String id, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 子域三员获取部门树
     *
     * @param id 组织节点id
     * @param treeType 树类型
     * @param disabled 是否禁用
     * @return {@code List<OrgUnit>}
     */
    List<OrgUnit> getTree4DeptManager(String id, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 根据父节点id，获取子节点下的所有人员，包括用户组和岗位下的人员
     *
     * @param parentId 父节点id
     * @return {@code List<OrgUnit>}
     */
    List<Person> listAllPersonsByParentId(String parentId);

    /**
     * 根据父节点id,递归获取其下所有人员
     *
     * @param parentId 父节点id
     * @return {@code List<OrgUnit>}
     */
    List<Person> listAllDescendantPersons(String parentId);

    /**
     * 根据父节点id,递归获取其下所有没有禁用/禁用人员
     *
     * @param parentId 父节点id
     * @param disabled 是否禁用
     * @return {@code List<OrgUnit>}
     */
    List<Person> listAllDescendantPersons(String parentId, Boolean disabled);

    /**
     * 根据父节点id,人员姓名，是否禁用，递归获取其下所有人员
     *
     * @param parentId 父节点id
     * @param name 组织节点名称
     * @param disabled 是否禁用
     * @return {@code List<Y9Person>}
     */
    List<Person> searchAllPersonsRecursionDownward(String parentId, String name, Boolean disabled);

    /**
     * 排序
     *
     * @param orgUnitIds 组织节点id
     */
    void sort(List<String> orgUnitIds);

    /**
     * 同步数据到消息中间件
     *
     * @param syncId 同步的组织机构id
     * @param orgType 组织节点类型
     * @param needRecursion 是否递归获取
     * @param targetSystemName 目标系统名
     */
    void sync(String syncId, OrgTypeEnum orgType, boolean needRecursion, String targetSystemName);

    /**
     * 根据name，和结构树类型查询机构主体(不含禁用人员)
     *
     * @param name 组织节点名称
     * @param treeType 树类型{@link OrgTreeTypeEnum}
     * @param disabled 是否包含禁用的组织节点
     * @return {@code List<OrgUnit>}
     */
    List<OrgUnit> treeSearch(String name, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 组织节点名称
     * @param treeType 树类型{@link OrgTreeTypeEnum}
     * @param dnName dn
     * @param disabled 是否包含禁用的组织节点
     * @return {@code List<OrgUnit>}
     */
    List<OrgUnit> treeSearch(String name, OrgTreeTypeEnum treeType, String dnName, Boolean disabled);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 组织节点名称
     * @param treeType 树类型{@link OrgTreeTypeEnum}
     * @param disabled 是否禁用
     * @return {@code List<OrgUnit>}
     */
    List<OrgUnit> treeSearch4DeptManager(String name, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 通过父节点 id 获取作为可作为父亲节点的节点（只可能是组织机构和部门）
     *
     * @param parentId 父id
     * @return {@code List<OrgUnit> }
     */
    List<OrgUnit> listOrgUnitsAsParentByParentId(String parentId);

}
