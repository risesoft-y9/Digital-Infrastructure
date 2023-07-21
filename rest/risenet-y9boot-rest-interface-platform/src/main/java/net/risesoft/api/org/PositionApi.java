package net.risesoft.api.org;

import java.util.List;

import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * 岗位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface PositionApi {

    /**
     * 岗位增加人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return boolean true 移除成功，false 移除失败
     * @since 9.6.0
     */
    boolean addPerson(String tenantId, String positionId, String personId);

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    Position createPosition(String tenantId, String positionJson);

    /**
     * 根据岗位id删除岗位
     *
     * @param tenantId 租户id
     * @param positoinId 岗位id
     * @return boolean s是否删除成功
     * @since 9.6.0
     */
    boolean deletePosition(String tenantId, String positoinId);

    /**
     * 获取岗位父节点
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getParent(String tenantId, String positionId);

    /**
     * 根据id获得岗位对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return Position 岗位对象
     * @since 9.6.0
     */
    Position getPosition(String tenantId, String positionId);

    /**
     * 根据人员id和岗位id判断该人员是否拥有此岗位
     *
     * @param tenantId 租户id
     * @param positionName 岗位名称
     * @param personId 岗位唯一标识
     * @return boolean 是否拥有该岗位
     * @since 9.6.0
     */
    boolean hasPosition(String tenantId, String positionName, String personId);

    /**
     * 根据父节点获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Position> listByParentId(String tenantId, String parentId);

    /**
     * 根据用户ID,获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Position> listByPersonId(String tenantId, String personId);

    /**
     * 获取岗位的人员列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listPersons(String tenantId, String positionId);

    /**
     * 从岗位移除人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return boolean true 移除成功，false 移除失败
     * @since 9.6.0
     */
    boolean removePerson(String tenantId, String positionId, String personId);

    /**
     * 更新岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    Position updatePosition(String tenantId, String positionJson);
}
