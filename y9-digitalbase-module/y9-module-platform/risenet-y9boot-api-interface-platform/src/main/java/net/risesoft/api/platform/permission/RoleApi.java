package net.risesoft.api.platform.permission;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Result;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface RoleApi {

    /**
     * 添加人员到角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/addPerson")
    Y9Result<Object> addPerson(@RequestParam("personId") @NotBlank String personId,
                               @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 新增角色节点
     *
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param parentId 父节点id
     * @param customId 自定义id
     * @param type 角色类型，node或者role
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是保存的角色对象
     * @since 9.6.0
     */
    @PostMapping("/createRole")
    Y9Result<Role> createRole(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName,
                              @RequestParam("parentId") String parentId, @RequestParam("customId") String customId,
                              @RequestParam("type") RoleTypeEnum type);

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/deleteRole")
    Y9Result<Object> deleteRole(@RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据customId和parentId查找角色对象
     *
     * @param customId customId
     * @param parentId 角色的父节点id
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是角色对象
     * @since 9.6.0
     */
    @GetMapping("/findByCustomIdAndParentId")
    Y9Result<Role> findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
                                             @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 根据id获取相应角色节点
     *
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是角色对象
     * @since 9.6.0
     */
    @GetMapping("/getRole")
    Y9Result<Role> getRole(@RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据角色Id获取关联的组织节点集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 数据类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点集合
     * @since 9.6.0
     */
    @GetMapping("/listOrgUnitsById")
    Y9Result<List<OrgUnit>> listOrgUnitsById(@RequestParam("tenantId") @NotBlank String tenantId,
                                             @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgType") OrgTypeEnum orgType);

    /**
     * 根据角色Id获取直接关联的人员对象集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsById")
    Y9Result<List<Person>> listPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
                                           @RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRoleByParentId")
    Y9Result<List<Role>> listRoleByParentId(@RequestParam("roleId") @NotBlank String roleId);

    /**
     * 删除角色关联的人员
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/removePerson")
    Y9Result<Object> removePerson(@RequestParam("personId") @NotBlank String personId,
                                  @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId);
}
