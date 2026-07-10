package net.risesoft.initializer;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.Optional;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.tenant.DataSourceInfo;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;
import net.risesoft.y9public.service.tenant.Y9DataSourceService;
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
    private final Y9RoleService y9RoleService;
    private final Y9DataSourceService y9DataSourceService;
    private final Y9TenantSystemService y9TenantSystemService;

    private final Y9Properties y9Properties;
    private final Y9PlatformProperties y9PlatformProperties;

    private DataSourceInfo createOrFindDataSource(String dbName) {
        return y9DataSourceService.createTenantDefaultDataSource(dbName);
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

    private System createOrFindSystem(String name, String cnName, String contextPath) {
        Optional<System> systemOptional = y9SystemService.findByName(y9Properties.getSystemName());
        if (systemOptional.isEmpty()) {
            System system = new System();
            system.setName(name);
            system.setCnName(cnName);
            system.setContextPath(contextPath);
            system.setAutoInit(true);
            // 租用系统会发送 租户租用系统事件 系统做监听做数据初始化
            System savedSystem = y9SystemService.saveAndRegister4Tenant(system);

            return savedSystem;
        }
        return systemOptional.get();
    }

    private Tenant createOrFindTenant(String dataSourceId, String tenantName) {
        Optional<Tenant> tenantOptional = y9TenantService.findByShortName(tenantName);
        if (tenantOptional.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setDefaultDataSourceId(dataSourceId);
            tenant.setShortName(tenantName);
            tenant.setName(tenantName);
            tenant.setEnabled(true);
            tenant.setTabIndex(10000);
            return y9TenantService.saveOrUpdate(tenant);
        }
        return tenantOptional.get();
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("platform ApplicationReady...");

        DataSourceInfo defaultDataSource = createOrFindDataSource(y9PlatformProperties.getInitTenantSchema());
        createOrFindTenant(defaultDataSource.getId(), y9PlatformProperties.getInitTenantName());
        createOrFindSystem(y9Properties.getSystemName(), y9Properties.getSystemCnName(), y9Properties.getContextPath());
        createPublicRoleTopNode();
    }

}
