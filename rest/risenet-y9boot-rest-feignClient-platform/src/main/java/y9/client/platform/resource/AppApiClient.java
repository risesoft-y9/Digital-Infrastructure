package y9.client.platform.resource;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.resource.AppApi;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.App;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/app")
public interface AppApiClient extends AppApi {

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return
     */
    @Override
    @GetMapping("/findByIdInCache")
    App findByIdInCache(@RequestParam("appId") String appId);

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统唯一标识
     * @param customId customId
     * @return App
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findBySystemIdAndCustomId")
    App findBySystemIdAndCustomId(@RequestParam("systemId") String systemId, @RequestParam("customId") String customId);

    /**
     * 根据系统名和自定义标识查找应用
     *
     * @param systemName 系统名
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findBySystemNameAndCustomId")
    App findBySystemNameAndCustomId(@RequestParam("systemName") String systemName, @RequestParam("customId") String customId);

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 操作类型(如：BROWSE、ADMIN)
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAccessAppForPerson")
    List<App> listAccessAppForPerson(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("authority") Integer authority);

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAccessAppForPosition")
    List<App> listAccessAppForPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("authority") Integer authority);

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByCustomId")
    List<App> listByCustomId(@RequestParam("customId") String customId);

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId systemId
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listBySystemId")
    List<App> listBySystemId(@RequestParam("systemId") String systemId);

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listBySystemName")
    List<App> listBySystemName(@RequestParam("systemName") String systemName);

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @param systemEntityId 系统id
     * @return App
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveIsvApp")
    App saveIsvApp(@SpringQueryMap App app, @RequestParam("systemEntityId") String systemEntityId);
}
