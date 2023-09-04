package net.risesoft.service.org;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.OrgTypeEnum;

/**
 * 组合的组织节点 service
 * 
 * @author shidaobang
 * @date 2023/07/31
 * @since 9.6.3
 */
public interface CompositeOrgBaseService {

    /**
     * 排序
     *
     * @param orgUnitIds 组织节点id
     */
    void sort(String[] orgUnitIds);

    /**
     * 根据组织节点id获取所在委办局
     *
     * @param orgUnitId
     * @return
     */
    Y9OrgBase getOrgUnitBureau(String orgUnitId);

    /**
     * 递归得到 guid路径
     *
     * @param sb StringBuilder
     * @param y9OrgBase 组织节点
     */
    void getGuidPathRecursiveUp(StringBuilder sb, Y9OrgBase y9OrgBase);

    /**
     * 根据父节点id获取子节点最大的tabIndex
     *
     * @param parentId
     * @param orgType
     * @return
     */
    Integer getMaxSubTabIndex(String parentId, OrgTypeEnum orgType);

    /**
     * 递归向上获取排序序列号
     *
     * @param sb StringBuilder
     * @param y9OrgBase 组织节点
     */
    void getOrderedPathRecursiveUp(StringBuilder sb, Y9OrgBase y9OrgBase);

    /**
     * 根据指定id获取ORGBase对象(可以是org的任意类型)
     *
     * @param id
     * @return
     */
    Y9OrgBase getOrgBase(String id);

    /**
     * 根据指定id获取已删除的ORGBase对象(可以是org的任意类型)
     *
     * @param orgUnitId 组织节点id
     * @return
     */
    Y9OrgBase getOrgBaseDeletedByOrgUnitId(String orgUnitId);

    /**
     * 根据组织节点id，获取父节点(parent只可能是组织机构和部门)
     *
     * @param orgUnitId 组织节点id
     * @return ORGBase
     */
    Y9OrgBase getParent(String orgUnitId);

    /**
     * 根据syncId,orgType,needRecursion进行查找
     *
     * @param syncId 同步id
     * @param orgType 机构类型：Organization（组织），Department（部门），Group（用户组），Position（岗位），Person（人员），PersonLink（人员）
     * @param needRecursion 是否递归
     * @return
     */
    HashMap<String, Serializable> getSyncMap(String syncId, String orgType, Integer needRecursion);

    /**
     * 获取机构树子节点 get the children node of the current node
     *
     * @param id
     * @param treeType
     * @param disabled
     * @return
     */
    List<Y9OrgBase> getTree(String id, String treeType, boolean disabled);

    /**
     * 获取组织机构树
     *
     * @param id 节点id
     * @param treeType 节点类型
     * @param isPersonIncluded 是否包含人员
     * @param disabled 人员是否禁用
     * @return
     */
    List<Y9OrgBase> getTree(String id, String treeType, boolean isPersonIncluded, boolean disabled);

    /**
     * 子域三员获取部门树
     *
     * @param id
     * @param treeType
     * @return
     */
    List<Y9OrgBase> getTree4DeptManager(String id, String treeType);

    /**
     * 根据组织节点id向下递归获取所有组织节点
     *
     * @param orgId
     * @return
     */
    List<Y9OrgBase> listAllOrgUnits(String orgId);

    /**
     * 根据父节点id,递归获取其下所有人员
     *
     * @param parentId
     * @return
     */
    List<Y9Person> listAllPersonsRecursionDownward(String parentId);

    /**
     * 根据父节点id,递归获取其下所有没有禁用/禁用人员
     *
     * @param parentId
     * @param disabled
     * @return
     */
    List<Y9Person> listAllPersonsRecursionDownward(String parentId, Boolean disabled);

    /**
     * 根据父节点id，递归获取其下所有岗位
     *
     * @param parentId
     * @return
     */
    List<Y9Position> listAllPositionsRecursionDownward(String parentId);

    /**
     * 递归修改子节点的属性
     *
     * @param parent
     */
    void recursivelyUpdateProperties(Y9OrgBase parent);

    /**
     * 根据父节点id,人员姓名，是否禁用，递归获取其下所有人员
     *
     * @param parentId
     * @param disabled
     * @param name
     * @return
     */
    List<Y9Person> searchAllPersonsRecursionDownward(String parentId, Boolean disabled, String name);

    /**
     * 同步数据到消息中间件
     *
     * @param syncId
     * @param orgType
     * @param needRecursion
     * @return
     */
    void sync(String syncId, String orgType, Integer needRecursion);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name
     * @param treeType
     * @return
     */
    List<Y9OrgBase> treeSearch(String name, String treeType);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name
     * @param treeType
     * @param dnName
     * @return
     */
    List<Y9OrgBase> treeSearch(String name, String treeType, String dnName);

    /**
     * 根据name，和结构树类型查询机构主体(不含禁用人员)
     *
     * @param name
     * @param treeType
     * @param isDisabledIncluded
     * @return
     */
    List<Y9OrgBase> treeSearch(String name, String treeType, boolean isDisabledIncluded);

    /**
     * 获取组织节点所在组织机构
     *
     * @param orgUnitId 组织节点id
     * @return {@link Y9OrgBase}
     */
    Y9OrgBase getOrgUnitOrganization(String orgUnitId);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name
     * @param treeType
     * @return
     */
    List<Y9OrgBase> treeSearch4DeptManager(String name, String treeType);
}
