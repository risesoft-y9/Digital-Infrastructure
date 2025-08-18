package net.risesoft.api.platform.v0.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Result;

/**
 * 应用管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
@Deprecated
public interface AppApi {

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return App
     */
    @GetMapping("/findById")
    App findById(@RequestParam("appId") @NotBlank String appId);

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统唯一标识
     * @param customId customId
     * @return App
     * @since 9.6.0
     */
    @GetMapping("/findBySystemIdAndCustomId")
    App findBySystemIdAndCustomId(@RequestParam("systemId") @NotBlank String systemId,
        @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据系统名和自定义标识查找应用
     *
     * @param systemName 系统名
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    @GetMapping("/findBySystemNameAndCustomId")
    App findBySystemNameAndCustomId(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("customId") @NotBlank String customId);

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
    List<App> listAccessAppForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority);

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
    List<App> listAccessAppForPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority);

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @GetMapping("/listByCustomId")
    List<App> listByCustomId(@RequestParam("customId") @NotBlank String customId);

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId 系统Id
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @GetMapping("/listBySystemId")
    List<App> listBySystemId(@RequestParam("systemId") @NotBlank String systemId);

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName 系统名称
     * @return List&lt;App&gt;
     * @since 9.6.0
     */
    @GetMapping("/listBySystemName")
    List<App> listBySystemName(@RequestParam("systemName") @NotBlank String systemName);

    /**
     * 注册应用
     *
     * @param systemName 系统名称
     * @param name 应用名称
     * @param url 链接地址
     * @param customId customId
     * @param tenantGuid 租户id
     * @return Y9Result&lt;App&gt;
     */
    @PostMapping("/registryApp")
    Y9Result<App> registryApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("name") @NotBlank String name, @RequestParam("url") @NotBlank String url,
        @RequestParam("customId") String customId, @RequestParam("tenantGuid") String tenantGuid);

    /**
     * 注册系统和应用信息
     *
     * @param systemName 系统名称
     * @param systemCnName 系统中文名称
     * @param isvGuid 租户id
     * @param contextPath 系统上下文
     * @param appName 应用名称
     * @param url 链接地址
     * @param customId customId
     * @return Y9Result&lt;App&gt;
     */
    @PostMapping("/registrySystemAndApp")
    Y9Result<App> registrySystemAndApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("systemCnName") @NotBlank String systemCnName, @RequestParam("isvGuid") String isvGuid,
        @RequestParam("contextPath") String contextPath, @RequestParam("appName") @NotBlank String appName,
        @RequestParam("url") @NotBlank String url, @RequestParam("customId") String customId);

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @return App
     * @since 9.6.0
     */
    @PostMapping("/saveIsvApp")
    App saveIsvApp(@RequestBody App app);
}
