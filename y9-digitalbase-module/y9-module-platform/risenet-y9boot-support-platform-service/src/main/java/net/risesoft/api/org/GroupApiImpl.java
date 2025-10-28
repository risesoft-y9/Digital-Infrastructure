package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.GroupApi;
import net.risesoft.dto.platform.CreateGroupDTO;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 用户组服务组件
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
@RequestMapping(value = "/services/rest/v1/group", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupApiImpl implements GroupApi {

    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonService y9PersonService;

    /**
     * 向用户组添加人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> addPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonsToGroupsService.addPersons(groupId, new String[] {personId});
        return Y9Result.success();
    }

    /**
     * 创建用户组
     *
     * @param tenantId 租户id
     * @param createGroupDTO 用户组对象
     * @return {@code Y9Result<Group>} 通用请求返回对象 - data 是保存的用户组
     * @since 9.6.0
     */
    @Override
    public Y9Result<Group> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreateGroupDTO createGroupDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Group y9Group = Y9ModelConvertUtil.convert(createGroupDTO, Y9Group.class);
        y9Group = y9GroupService.saveOrUpdate(y9Group);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Group, Group.class));
    }

    /**
     * 删除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9GroupService.delete(groupId);
        return Y9Result.success();
    }

    /**
     * 根据id获得用户组对象
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return {@code Y9Result<Group>} 通用请求返回对象 - data 是用户组对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Group> get(@RequestParam @NotBlank String tenantId, @RequestParam @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Group y9Group = y9GroupService.findById(groupId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Group, Group.class));
    }

    /**
     * 获取下一级用户组列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 父节点唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Group>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = y9GroupService.listByParentId(parentId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9GroupList, Group.class));
    }

    /**
     * 获取用户组内的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersonsByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByGroupId(groupId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 将人员移除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> removePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonsToGroupsService.removePersons(groupId, new String[] {personId});
        return Y9Result.success();
    }
}
