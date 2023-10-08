package net.risesoft;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.enums.DataSourceTypeEnum;
import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.enums.TenantTypeEnum;
import net.risesoft.enums.Y9RoleTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;
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
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 应用启动监听器 <br/>
 * 执行一系列的数据初始化
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate4Public;
    private final Y9TenantService y9TenantService;
    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9DataSourceService y9DataSourceService;
    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9Properties y9Config;
    private final Y9RoleService y9RoleService;

    private final InitTenantDataService initTenantDataService;

    public OnApplicationReady(@Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        Y9TenantService y9TenantService, Y9SystemService y9SystemService, Y9AppService y9AppService,
        Y9DataSourceService y9DataSourceService, Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        Y9TenantSystemService y9TenantSystemService, Y9Properties y9Config, Y9RoleService y9RoleService,
        InitTenantDataService initTenantDataService) {
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.y9TenantService = y9TenantService;
        this.y9SystemService = y9SystemService;
        this.y9AppService = y9AppService;
        this.y9DataSourceService = y9DataSourceService;
        this.y9TenantDataSourceLookup = y9TenantDataSourceLookup;
        this.y9TenantSystemService = y9TenantSystemService;
        this.y9Config = y9Config;
        this.y9RoleService = y9RoleService;
        this.initTenantDataService = initTenantDataService;
    }

    private void createApp(String appId, String systemId) {
        if (!y9AppService.existsById(appId)) {
            Y9App y9App = new Y9App();
            y9App.setId(appId);
            y9App.setSystemId(systemId);
            y9App.setName("部门管理");
            y9App.setAliasName("部门管理");
            y9App.setEnabled(true);
            y9App.setHidden(false);
            y9App.setUrl(y9Config.getCommon().getOrgBaseUrl());
            y9App.setResourceType(ResourceTypeEnum.APP.getValue());
            y9App.setInherit(false);
            y9App.setChecked(false);
            y9App.setShowNumber(false);
            y9AppService.saveOrUpdate(y9App);
        }
    }

    private void createDataSource(String dataSourceId) {
        Optional<Y9DataSource> y9DataSourceOptional = y9DataSourceService.findById(dataSourceId);
        if (y9DataSourceOptional.isEmpty()) {
            DruidDataSource dataSource = (DruidDataSource)jdbcTemplate4Public.getDataSource();
            Y9DataSource y9DataSource = new Y9DataSource();
            y9DataSource.setId(dataSourceId);
            y9DataSource.setType(DataSourceTypeEnum.DRUID.getValue());
            y9DataSource.setJndiName("y9DefaultDs");
            y9DataSource.setDriver(dataSource.getDriverClassName());
            String url = dataSource.getUrl();
            y9DataSource.setUrl(url.replace("/y9_public?", "/y9_default?"));
            y9DataSource.setUsername(dataSource.getUsername());
            y9DataSource.setPassword(dataSource.getPassword());
            y9DataSource.setInitialSize(dataSource.getInitialSize());
            y9DataSource.setMaxActive(dataSource.getMaxActive());
            y9DataSource.setMinIdle(dataSource.getMinIdle());
            y9DataSourceService.save(y9DataSource);

            try {
                // 增加了租户，创建租户的数据库
                jdbcTemplate4Public.execute("CREATE DATABASE y9_default DEFAULT CHARACTER SET utf8 COLLATE utf8_bin");
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    private void createSystem(String systemId) {
        Optional<Y9System> y9SystemOptional = y9SystemService.findById(systemId);
        if (y9SystemOptional.isEmpty()) {
            Y9System y9System = new Y9System();
            y9System.setId(systemId);
            y9System.setContextPath("platform");
            y9System.setName("riseplatform");
            y9System.setCnName("开源内核");
            y9System.setEnabled(true);
            y9System.setAutoInit(true);
            y9System.setTabIndex(10000);
            y9SystemService.saveOrUpdate(y9System);
        }
    }

    private void createPublicRoleTopNode() {
        Optional<Y9Role> y9RoleOptional = y9RoleService.findById(DefaultIdConsts.TOP_PUBLIC_ROLE_ID);
        if (y9RoleOptional.isEmpty()) {
            Y9Role publicRole = new Y9Role();
            publicRole.setId(DefaultIdConsts.TOP_PUBLIC_ROLE_ID);
            publicRole.setName("公共角色列表");
            publicRole.setAppId(DefaultIdConsts.TOP_PUBLIC_ROLE_ID);
            publicRole.setAppCnName("公共角色");
            publicRole.setSystemCnName("公共角色顶节点");
            publicRole.setSystemName("Y9OrgHierarchyManagement");
            publicRole.setType(Y9RoleTypeEnum.FOLDER.getValue());
            publicRole.setTenantCustom(false);
            publicRole.setDynamic(false);
            publicRole.setTabIndex(0);
            y9RoleService.createRole(publicRole);
        }
    }

    private void createTenant(String tenantId, String dataSourceId) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantService.findById(tenantId);
        if (y9TenantOptional.isEmpty()) {
            Y9Tenant y9Tenant = new Y9Tenant();
            y9Tenant.setId(tenantId);
            y9Tenant.setDefaultDataSourceId(dataSourceId);
            y9Tenant.setShortName("default");
            y9Tenant.setName("default");
            y9Tenant.setEnabled(true);
            y9Tenant.setTenantType(TenantTypeEnum.TENANT.getValue());
            y9Tenant.setTabIndex(10000);
            y9TenantService.save(y9Tenant);
        }
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
            y9TenantSystem.setInitialized(true);
            y9TenantSystemService.save(y9TenantSystem);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("platform ApplicationReady...");

        createSystem(DefaultIdConsts.SYSTEM_ID);
        createApp(DefaultIdConsts.APP_ID, DefaultIdConsts.SYSTEM_ID);
        createDataSource(DefaultIdConsts.DATASOURCE_ID);
        createTenant(DefaultIdConsts.TENANT_ID, DefaultIdConsts.DATASOURCE_ID);
        createTenantSystem(DefaultIdConsts.TENANT_ID, DefaultIdConsts.SYSTEM_ID, DefaultIdConsts.DATASOURCE_ID);
        createPublicRoleTopNode();
        try {
            // 重新加载数据源
            y9TenantDataSourceLookup.loadDataSources();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            // 更新租户数据库里的表结构
            updateTenantSchema();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        initTenantDataService.init(DefaultIdConsts.TENANT_ID);
    }

    private void updateTenantSchema() {
        Map<String, DruidDataSource> map = y9TenantDataSourceLookup.getDataSources();
        Set<String> list = map.keySet();
        for (String tenantId : list) {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
        }
    }
}
