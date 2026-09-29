package net.risesoft.api.v0.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.v0.resource.AppApi;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

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
@RestController(value = "v0AppApiImpl")
@RequestMapping(value = "/services/rest/app", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class AppApiImpl implements AppApi {

    private final Y9AppService y9AppService;
    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9PositionToResourceService y9PositionToResourceService;
    private final Y9SystemService y9SystemService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9TenantAppService y9TenantAppService;

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return App 应用
     */
    @Override
    public App findById(@RequestParam("appId") @NotBlank String appId) {
        return y9AppService.findById(appId).orElse(null);
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
        return y9AppService.findBySystemIdAndCustomId(systemId, customId).orElse(null);
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
        return y9AppService.findBySystemNameAndCustomId(systemName, customId).orElse(null);
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code List<App>} 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listAccessAppForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority) {

        return y9PersonToResourceService.listAppsByAuthority(personId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code List<App>} 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listAccessAppForPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority) {

        return y9PositionToResourceService.listAppsByAuthority(positionId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return {@code List<App>} 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listByCustomId(@RequestParam("customId") @NotBlank String customId) {
        return y9AppService.listByCustomId(customId);
    }

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId systemId
     * @return {@code List<App>} 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listBySystemId(@RequestParam("systemId") @NotBlank String systemId) {
        return y9AppService.listBySystemId(systemId);
    }

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName 系统英文名称
     * @return {@code List<App>}
     * @since 9.6.0
     */
    @Override
    public List<App> listBySystemName(@RequestParam("systemName") @NotBlank String systemName) {
        return y9AppService.listBySystemName(systemName);
    }

    /**
     * 注册应用
     *
     * @param systemName 系统名称
     * @param name 应用名称
     * @param url url
     * @param customId customId
     * @param tenantGuid 租户id
     * @return {@code Y9Result<App>}
     * @since 9.6.3
     */
    @Override
    public Y9Result<App> registryApp(@NotBlank String systemName, @NotBlank String name, @NotBlank String url,
        String customId, String tenantGuid) {

        String systemId = y9SystemService.getByName(systemName).getId();

        LOGGER.info("创建应用");
        App app = new App();
        app.setName(name);
        app.setSystemId(systemId);
        app.setUrl(url);
        app.setEnabled(true);
        if (StringUtils.isNotBlank(customId)) {
            app.setCustomId(customId);
        }
        app.setShowNumber(false);
        app.setOpentype(AppOpenTypeEnum.DESKTOP);
        app.setType(AppTypeEnum.BUSINESS_COLLABORATION);
        App saveIsvApp = y9AppService.saveAndRegister4Tenant(app);

        return Y9Result.success(PlatformModelConvertUtil.convert(saveIsvApp, App.class), "注册应用成功！");
    }

    @Override
    public Y9Result<App> registrySystemAndApp(@NotBlank String systemName, @NotBlank String systemCnName,
        String isvGuid, String contextPath, @NotBlank String appName, @NotBlank String url, String customId) {

        System system = new System();
        system.setName(systemName);
        system.setCnName(systemCnName);
        system.setContextPath(contextPath);
        System savedSystem = y9SystemService.saveOrUpdate(system);

        App app = new App();
        app.setName(appName);
        app.setSystemId(savedSystem.getId());
        app.setUrl(url);
        app.setEnabled(true);
        if (StringUtils.isNotBlank(customId)) {
            app.setCustomId(customId);
        }
        app.setShowNumber(false);
        app.setType(AppTypeEnum.BUSINESS_COLLABORATION);
        App savedApp = y9AppService.saveAndRegister4Tenant(app);

        return Y9Result.success(PlatformModelConvertUtil.convert(savedApp, App.class), "创建成功!");
    }

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @return App 应用
     * @since 9.6.0
     */
    @Override
    public App saveIsvApp(@RequestBody App app) {
        return y9AppService.saveOrUpdate(app);
    }

}
