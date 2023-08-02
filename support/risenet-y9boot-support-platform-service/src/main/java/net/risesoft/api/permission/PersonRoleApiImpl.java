package net.risesoft.api.permission;

import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9Page;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

import javax.validation.constraints.NotBlank;

/**
 * 权限查看组件
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
@RequestMapping(value = "/services/rest/personRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
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
    public long countByPersonId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
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
    public Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("positionId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToRoleService.hasRole(personId, customId);
    }

    /**
     * 获取人员的权限分页列表
     *
     * @param tenantId 租户id
     * @param personId 人员ID
     * @param type 查询类型，0：查询所有，1：查询拥有的角色
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page 第几页
     * @param rows 多少条数据
     * @return Map<String, Object>
     * @since 9.6.0
     */
    @Override
    public Y9Page<Map<String, Object>> pagePersonPermission(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("type") String type, @RequestParam("systemCnName") String systemCnName, @RequestParam("appName") String appName,
                                                            @RequestParam("roleName") String roleName, @RequestParam("page") int page, @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PersonToRoleService.pagePersonPermission(personId, type, systemCnName, appName, roleName, page, rows);
    }

    /**
     * 获取个人权限分页列表（拥有门户网站的权限查看角色才能查看到特殊角色）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @param type 节点类型,0：查询所有，1：查询拥有的角色
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page page
     * @param rows rows
     * @return Map<String, Object>
     * @since 9.6.0
     */
    @Override
    public Y9Page<Map<String, Object>> pagePersonPermissionWithSpecial(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("type") String type, @RequestParam("systemCnName") String systemCnName, @RequestParam("appName") String appName,
                                                                       @RequestParam("roleName") String roleName, @RequestParam("page") int page, @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PersonToRoleService.pagePersonAccessPermission(personId, type, systemCnName, appName, roleName, page, rows);
    }

}
