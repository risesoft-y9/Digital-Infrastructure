package y9.client.platform.permission;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.permission.PositionResourceApi;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Resource;

/**
 * 岗位资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionResourceApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/positionResource")
public interface PositionResourceApiClient extends PositionResourceApi {

    /**
     * 判断person对resource是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param resourceId 资源唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return Boolean 是否有权限
     * @since 9.6.0
     */
    @Override
    @GetMapping("/hasPermission")
    boolean hasPermission(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("resourceId") String resourceId, @RequestParam("authority") Integer authority);

    /**
     * 判断岗位对 customId 对应的资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return boolean
     * @since 9.6.0
     */
    @Override
    @GetMapping("/hasPermissionByCustomId")
    boolean hasPermissionByCustomId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("customId") String customId, @RequestParam("authority") Integer authority);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子菜单
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubMenus")
    List<Resource> listSubMenus(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("authority") Integer authority, @RequestParam("resourceId") String resourceId);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子节点
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubResources")
    List<Resource> listSubResources(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("authority") Integer authority, @RequestParam(name = "resourceId", required = false) String resourceId);
}
