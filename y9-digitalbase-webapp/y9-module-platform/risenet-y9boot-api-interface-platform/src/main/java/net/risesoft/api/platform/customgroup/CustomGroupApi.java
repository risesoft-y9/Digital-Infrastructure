package net.risesoft.api.platform.customgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;

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
public interface CustomGroupApi {

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/addMember")
    Y9Result<Object> addMember(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customGroupId") @NotBlank String customGroupId,
        @RequestParam("orgUnitList") @NotEmpty List<String> orgUnitList);

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/deleteAllGroup")
    Y9Result<Object> deleteAllGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds);

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是查找的自定义用户组
     * @since 9.6.0
     */
    @GetMapping("/findCustomGroupByCustomId")
    Y9Result<CustomGroup> findCustomGroupByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是查找的自定义用户组
     * @since 9.6.0
     */
    @GetMapping("/findCustomGroupById")
    Y9Result<CustomGroup> findCustomGroupById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据id解析该自定义用户组下的人员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是查找的人员列表
     * @since 9.6.0
     */
    @GetMapping("/listAllPersonByGroupId")
    Y9Result<List<Person>> listAllPersonByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return {@code Y9Result<List<CustomGroup>>} 通用请求返回对象 - data 是查找的用户组列表
     * @since 9.6.0
     */
    @GetMapping("/listCustomGroupByUserId")
    Y9Result<List<CustomGroup>> listCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return {@code Y9Result<List<CustomGroupMember>>} 通用请求返回对象 - data 是查找的用户组成员列表
     * @since 9.6.0
     */
    @GetMapping("/listCustomGroupMemberByGroupId")
    Y9Result<List<CustomGroupMember>> listCustomGroupMemberByGroupId(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId,
        @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据用户组id和成员类型，获取用户组成员列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param groupId 用户组id
     * @param memberType 成员类型
     * @return {@code Y9Result<List<CustomGroupMember>>} 通用请求返回对象 - data 是查找的用户组成员列表
     * @since 9.6.0
     */
    @GetMapping("/listCustomGroupMemberByGroupIdAndMemberType")
    Y9Result<List<CustomGroupMember>> listCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") String personId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("memberType") OrgTypeEnum memberType);

    /**
     * 根据人员id分页获取其自定义用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<CustomGroup>} 通用分页请求返回对象 - rows 是返回的用户组列表
     * @since 9.6.0
     */
    @GetMapping("/pageCustomGroupByPersonId")
    Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @Validated Y9PageQuery pageQuery);

    /**
     * 根据自定义用户组id分页获取其自定义用户组成员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<CustomGroupMember>} 通用分页请求返回对象 - rows 是返回的用户组成员列表
     * @since 9.6.0
     */
    @GetMapping("/pageCustomGroupMemberByGroupId")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @Validated Y9PageQuery pageQuery);

    /**
     * 根据自定义用户组id和成员类型分页获取其自定义用户组成员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param memberType 成员类型
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<CustomGroupMember>}
     * @since 9.6.0
     */
    @GetMapping("/pageCustomGroupMemberByGroupIdAndMemberType")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("groupId") @NotBlank String groupId,
        @RequestParam("memberType") OrgTypeEnum memberType, @Validated Y9PageQuery pageQuery);

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIds 用户组成员id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/removeMembers")
    Y9Result<Object> removeMembers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds);

    /**
     * 保存自定义用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是保存的自定义用户组
     * @since 9.6.0
     */
    @PostMapping("/saveCustomGroup")
    Y9Result<CustomGroup> saveCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody CustomGroup customGroup);

    /**
     * 保存自定义用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/saveCustomGroupOrder")
    Y9Result<Object> saveCustomGroupOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("sortIds") @NotEmpty List<String> sortIds);

    /**
     * 保存自定义用户组成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序的用户组成员id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/saveMemberOrder")
    Y9Result<Object> saveMemberOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param personIds 人员id，多个用英文逗号,隔开
     * @param groupId 用户组Id
     * @param groupName 用户组名称
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是保存的自定义用户组
     * @since 9.6.0
     */
    @PostMapping("/saveOrUpdateCustomGroup")
    Y9Result<CustomGroup> saveOrUpdateCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId,
        @RequestParam("personIds") @NotEmpty List<String> personIds, @RequestParam("groupId") String groupId,
        @RequestParam("groupName") @NotBlank String groupName);

    /**
     * 分享用户组给其他人使用
     *
     * @param tenantId 租户id
     * @param personIds 人员id，多个用英文逗号,隔开
     * @param groupIds 用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/shareCustomGroup")
    Y9Result<Object> shareCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personIds") @NotEmpty List<String> personIds,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds);

}