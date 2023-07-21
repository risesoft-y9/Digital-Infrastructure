package net.risesoft.api.customgroup;

import java.util.List;

import net.risesoft.model.CustomGroup;
import net.risesoft.model.CustomGroupMember;
import net.risesoft.model.Person;
import net.risesoft.pojo.Y9Page;

/**
 * 自定义用户组组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface CustomGroupApi {

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @since 9.6.0
     */
    void addMember(String tenantId, String customGroupId, List<String> orgUnitList);

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 删除是否成功
     * @since 9.6.0
     */
    boolean deleteAllGroup(String tenantId, List<String> groupIds);

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return CustomGroup 自定义用户组对象
     * @since 9.6.0
     */
    CustomGroup findCustomGroupByCustomId(String tenantId, String customId);

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    CustomGroup findCustomGroupById(String tenantId, String personId, String groupId);

    /**
     * 根据用户组id 解析该用户组下所有人员
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return List&lt;Person&gt; 人员列表
     * @since 9.6.0
     */
    List<Person> listAllPersonByGroupId(String tenantId, String groupId);

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return List&lt;CustomGroup&gt; 用户组对象列表
     * @since 9.6.0
     */
    List<CustomGroup> listCustomGroupByUserId(String tenantId, String personId);

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return List&lt;CustomGroupMember&gt; 用户组成员列表
     * @since 9.6.0
     */
    List<CustomGroupMember> listCustomGroupMemberByGroupId(String tenantId, String personId, String groupId);

    /**
     * 根据用户组id和成员类型，获取用户组员列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param groupId 用户组id
     * @param memberType 成员类型，如：Person、Position
     * @return List&lt;CustomGroupMember&gt; 用户组员列表
     * @since 9.6.0
     */
    List<CustomGroupMember> listCustomGroupMemberByGroupIdAndMemberType(String tenantId, String personId,
        String groupId, String memberType);

    /**
     * 分页获取自定义用户组
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Y9Page&lt;Map&lt;String, Object&gt;&gt;
     * @since 9.6.0
     */
    Y9Page<CustomGroup> pageCustomGroupByPersonId(String tenantId, String personId, int page, int rows);

    /**
     * 获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Y9Page&lt;Map&lt;String, Object&gt;&gt; 人员分页列表
     * @since 9.6.0
     */
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(String tenantId, String groupId, int page, int rows);

    /**
     * 根据用户组id和成员类型，获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param memberType 成员类型，如：Person、Position
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Map&lt;String, Object&gt; 人员分页列表
     * @since 9.6.0
     */
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(String tenantId, String groupId,
        String memberType, int page, int rows);

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIdList id集合
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    boolean removeMembers(String tenantId, List<String> memberIdList);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    CustomGroup saveCustomGroup(String tenantId, CustomGroup customGroup);

    /**
     * 保存用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    boolean saveCustomGroupOrder(String tenantId, List<String> sortIds);

    /**
     * 保存成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序后的Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    boolean saveMemberOrder(String tenantId, List<String> memberIds);

    /**
     * 保存或者更新用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupId 用户组Id
     * @param groupName 用户组名称
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    CustomGroup saveOrUpdateCustomGroup(String tenantId, String personId, List<String> personIds, String groupId,
        String groupName);

    /**
     * 共享用户组
     *
     * @param tenantId 租户id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 分享是否成功
     * @since 9.6.0
     */
    boolean shareCustomGroup(String tenantId, List<String> personIds, List<String> groupIds);
}