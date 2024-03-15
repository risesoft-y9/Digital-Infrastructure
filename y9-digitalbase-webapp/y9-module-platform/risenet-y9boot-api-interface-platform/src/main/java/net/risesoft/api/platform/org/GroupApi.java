package net.risesoft.api.platform.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.dto.CreateGroupDTO;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;

/**
 * 用户组服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface GroupApi {

    /**
     * 向用户组添加人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/addPerson")
    Y9Result<Object> addPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("personId") @NotBlank String personId);

    /**
     * 创建用户组
     *
     * @param tenantId 租户id
     * @param createGroupDTO 用户组对象
     * @return {@code Y9Result<Group>} 通用请求返回对象 - data 是保存的用户组
     * @since 9.6.0
     */
    @PostMapping("/create")
    Y9Result<Group> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupJson") @Validated @RequestBody CreateGroupDTO createGroupDTO);

    /**
     * 删除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 根据id获得用户组对象
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return {@code Y9Result<Group>} 通用请求返回对象 - data 是用户组对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Group> get(@RequestParam @NotBlank String tenantId, @RequestParam @NotBlank String groupId);

    /**
     * 获取下一级用户组列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 父节点唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByParentId")
    Y9Result<List<Group>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 获取用户组内的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsByGroupId")
    Y9Result<List<Person>> listPersonsByGroupId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 将人员移除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/removePerson")
    Y9Result<Object> removePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("groupId") @NotBlank String groupId, @RequestParam("personId") @NotBlank String personId);

}