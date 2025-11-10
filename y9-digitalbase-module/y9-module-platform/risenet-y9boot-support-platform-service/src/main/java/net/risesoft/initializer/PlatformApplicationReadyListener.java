package net.risesoft.initializer;

import java.util.Optional;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.tenant.DataSourceInfo;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.model.platform.tenant.TenantSystem;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
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
            App app = new App();
            app.setId(appId);
            app.setSystemId(systemId);
            app.setName("数据目录");
            app.setAliasName("数据目录");
            app.setEnabled(true);
            app.setHidden(false);
            app.setUrl(y9Properties.getCommon().getOrgBaseUrl());
            app.setInherit(false);
            app.setChecked(false);
            app.setShowNumber(false);
            y9AppService.saveOrUpdate(app);
        }
    }

    private DataSourceInfo createDataSource(String dbName, String datasourceId) {
        return y9DataSourceService.createTenantDefaultDataSource(dbName, datasourceId);
    }

    private void createPublicRoleTopNode() {
        Optional<Role> roleOptional = y9RoleService.findById(InitDataConsts.TOP_PUBLIC_ROLE_ID);
        if (roleOptional.isEmpty()) {
            Role publicRole = new Role();
            publicRole.setId(InitDataConsts.TOP_PUBLIC_ROLE_ID);
            publicRole.setName("公共角色列表");
            publicRole.setType(RoleTypeEnum.FOLDER);
            publicRole.setDynamic(false);
            publicRole.setTabIndex(0);
            y9RoleService.saveOrUpdate(publicRole);
        }
    }

    private void createSystem(String systemId) {
        Optional<System> systemOptional = y9SystemService.findById(systemId);
        if (systemOptional.isEmpty()) {
            System system = new System();
            system.setId(systemId);
            system.setContextPath(y9Properties.getContextPath());
            system.setName(y9Properties.getSystemName());
            system.setCnName(y9Properties.getSystemCnName());
            system.setEnabled(true);
            system.setAutoInit(true);
            system.setTabIndex(10000);
            y9SystemService.saveOrUpdate(system);
        }
    }

    private void createTenant(String tenantId, String dataSourceId) {
        Optional<Tenant> tenantOptional = y9TenantService.findById(tenantId);
        if (tenantOptional.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setId(tenantId);
            tenant.setDefaultDataSourceId(dataSourceId);
            tenant.setShortName(y9PlatformProperties.getInitTenantName());
            tenant.setName(y9PlatformProperties.getInitTenantName());
            tenant.setEnabled(true);
            tenant.setTabIndex(10000);
            y9TenantService.saveOrUpdate(tenant);
        }
    }

    private void createTenantApp(String appId, String tenantId) {
        y9TenantAppService.save(appId, tenantId, "系统默认租用");
    }

    private void createTenantSystem(String tenantId, String systemId, String dataSourceId) {
        Optional<TenantSystem> tenantSystemOptional =
            y9TenantSystemService.getByTenantIdAndSystemId(tenantId, systemId);
        if (tenantSystemOptional.isEmpty()) {
            TenantSystem tenantSystem = new TenantSystem();
            tenantSystem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            tenantSystem.setTenantId(tenantId);
            tenantSystem.setTenantDataSource(dataSourceId);
            tenantSystem.setSystemId(systemId);
            tenantSystem.setInitialized(false);
            y9TenantSystemService.save(tenantSystem);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("platform ApplicationReady...");

        createDataSource(y9PlatformProperties.getInitTenantSchema(), InitDataConsts.DATASOURCE_ID);
        createTenant(InitDataConsts.TENANT_ID, InitDataConsts.DATASOURCE_ID);
        createSystem(InitDataConsts.SYSTEM_ID);
        // 租用系统会发送 租户租用系统事件 系统做监听做数据初始化
        createTenantSystem(InitDataConsts.TENANT_ID, InitDataConsts.SYSTEM_ID, InitDataConsts.DATASOURCE_ID);
        createApp(InitDataConsts.APP_ID, InitDataConsts.SYSTEM_ID);
        createTenantApp(InitDataConsts.APP_ID, InitDataConsts.TENANT_ID);
        createPublicRoleTopNode();
    }

}
