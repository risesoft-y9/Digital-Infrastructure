package net.risesoft.initializer;

import java.util.Optional;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;
import net.risesoft.y9public.service.tenant.Y9DataSourceService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 应用启动监听器 <br>
 * 执行一系列的数据初始化
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PlatformApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private final Y9TenantService y9TenantService;
    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9DataSourceService y9DataSourceService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9Properties y9Properties;
    private final Y9PlatformProperties y9PlatformProperties;
    private final Y9RoleService y9RoleService;
    private final Y9TenantAppService y9TenantAppService;

    private void createApp(String appId, String systemId) {
        if (!y9AppService.existsById(appId)) {
            Y9App y9App = new Y9App();
            y9App.setId(appId);
            y9App.setSystemId(systemId);
            y9App.setName("数据目录");
            y9App.setAliasName("数据目录");
            y9App.setEnabled(true);
            y9App.setHidden(false);
            y9App.setUrl(y9Properties.getCommon().getOrgBaseUrl());
            y9App.setInherit(false);
            y9App.setChecked(false);
            y9App.setShowNumber(false);
            y9AppService.saveOrUpdate(y9App);
        }
    }

    private Y9DataSource createDataSource(String datasourceId, String dbName) {
        return y9DataSourceService.createTenantDefaultDataSource(dbName, datasourceId);
    }

    private void createPublicRoleTopNode() {
        Optional<Y9Role> y9RoleOptional = y9RoleService.findById(InitDataConsts.TOP_PUBLIC_ROLE_ID);
        if (y9RoleOptional.isEmpty()) {
            Y9Role publicRole = new Y9Role();
            publicRole.setId(InitDataConsts.TOP_PUBLIC_ROLE_ID);
            publicRole.setName("公共角色列表");
            publicRole.setType(RoleTypeEnum.FOLDER);
            publicRole.setDynamic(false);
            publicRole.setTabIndex(0);
            y9RoleService.createRole(publicRole);
        }
    }

    private void createSystem(String systemId) {
        Optional<Y9System> y9SystemOptional = y9SystemService.findById(systemId);
        if (y9SystemOptional.isEmpty()) {
            Y9System y9System = new Y9System();
            y9System.setId(systemId);
            y9System.setContextPath(y9Properties.getContextPath());
            y9System.setName(y9Properties.getSystemName());
            y9System.setCnName(y9Properties.getSystemCnName());
            y9System.setEnabled(true);
            y9System.setAutoInit(true);
            y9System.setTabIndex(10000);
            y9SystemService.saveOrUpdate(y9System);
        }
    }

    private void createTenant(String tenantId, String dataSourceId) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantService.findById(tenantId);
        if (y9TenantOptional.isEmpty()) {
            Y9Tenant y9Tenant = new Y9Tenant();
            y9Tenant.setId(tenantId);
            y9Tenant.setDefaultDataSourceId(dataSourceId);
            y9Tenant.setShortName(y9PlatformProperties.getInitTenantName());
            y9Tenant.setName(y9PlatformProperties.getInitTenantName());
            y9Tenant.setEnabled(true);
            y9Tenant.setTenantType(TenantTypeEnum.TENANT);
            y9Tenant.setTabIndex(10000);
            y9TenantService.save(y9Tenant);
        }
    }

    private void createTenantApp(String appId, String tenantId) {
        y9TenantAppService.save(appId, tenantId, "系统默认租用");
    }

    private void createTenantSystem(String tenantId, String systemId, String dataSourceId) {
        Optional<Y9TenantSystem> y9TenantSystemOptional =
            y9TenantSystemService.getByTenantIdAndSystemId(tenantId, systemId);
        if (y9TenantSystemOptional.isEmpty()) {
            Y9TenantSystem y9TenantSystem = new Y9TenantSystem();
            y9TenantSystem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9TenantSystem.setTenantId(tenantId);
            y9TenantSystem.setTenantDataSource(dataSourceId);
            y9TenantSystem.setSystemId(systemId);
            y9TenantSystem.setInitialized(false);
            y9TenantSystemService.save(y9TenantSystem);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("platform ApplicationReady...");

        createDataSource(InitDataConsts.DATASOURCE_ID, y9PlatformProperties.getInitTenantSchema());
        createTenant(InitDataConsts.TENANT_ID, InitDataConsts.DATASOURCE_ID);
        createSystem(InitDataConsts.SYSTEM_ID);
        // 租用系统会发送 租户租用系统事件 系统做监听做数据初始化
        createTenantSystem(InitDataConsts.TENANT_ID, InitDataConsts.SYSTEM_ID, InitDataConsts.DATASOURCE_ID);
        createApp(InitDataConsts.APP_ID, InitDataConsts.SYSTEM_ID);
        createTenantApp(InitDataConsts.APP_ID, InitDataConsts.TENANT_ID);
        createPublicRoleTopNode();
    }

}
