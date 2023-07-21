package net.risesoft.api.org;

import java.util.List;

import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;

/**
 * 用户组服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface GroupApi {

    /**
     * 用户组添加人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 是否添加成功
     * @since 9.6.0
     */
    boolean addPerson2Group(String tenantId, String groupId, String personId);

    /**
     * 创建用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象
     * @return Group 用户组对象
     * @since 9.6.0
     */
    Group createGroup(String tenantId, String groupJson);

    /**
     * 删除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @return true 删除成功，false 删除失败
     * @since 9.6.0
     */
    boolean deleteGroup(String tenantId, String groupId);

    /**
     * 根据id获取用户组对象
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return Group 用户组对象
     * @since 9.6.0
     */
    Group getGroup(String tenantId, String groupId);

    /**
     * 获取用户组父节点
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return OrgUnit 组织架构节点对象
     * @since 9.6.0
     */
    OrgUnit getParent(String tenantId, String groupId);

    /**
     * 根据租户id和路径获取所有用户组
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    List<Group> listByDn(String tenantId, String dn);

    /**
     * 获取组内的人员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listPersons(String tenantId, String groupId);

    /**
     * 从用户组移除人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 是否移除成功
     * @since 9.6.0
     */
    boolean removePerson(String tenantId, String groupId, String personId);

    /**
     * 更新用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象JSON字符串
     * @return Group 用户组对象
     * @since 9.6.0
     */
    Group updateGroup(String tenantId, String groupJson);
}