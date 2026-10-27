package net.risesoft.y9.configuration.app.y9platform;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9platform.datacatalog.DataCatalogProperties;
import net.risesoft.y9.configuration.app.y9platform.init.InitProperties;
import net.risesoft.y9.configuration.app.y9platform.manager.ManagerProperties;
import net.risesoft.y9.configuration.app.y9platform.schedule.ScheduleProperties;
import net.risesoft.y9.configuration.app.y9platform.tenantdefault.TenantDefaultProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.platform", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9PlatformProperties {

    /**
     * 系统名称
     */
    private String systemName = "riseplatform";

    /**
     * 三员配置
     */
    @NestedConfigurationProperty
    private ManagerProperties manager = new ManagerProperties();

    /**
     * 租户默认配置
     */
    @NestedConfigurationProperty
    private TenantDefaultProperties tenantDefault = new TenantDefaultProperties();

    /**
     * 数据目录配置
     */
    @NestedConfigurationProperty
    private DataCatalogProperties dataCatalog = new DataCatalogProperties();

    /**
     * 定时任务配置
     */
    @NestedConfigurationProperty
    private ScheduleProperties schedule = new ScheduleProperties();

    /**
     * 初始化配置
     */
    @NestedConfigurationProperty
    private InitProperties init = new InitProperties();
}
