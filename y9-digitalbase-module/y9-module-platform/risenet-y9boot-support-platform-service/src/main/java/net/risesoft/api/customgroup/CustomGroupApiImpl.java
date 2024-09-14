package net.risesoft.api.customgroup;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.entity.Y9CustomGroup;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9CustomGroupService;
import net.risesoft.service.relation.Y9CustomGroupMembersService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/customGroup", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomGroupApiImpl implements CustomGroupApi {

    private final Y9CustomGroupMembersService customGroupMembersService;
    private final Y9CustomGroupService customGroupService;

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> addMember(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customGroupId") @NotBlank String customGroupId,
        @RequestParam("orgUnitList") @NotEmpty List<String> orgUnitList) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupMembersService.save(orgUnitList, customGroupId);
        return Y9Result.success();
    }

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deleteAllGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupService.delete(groupIds);
        return Y9Result.success();
    }

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是查找的自定义用户组
     * @since 9.6.0
     */
    @Override
    public Y9Result<CustomGroup> findCustomGroupByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9CustomGroup y9CustomGroup = customGroupService.findByCustomId(customId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9CustomGroup, CustomGroup.class));
    }

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是查找的自定义用户组
     * @since 9.6.0
     */
    @Override
    public Y9Result<CustomGroup> findCustomGroupById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9CustomGroup customGroup = customGroupService.findById(groupId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(customGroup, CustomGroup.class));
    }

    /**
     * 根据id解析该自定义用户组下的人员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是查找的人员列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listAllPersonByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = customGroupMembersService.listAllPersonsByGroupId(groupId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return {@code Y9Result<List<CustomGroup>>} 通用请求返回对象 - data 是查找的用户组列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<CustomGroup>> listCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9CustomGroup> y9CustomGroupList = customGroupService.listByPersonId(personId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9CustomGroupList, CustomGroup.class));
    }

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return {@code Y9Result<List<CustomGroupMember>>} 通用请求返回对象 - data 是查找的用户组成员列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<CustomGroupMember>> listCustomGroupMemberByGroupId(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId,
        @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9CustomGroupMember> y9CustomGroupMemberList = customGroupMembersService.listByGroupId(groupId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9CustomGroupMemberList, CustomGroupMember.class));
    }

    /**
     * 根据用户组id和成员类型，获取用户组成员列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param groupId 用户组id
     * @param memberType 成员类型 {@link OrgTypeEnum}
     * @return {@code Y9Result<List<CustomGroupMember>>} 通用请求返回对象 - data 是查找的用户组成员列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<CustomGroupMember>> listCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") String personId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("memberType") OrgTypeEnum memberType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9CustomGroupMember> y9CustomGroupMemberList =
            customGroupMembersService.listByGroupIdAndMemberType(groupId, memberType);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9CustomGroupMemberList, CustomGroupMember.class));
    }

    /**
     * 根据人员id分页获取其自定义用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<CustomGroup>} 通用分页请求返回对象 - rows 是返回的用户组列表
     * @since 9.6.0
     */
    @Override
    public Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9CustomGroup> y9CustomGroupPage = customGroupService.pageByPersonId(personId, pageQuery);
        return Y9Page.success(pageQuery.getPage(), y9CustomGroupPage.getTotalPages(),
            y9CustomGroupPage.getTotalElements(),
            Y9ModelConvertUtil.convert(y9CustomGroupPage.getContent(), CustomGroup.class));
    }

    /**
     * 根据自定义用户组id分页获取其自定义用户组成员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<CustomGroupMember>} 通用分页请求返回对象 - rows 是返回的用户组成员列表
     * @since 9.6.0
     */
    @Override
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9CustomGroupMember> y9CustomGroupMemberPage = customGroupMembersService.pageByGroupId(groupId, pageQuery);
        return Y9Page.success(pageQuery.getPage(), y9CustomGroupMemberPage.getTotalPages(),
            y9CustomGroupMemberPage.getTotalElements(),
            Y9ModelConvertUtil.convert(y9CustomGroupMemberPage.getContent(), CustomGroupMember.class));
    }

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
    @Override
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("groupId") @NotBlank String groupId,
        @RequestParam("memberType") OrgTypeEnum memberType, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9CustomGroupMember> y9CustomGroupMemberPage =
            customGroupMembersService.pageByGroupIdAndMemberType(groupId, memberType, pageQuery);
        return Y9Page.success(pageQuery.getPage(), y9CustomGroupMemberPage.getTotalPages(),
            y9CustomGroupMemberPage.getTotalElements(),
            Y9ModelConvertUtil.convert(y9CustomGroupMemberPage.getContent(), CustomGroupMember.class));
    }

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIds 用户组成员id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> removeMembers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupMembersService.delete(memberIds);
        return Y9Result.success();
    }

    /**
     * 保存自定义用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return {@code Y9Result<CustomGroup>} 通用请求返回对象 - data 是保存的自定义用户组
     * @since 9.6.0
     */
    @Override
    public Y9Result<CustomGroup> saveCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody CustomGroup customGroup) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9CustomGroup y9CustomGroup = new Y9CustomGroup();
        Y9BeanUtil.copyProperties(customGroup, y9CustomGroup);
        Y9CustomGroup savedY9CustomGroup = customGroupService.save(y9CustomGroup);
        return Y9Result.success(Y9ModelConvertUtil.convert(savedY9CustomGroup, CustomGroup.class));
    }

    /**
     * 保存自定义用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveCustomGroupOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("sortIds") @NotEmpty List<String> sortIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupService.saveCustomGroupOrder(sortIds);
        return Y9Result.success();
    }

    /**
     * 保存自定义用户组成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序的用户组成员id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveMemberOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupMembersService.saveOrder(memberIds);
        return Y9Result.success();
    }

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
    @Override
    public Y9Result<CustomGroup> saveOrUpdateCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId,
        @RequestParam("personIds") @NotEmpty List<String> personIds, @RequestParam("groupId") String groupId,
        @RequestParam("groupName") @NotBlank String groupName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9CustomGroup customGroup = customGroupService.saveOrUpdate(personId, personIds, groupId, groupName);
        return Y9Result.success(Y9ModelConvertUtil.convert(customGroup, CustomGroup.class));
    }

    /**
     * 分享用户组给其他人使用
     *
     * @param tenantId 租户id
     * @param personIds 人员id，多个用英文逗号,隔开
     * @param groupIds 用户组id，多个用英文逗号,隔开
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> shareCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personIds") @NotEmpty List<String> personIds,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupService.share(personIds, groupIds);
        return Y9Result.success();
    }

}
