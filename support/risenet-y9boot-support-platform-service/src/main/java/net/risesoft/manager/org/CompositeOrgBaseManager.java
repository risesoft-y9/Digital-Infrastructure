package net.risesoft.manager.org;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.OrgTypeEnum;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface CompositeOrgBaseManager {

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

}
