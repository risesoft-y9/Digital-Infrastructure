package net.risesoft.api.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.PlatformModelConvertUtil;
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
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/app", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AppApiImpl implements AppApi {

    private final Y9AppService y9AppService;
    private final Y9SystemService y9SystemService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9TenantAppService y9TenantAppService;

    /**
     * 根据应用id，获取应用信息
     *
     * @param appId 应用id
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是应用对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<App> findById(@RequestParam("appId") @NotBlank String appId) {
        App app = y9AppService.findById(appId).orElse(null);
        return Y9Result.success(app);
    }

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统唯一标识
     * @param customId customId
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是应用对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<App> findBySystemIdAndCustomId(@RequestParam("systemId") @NotBlank String systemId,
        @RequestParam("customId") @NotBlank String customId) {
        App app = y9AppService.findBySystemIdAndCustomId(systemId, customId).orElse(null);
        return Y9Result.success(app);
    }

    /**
     * 根据系统名和自定义标识查找应用
     *
     * @param systemName 系统名
     * @param customId customId
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是应用对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<App> findBySystemNameAndCustomId(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("customId") @NotBlank String customId) {
        App app = y9AppService.findBySystemNameAndCustomId(systemName, customId).orElse(null);
        return Y9Result.success(app);
    }

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<App> findByCustomId(@RequestParam("customId") @NotBlank String customId) {
        return Y9Result.success(y9AppService.findByCustomId(customId).orElse(null));
    }

    /**
     * 根据 systemId ，获取应用列表
     *
     * @param systemId 系统Id
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listBySystemId(@RequestParam("systemId") @NotBlank String systemId) {
        List<App> y9AppList = y9AppService.listBySystemId(systemId);
        return Y9Result.success(y9AppList);
    }

    /**
     * 根据 systemName 获取应用列表
     *
     * @param systemName 系统名称
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listBySystemName(@RequestParam("systemName") @NotBlank String systemName) {
        return Y9Result.success(y9AppService.listBySystemName(systemName));
    }

    /**
     * 注册应用
     *
     * @param systemName 系统名称
     * @param name 应用名称
     * @param url 链接地址
     * @param customId customId
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是注册的应用
     * @since 9.6.3
     */
    @Override
    public Y9Result<App> registerApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("name") @NotBlank String name, @RequestParam("url") @NotBlank String url,
        @RequestParam("customId") String customId) {

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
        App saveIsvApp = y9AppService.saveOrUpdate(app);
        y9AppService.saveAndRegister4Tenant(saveIsvApp);

        return Y9Result.success(PlatformModelConvertUtil.convert(saveIsvApp, App.class), "注册应用成功！");
    }

    /**
     * 注册系统和应用信息
     *
     * @param systemName 系统名称
     * @param systemCnName 系统中文名称
     * @param contextPath 系统上下文
     * @param appName 应用名称
     * @param url 链接地址
     * @param customId customId
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是注册的应用
     * @since 9.6.3
     */
    @Override
    public Y9Result<App> registerSystemAndApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("systemCnName") @NotBlank String systemCnName, @RequestParam("contextPath") String contextPath,
        @RequestParam("appName") @NotBlank String appName, @RequestParam("url") @NotBlank String url,
        @RequestParam("customId") String customId) {

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
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是保存的应用
     * @since 9.6.0
     */
    @Override
    public Y9Result<App> saveIsvApp(@RequestBody App app) {
        return Y9Result.success(y9AppService.saveOrUpdate(app));
    }

}
