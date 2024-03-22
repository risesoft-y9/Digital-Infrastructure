package net.risesoft.init;

import net.risesoft.model.platform.TenantApp;

/**
 * 租户应用初始化器
 * 
 * @see net.risesoft.listener.TenantAppEventListener
 *
 * @author shidaobang
 * @date 2024/03/22
 */
public interface TenantAppInitializer {

    void init(TenantApp tenantApp);

}
