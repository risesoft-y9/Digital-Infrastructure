package net.risesoft.api.v0.customgroup;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.customgroup.CustomGroupApi;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.CustomGroupMemberQuery;
import net.risesoft.service.org.Y9CustomGroupService;
import net.risesoft.service.relation.Y9CustomGroupMembersService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;

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
@RestController(value = "v0CustomGroupApiImpl")
@RequestMapping(value = "/services/rest/customGroup", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class CustomGroupApiImpl implements CustomGroupApi {

    private final Y9CustomGroupMembersService customGroupMembersService;
    private final Y9CustomGroupService customGroupService;

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @since 9.6.0
     */
    @Override
    public void addMember(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customGroupId") @NotBlank String customGroupId,
        @RequestParam("orgUnitList") @NotEmpty List<String> orgUnitList) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupMembersService.save(orgUnitList, customGroupId);
    }

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 删除是否成功
     * @since 9.6.0
     */
    @Override
    public boolean deleteAllGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupService.delete(groupIds);
        return true;
    }

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return CustomGroup 自定义用户组对象
     * @since 9.6.0
     */
    @Override
    public CustomGroup findCustomGroupByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.findByCustomId(customId).orElse(null);
    }

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    public CustomGroup findCustomGroupById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.findById(groupId).orElse(null);
    }

    /**
     * 根据用户组id 解析该用户组下所有人员
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return {@code List<Person>} 人员列表
     * @since 9.6.0
     */
    @Override
    public List<Person> listAllPersonByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupMembersService.listAllPersonsByGroupId(groupId);
    }

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return {@code List<CustomGroup>} 用户组对象列表
     * @since 9.6.0
     */
    @Override
    public List<CustomGroup> listCustomGroupByUserId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.listByPersonId(personId);
    }

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return {@code List<CustomGroupMember>} 用户组成员列表
     * @since 9.6.0
     */
    @Override
    public List<CustomGroupMember> listCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupMembersService.list(new CustomGroupMemberQuery(groupId, null));
    }

    /**
     * 根据用户组id和成员类型，获取用户组员列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param groupId 用户组id
     * @param memberType 成员类型，如：Person、Position
     * @return {@code List<CustomGroupMember>} 用户组员列表
     * @since 9.6.0
     */
    @Override
    public List<CustomGroupMember> listCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") String personId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("memberType") @NotBlank String memberType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupMembersService
            .list(new CustomGroupMemberQuery(groupId, Y9EnumUtil.valueOf(OrgTypeEnum.class, memberType)));
    }

    /**
     * 分页获取自定义用户组
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return {@code Y9Page<CustomGroup>}
     * @since 9.6.0
     */
    @Override
    public Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("page") int page,
        @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.pageByPersonId(personId, new Y9PageQuery(page, rows));
    }

    /**
     * 获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return {@code Y9Page<CustomGroupMember>} 人员分页列表
     * @since 9.6.0
     */
    @Override
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("page") int page,
        @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        CustomGroupMemberQuery customGroupMemberQuery = new CustomGroupMemberQuery(groupId, null);
        return customGroupMembersService.page(customGroupMemberQuery, new Y9PageQuery(page, rows));
    }

    /**
     * 根据用户组id和成员类型，获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param memberType 成员类型，如：Person、Position
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return {@code Y9Page<CustomGroupMember>} 人员分页列表
     * @since 9.6.0
     */
    @Override
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("groupId") @NotBlank String groupId,
        @RequestParam("memberType") @NotBlank String memberType, @RequestParam("page") int page,
        @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        CustomGroupMemberQuery customGroupMemberQuery =
            new CustomGroupMemberQuery(groupId, Y9EnumUtil.valueOf(OrgTypeEnum.class, memberType));

        return customGroupMembersService.page(customGroupMemberQuery, new Y9PageQuery(page, rows));
    }

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIds ids,多个“,”隔开
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    public boolean removeMembers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupMembersService.delete(memberIds);
        return true;
    }

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    public CustomGroup saveCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId, CustomGroup customGroup) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.save(customGroup);
    }

    /**
     * 保存用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    public boolean saveCustomGroupOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("sortIds") @NotEmpty List<String> sortIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.saveCustomGroupOrder(sortIds);
    }

    /**
     * 保存成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序后的Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    public boolean saveMemberOrder(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("memberIds") @NotEmpty List<String> memberIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupMembersService.saveOrder(memberIds);
    }

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
    @Override
    public CustomGroup saveOrUpdateCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId,
        @RequestParam("personIds") @NotEmpty List<String> personIds, @RequestParam("groupId") String groupId,
        @RequestParam("groupName") @NotBlank String groupName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.saveOrUpdate(personId, personIds, groupId, groupName);
    }

    /**
     * 共享用户组
     *
     * @param tenantId 租户id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 分享是否成功
     * @since 9.6.0
     */
    @Override
    public boolean shareCustomGroup(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personIds") @NotEmpty List<String> personIds,
        @RequestParam("groupIds") @NotEmpty List<String> groupIds) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return customGroupService.share(personIds, groupIds);
    }

}
