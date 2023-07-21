package y9.client.platform.permission;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.pojo.Y9Page;

/**
 * 人员角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonRoleApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/personRole", primary = false)
public interface PersonRoleApiClient extends PersonRoleApi {

    /**
     * 根据人员id获取该人员拥有的角色数
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return long 角色数
     * @since 9.6.0
     */
    @Override
    @GetMapping("/countByPersonId")
    long countByPersonId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId);

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
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String personId, @RequestParam("customId") String customId);

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
     * @return Y9Page<Map<String, Object>> 角色权限列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pagePersonPermission")
    Y9Page<Map<String, Object>> pagePersonPermission(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("type") String type, @RequestParam("systemCnName") String systemCnName, @RequestParam("appName") String appName,
        @RequestParam("roleName") String roleName, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取个人权限分页列表（拥有门户网站的权限查看角色才能查看到特殊角色）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @param type 节点类型,0：查询所有，1：查询拥有的角色
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page 第几页
     * @param rows 多少条数据
     * @return Y9Page<Map<String, Object>> 角色权限列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pagePersonPermissionWithSpecial")
    Y9Page<Map<String, Object>> pagePersonPermissionWithSpecial(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("type") String type, @RequestParam("systemCnName") String systemCnName, @RequestParam("appName") String appName,
        @RequestParam("roleName") String roleName, @RequestParam("page") int page, @RequestParam("rows") int rows);

}
