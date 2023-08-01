package net.risesoft.api.resource;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.App;

/**
 * 应用管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface AppApi {

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return
     */
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
    @GetMapping("/listAccessAppForPosition")
    List<App> listAccessAppForPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("authority") Integer authority);

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @GetMapping("/listByCustomId")
    List<App> listByCustomId(@RequestParam("customId") String customId);

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId systemId
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @GetMapping("/listBySystemId")
    List<App> listBySystemId(@RequestParam("systemId") String systemId);

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName
     * @return
     * @since 9.6.0
     */
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
    @PostMapping("/saveIsvApp")
    App saveIsvApp(App app, @RequestParam("systemEntityId") String systemEntityId);
}
