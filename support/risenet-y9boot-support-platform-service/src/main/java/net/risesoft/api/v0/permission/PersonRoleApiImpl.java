package net.risesoft.api.v0.permission;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Role;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 人员角色组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0PersonRoleApiImpl")
@RequestMapping(value = "/services/rest/personRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class PersonRoleApiImpl implements PersonRoleApi {

    private final Y9PersonToRoleService y9PersonToRoleService;

    /**
     * 根据人员id获取该人员拥有的角色个数
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return long 角色数
     * @since 9.6.0
     */
    @Override
    public long countByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.countByPersonId(personId);
    }

    /**
     * 判断人员是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @return {@link Boolean}
     * @since 9.6.0
     */
    @Override
    public Boolean hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.hasRoleByCustomId(personId, customId);
    }

    /**
     * 根据人员id判断该人员是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param personId 人员id
     * @return boolean
     * @since 9.6.0
     */
    @Override
    public Boolean hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.hasPublicRole(personId, roleName);
    }

    /**
     * 判断人员是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param personId 人员id
     * @return {@link Boolean}
     * @since 9.6.0
     */
    @Override
    public Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.hasRole(personId, roleId);
    }

    /**
     * 根据人员id判断改人员是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param personId 人员id
     * @return Boolean 是否拥有
     * @since 9.6.0
     */
    @Override
    public Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.hasRole(personId, systemName, roleName, properties);
    }

    /**
     * 根据角色Id获取角色下所有人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersonsByRoleId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonToRoleService.listPersonsByRoleId(roleId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据人员id获取所有关联的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List<Role> 角色对象集合
     * @since 9.6.0
     */
    @Override
    public List<Role> listRolesByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Role> y9RoleList = y9PersonToRoleService.listRolesByPersonId(personId);
        List<Role> roleList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return roleList;
    }
}
