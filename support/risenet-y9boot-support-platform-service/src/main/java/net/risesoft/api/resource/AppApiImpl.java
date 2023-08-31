package net.risesoft.api.resource;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.App;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.service.resource.Y9AppService;

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
@RestController
@RequestMapping(value = "/services/rest/app", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppApiImpl implements AppApi {

    private final Y9AppService y9AppService;
    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return
     */
    @Override
    public App findById(@RequestParam("appId") @NotBlank String appId) {
        Y9App y9App = y9AppService.findById(appId);
        return Y9ModelConvertUtil.convert(y9App, App.class);
    }

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统唯一标识
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    @Override
    public App findBySystemIdAndCustomId(@RequestParam("systemId") @NotBlank String systemId,
        @RequestParam("customId") @NotBlank String customId) {
        Y9App y9App = y9AppService.findBySystemIdAndCustomId(systemId, customId).orElse(null);
        return Y9ModelConvertUtil.convert(y9App, App.class);
    }

    /**
     * 根据系统名和自定义标识查找应用
     *
     * @param systemName 系统名
     * @param customId customId
     * @return App 应用
     * @since 9.6.0
     */
    @Override
    public App findBySystemNameAndCustomId(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("customId") @NotBlank String customId) {
        Y9App y9App = y9AppService.findBySystemNameAndCustomId(systemName, customId).orElse(null);
        return Y9ModelConvertUtil.convert(y9App, App.class);
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listAccessAppForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9App> appList = y9PersonToResourceAndAuthorityService.listAppsByAuthority(personId, authority);
        return Y9ModelConvertUtil.convert(appList, App.class);
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listAccessAppForPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9App> appList = y9PositionToResourceAndAuthorityService.listAppsByAuthority(positionId, authority);
        return Y9ModelConvertUtil.convert(appList, App.class);
    }

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listByCustomId(@RequestParam("customId") @NotBlank String customId) {
        List<Y9App> y9AppList = y9AppService.listByCustomId(customId);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
    }

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId systemId
     * @return List&lt;App&gt; 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listBySystemId(@RequestParam("systemId") @NotBlank String systemId) {
        List<Y9App> y9AppList = y9AppService.listBySystemId(systemId);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
    }

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName
     * @return
     * @since 9.6.0
     */
    @Override
    public List<App> listBySystemName(@RequestParam("systemName") @NotBlank String systemName) {
        List<Y9App> y9AppList = y9AppService.listBySystemName(systemName);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
    }

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @param systemId 系统id
     * @return App 应用
     * @since 9.6.0
     */
    @Override
    public App saveIsvApp(App app, @RequestParam("systemId") @NotBlank String systemId) {
        Y9App y9App = new Y9App();
        Y9BeanUtil.copyProperties(app, y9App);
        y9App = y9AppService.saveIsvApp(y9App, systemId);
        return Y9ModelConvertUtil.convert(y9App, App.class);
    }

}
