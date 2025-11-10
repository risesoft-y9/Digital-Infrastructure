package net.risesoft.api.resource;

import java.util.List;
import java.util.Optional;

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
import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
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

    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9PositionToResourceService y9PositionToResourceService;

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
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 操作类型(如：BROWSE、ADMIN)
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是有权限的应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listAccessAppForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToResourceService.listAppsByAuthority(personId, authority));
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listAccessAppForPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionToResourceService.listAppsByAuthority(positionId, authority));
    }

    /**
     * 根据 customId ，获取应用列表
     *
     * @param customId customId
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listByCustomId(@RequestParam("customId") @NotBlank String customId) {
        return Y9Result.success(y9AppService.listByCustomId(customId));
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
     * @param tenantGuid 租户id
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是注册的应用
     * @since 9.6.3
     */
    @Override
    public Y9Result<App> registerApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("name") @NotBlank String name, @RequestParam("url") @NotBlank String url,
        @RequestParam("customId") String customId, @RequestParam("tenantGuid") String tenantGuid) {
        Optional<System> systemOptional = y9SystemService.findByName(systemName);
        if (systemOptional.isEmpty()) {
            return Y9Result.failure("该系统不存在，请重新输入！");
        }
        if (StringUtils.isBlank(tenantGuid)) {
            tenantGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(tenantGuid);
        String systemId = systemOptional.get().getId();
        App saveIsvApp = null;
        String appId = null;
        String msg = "注册应用成功！";
        try {
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
            saveIsvApp = y9AppService.saveIsvApp(app);
            appId = saveIsvApp.getId();
            y9AppService.verifyApp(appId, true, ManagerLevelEnum.SYSTEM_MANAGER.getName());
        } catch (Exception e) {
            e.printStackTrace();
            if (appId != null) {
                y9AppService.delete(appId);
            }
            return Y9Result.failure("创建失败！原因为：" + e.getMessage());
        }

        try {
            LOGGER.info("租用系统");
            y9TenantSystemService.saveTenantSystem(systemId, tenantGuid);
            y9TenantAppService.save(appId, tenantGuid, "系统默认租用");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("系统、应用已创建成功！但自动应用租用失败，请收到进行租用！");
            msg = "系统、应用已创建成功！但自动应用租用失败，请收到进行租用！";
        }
        return Y9Result.success(PlatformModelConvertUtil.convert(saveIsvApp, App.class), msg);
    }

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
     * @return {@code Y9Result<App>} 通用请求返回对象 - data 是注册的应用
     * @since 9.6.3
     */
    @Override
    public Y9Result<App> registerSystemAndApp(@RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("systemCnName") @NotBlank String systemCnName, @RequestParam("isvGuid") String isvGuid,
        @RequestParam("contextPath") String contextPath, @RequestParam("appName") @NotBlank String appName,
        @RequestParam("url") @NotBlank String url, @RequestParam("customId") String customId) {

        List<System> systemList = y9SystemService.listByContextPath(contextPath);
        if (!systemList.isEmpty()) {
            return Y9Result.failure("该系统上下文已存在，请重新输入！");
        }
        Optional<System> systemOptional = y9SystemService.findByName(systemName);
        if (systemOptional.isPresent()) {
            return Y9Result.failure("该系统名称已存在，请重新输入！");
        }
        if (StringUtils.isBlank(isvGuid)) {
            isvGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(isvGuid);
        String systemId = null;
        String msg = "创建成功!";
        try {
            System system = new System();
            system.setTenantId(isvGuid);
            system.setName(systemName);
            system.setCnName(systemCnName);
            system.setContextPath(contextPath);
            System savedSystem = y9SystemService.saveOrUpdate(system);
            systemId = savedSystem.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("创建系统失败！");
        }
        try {
            y9TenantSystemService.saveTenantSystem(systemId, isvGuid);
        } catch (Exception e) {
            y9SystemService.delete(systemId);
            e.printStackTrace();
            return Y9Result.failure("租用系统失败！");
        }
        String appId = null;
        App saveIsvApp;
        try {
            App app = new App();
            app.setName(appName);
            app.setSystemId(systemId);
            app.setUrl(url);
            app.setEnabled(true);
            if (StringUtils.isNotBlank(customId)) {
                app.setCustomId(customId);
            }
            app.setShowNumber(false);
            app.setType(AppTypeEnum.BUSINESS_COLLABORATION);

            saveIsvApp = y9AppService.saveIsvApp(app);
            appId = saveIsvApp.getId();
            y9AppService.verifyApp(appId, true, ManagerLevelEnum.SYSTEM_MANAGER.getName());
        } catch (Exception e) {
            e.printStackTrace();
            y9TenantSystemService.deleteByTenantIdAndSystemId(isvGuid, systemId);
            y9SystemService.delete(systemId);
            if (appId != null) {
                y9AppService.delete(appId);
            }
            return Y9Result.failure("系统创建成功，但应用添加失败！");
        }
        try {
            y9TenantAppService.save(appId, isvGuid, "开发使用");
        } catch (Exception e) {
            e.printStackTrace();
            msg = "系统、应用已创建成功！但自动应用租用失败，请收到进行租用";
        }
        return Y9Result.success(PlatformModelConvertUtil.convert(saveIsvApp, App.class), msg);
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
        return Y9Result.success(y9AppService.saveIsvApp(app));
    }

}
