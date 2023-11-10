package net.risesoft.api.v0.customgroup;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@Validated
@Deprecated
public interface CustomGroupApi {

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @since 9.6.0
     */
    @GetMapping("/addMember")
    void addMember(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customGroupId") @NotBlank String customGroupId,
        @RequestParam("orgUnitList") @NotEmpty List<String> orgUnitList);

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 删除是否成功
     * @since 9.6.0
     */
    @PostMapping("/deleteAllGroup")
    boolean deleteAllGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds);

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return CustomGroup 自定义用户组对象
     * @since 9.6.0
     */
    @GetMapping("/findCustomGroupByCustomId")
    CustomGroup findCustomGroupByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @GetMapping("/findCustomGroupById")
    CustomGroup findCustomGroupById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据用户组id 解析该用户组下所有人员
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return List&lt;Person&gt; 人员列表
     * @since 9.6.0
     */
    @GetMapping("/listAllPersonByGroupId")
    List<Person> listAllPersonByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return List&lt;CustomGroup&gt;
     * @since 9.6.0
     */
    @GetMapping("/listCustomGroupByUserId")
    List<CustomGroup> listCustomGroupByUserId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return List&lt;CustomGroupMember&gt; 用户组成员列表
     * @since 9.6.0
     */
    @GetMapping("/listCustomGroupMemberByGroupId")
    List<CustomGroupMember> listCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId);

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
    @GetMapping("/listCustomGroupMemberByGroupIdAndMemberType")
    List<CustomGroupMember> listCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") String personId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("memberType") @NotBlank String memberType);

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
    @GetMapping("/pageCustomGroupByPersonId")
    Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

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
    @GetMapping("/pageCustomGroupMemberByGroupId")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

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
    @GetMapping("/pageCustomGroupMemberByGroupIdAndMemberType")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("groupId") @NotBlank String groupId,
        @RequestParam("memberType") @NotBlank String memberType, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIds ids,多个“,”隔开
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @PostMapping("/removeMembers")
    boolean removeMembers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @PostMapping("/saveCustomGroup")
    CustomGroup saveCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId, CustomGroup customGroup);

    /**
     * 保存用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @PostMapping("/saveCustomGroupOrder")
    boolean saveCustomGroupOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("sortIds") @NotEmpty List<String> sortIds);

    /**
     * 保存成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序后的Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @PostMapping("/saveMemberOrder")
    boolean saveMemberOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupId 用户组Id
     * @param groupName 用户组名称
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @PostMapping("/saveOrUpdateCustomGroup")
    CustomGroup saveOrUpdateCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId,
        @RequestParam("personIds") @NotEmpty List<String> personIds, @RequestParam("groupId") String groupId,
        @RequestParam("groupName") @NotBlank String groupName);

    /**
     * 共享用户组
     *
     * @param tenantId 租户id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 分享是否成功
     * @since 9.6.0
     */
    @PostMapping("/shareCustomGroup")
    boolean shareCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personIds") @NotEmpty List<String> personIds,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds);

}