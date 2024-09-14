package net.risesoft.api.v0.resource;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

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
import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.AppOpenTypeEnum;
import net.risesoft.enums.platform.AppTypeEnum;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.model.platform.App;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
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
    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;
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
        Y9App y9App = y9AppService.findById(appId).orElse(null);
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
     * @return {@code List<App>} 应用列表
     * @since 9.6.0
     */
    @Override
    public List<App> listAccessAppForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9App> appList = y9PersonToResourceAndAuthorityService.listAppsByAuthority(personId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
        return Y9ModelConvertUtil.convert(appList, App.class);
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
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9App> appList = y9PositionToResourceAndAuthorityService.listAppsByAuthority(positionId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
        return Y9ModelConvertUtil.convert(appList, App.class);
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
        List<Y9App> y9AppList = y9AppService.listByCustomId(customId);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
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
        List<Y9App> y9AppList = y9AppService.listBySystemId(systemId);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
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
        List<Y9App> y9AppList = y9AppService.listBySystemName(systemName);
        return Y9ModelConvertUtil.convert(y9AppList, App.class);
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
        Optional<Y9System> y9SystemOptional = y9SystemService.findByName(systemName);
        if (y9SystemOptional.isEmpty()) {
            return Y9Result.failure("该系统不存在，请重新输入！");
        }
        if (StringUtils.isBlank(tenantGuid)) {
            tenantGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(tenantGuid);
        String systemId = y9SystemOptional.get().getId();
        Y9App saveIsvApp = null;
        String appId = null;
        String msg = "注册应用成功！";
        try {
            LOGGER.info("创建应用");
            Y9App y9App = new Y9App();
            y9App.setName(name);
            y9App.setSystemId(systemId);
            y9App.setUrl(url);
            y9App.setEnabled(true);
            if (StringUtils.isNotBlank(customId)) {
                y9App.setCustomId(customId);
            }
            y9App.setShowNumber(false);
            y9App.setOpentype(AppOpenTypeEnum.DESKTOP);
            y9App.setType(AppTypeEnum.BUSINESS_COLLABORATION);
            saveIsvApp = y9AppService.saveIsvApp(y9App);
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
        return Y9Result.success(Y9ModelConvertUtil.convert(saveIsvApp, App.class), msg);
    }

    @Override
    public Y9Result<App> registrySystemAndApp(@NotBlank String systemName, @NotBlank String systemCnName,
        String isvGuid, String contextPath, @NotBlank String appName, @NotBlank String url, String customId) {

        List<Y9System> y9Systems = y9SystemService.listByContextPath(contextPath);
        if (!y9Systems.isEmpty()) {
            return Y9Result.failure("该系统上下文已存在，请重新输入！");
        }
        Optional<Y9System> y9SystemOptional = y9SystemService.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            return Y9Result.failure("该系统名称已存在，请重新输入！");
        }
        if (StringUtils.isBlank(isvGuid)) {
            isvGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(isvGuid);
        String systemId = null;
        String msg = "创建成功!";
        try {
            Y9System y9System = new Y9System();
            y9System.setIsvGuid(isvGuid);
            y9System.setName(systemName);
            y9System.setCnName(systemCnName);
            y9System.setContextPath(contextPath);
            Y9System system = y9SystemService.saveOrUpdate(y9System);
            systemId = system.getId();
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
        Y9App saveIsvApp = null;
        try {
            Y9App y9App = new Y9App();
            y9App.setName(appName);
            y9App.setSystemId(systemId);
            y9App.setUrl(url);
            y9App.setEnabled(true);
            if (StringUtils.isNotBlank(customId)) {
                y9App.setCustomId(customId);
            }
            y9App.setShowNumber(false);
            y9App.setOpentype(AppOpenTypeEnum.DESKTOP);
            y9App.setType(AppTypeEnum.BUSINESS_COLLABORATION);

            saveIsvApp = y9AppService.saveIsvApp(y9App);
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
        return Y9Result.success(Y9ModelConvertUtil.convert(saveIsvApp, App.class), msg);
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
        Y9App y9App = new Y9App();
        Y9BeanUtil.copyProperties(app, y9App);
        y9App = y9AppService.saveIsvApp(y9App);
        return Y9ModelConvertUtil.convert(y9App, App.class);
    }

}
