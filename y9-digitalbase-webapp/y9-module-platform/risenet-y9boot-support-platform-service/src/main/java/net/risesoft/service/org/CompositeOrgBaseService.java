package net.risesoft.service.org;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.SyncOrgUnits;

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
     * 根据指定id获取已删除的组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Optional<Y9OrgBase> findOrgUnitDeleted(String orgUnitId);

    /**
     * 获取组织节点所在组织机构
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Optional<Y9Organization> findOrgUnitOrganization(String orgUnitId);

    /**
     * 根据组织节点id，获取其父节点(只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Optional<Y9OrgBase> findOrgUnitParent(String orgUnitId);

    /**
     * 根据指定id获取ORGBase对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
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
     * 根据组织节点id获取所在委办局（可能是组织机构或部门）
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Y9OrgBase getOrgUnitBureau(String orgUnitId);

    /**
     * 根据指定id获取已删除的组织节点对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Y9OrgBase getOrgUnitDeleted(String orgUnitId);

    /**
     * 获取组织节点所在组织机构
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Y9Organization getOrgUnitOrganization(String orgUnitId);

    /**
     * 根据组织节点id，获取其父节点(只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Y9OrgBase getOrgUnitParent(String orgUnitId);

    /**
     * 根据syncId,orgType,needRecursion进行查找
     *
     * @param syncId 同步的组织机构id
     * @param orgType 机构类型：Organization（组织），Department（部门），Group（用户组），Position（岗位），Person（人员），PersonLink（人员）
     * @param needRecursion 是否递归
     * @return {@link HashMap}<{@link String}, {@link Serializable}>
     */
    HashMap<String, Serializable> getSyncMap(String syncId, OrgTypeEnum orgType, Integer needRecursion);

    SyncOrgUnits getSyncOrgUnits(String organizationId, OrgTypeEnum orgTypeEnum, boolean recursionRequired);

    /**
     * 获取机构树子节点
     *
     * @param id 组织节点id
     * @param treeType 树类型
     * @param disabled 节点是否禁用
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> getTree(String id, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 子域三员获取部门树
     *
     * @param id 组织节点id
     * @param treeType 树类型
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> getTree4DeptManager(String id, OrgTreeTypeEnum treeType);

    /**
     * 根据组织节点id向下递归获取所有组织节点
     *
     * @param orgId 组织节点id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> listAllOrgUnits(String orgId);

    /**
     * 根据父节点id,递归获取其下所有人员
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listAllPersonsRecursionDownward(String parentId);

    /**
     * 根据父节点id,递归获取其下所有没有禁用/禁用人员
     *
     * @param parentId 父节点id
     * @param disabled 是否禁用
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listAllPersonsRecursionDownward(String parentId, Boolean disabled);

    /**
     * 根据父节点id，递归获取其下所有岗位
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> listAllPositionsRecursionDownward(String parentId);

    /**
     * 根据父节点id,人员姓名，是否禁用，递归获取其下所有人员
     *
     * @param parentId 父节点id
     * @param name 组织节点名称
     * @param disabled 是否禁用
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> searchAllPersonsRecursionDownward(String parentId, String name, Boolean disabled);

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
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 组织节点名称
     * @param treeType 树类型
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> treeSearch(String name, OrgTreeTypeEnum treeType);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 组织节点名称
     * @param treeType 树类型
     * @param dnName dn
     * @param disabled
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> treeSearch(String name, OrgTreeTypeEnum treeType, String dnName, Boolean disabled);

    /**
     * 根据name，和结构树类型查询机构主体(不含禁用人员)
     *
     * @param name 组织节点名称
     * @param treeType 树类型
     * @param disabled 是否包含禁用的组织节点
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> treeSearch(String name, OrgTreeTypeEnum treeType, Boolean disabled);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 组织节点名称
     * @param treeType 树类型
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> treeSearch4DeptManager(String name, OrgTreeTypeEnum treeType);

    /**
     * 分页获取机构下的人员列表
     * 
     * @param orgId 机构id
     * @param type 0-查全量，1-查询没被禁用的
     * @param page
     * @param rows
     * @return
     */
    Page<Y9Person> personPage(String orgId, String type, int page, int rows);

    /**
     * 分页获取机构下的部门列表
     * 
     * @param orgId 机构id
     * @param page
     * @param rows
     * @return
     */
    Page<Y9Department> deptPage(String orgId, int page, int rows);
}
