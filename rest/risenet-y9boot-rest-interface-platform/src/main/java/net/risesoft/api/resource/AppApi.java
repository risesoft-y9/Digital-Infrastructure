package net.risesoft.api.resource;

import java.util.List;

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
     * @return App 应用
     * @since 9.6.2
     */
    App findByIdInCache(String appId);

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统唯一标识
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    App findBySystemIdAndCustomId(String systemId, String customId);

    /**
     * 根据系统名和自定义标识查找应用
     *
     * @param systemName 系统名
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    App findBySystemNameAndCustomId(String systemName, String customId);

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    List<App> listAccessAppForPerson(String tenantId, String personId, Integer authority);

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    List<App> listAccessAppForPosition(String tenantId, String positionId, Integer authority);

    /**
     * 根据 customId 获取应用列表
     *
     * @param customId customId
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    List<App> listByCustomId(String customId);

    /**
     * 根据 systemId 获取应用列表
     *
     * @param systemId systemId
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    List<App> listBySystemId(String systemId);

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName 系统名称
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    List<App> listBySystemName(String systemName);

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @param systemEntityId 系统id
     * @return App 应用
     * @since 9.6.0
     */
    App saveIsvApp(App app, String systemEntityId);
}
