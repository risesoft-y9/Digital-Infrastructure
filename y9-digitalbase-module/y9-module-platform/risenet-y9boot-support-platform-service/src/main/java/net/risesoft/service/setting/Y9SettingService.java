package net.risesoft.service.setting;

import net.risesoft.service.setting.impl.TenantSetting;

/**
 * 设置 Service
 *
 * @author shidaobang
 * @date 2024/03/27
 */
public interface Y9SettingService {

    String get(String key);

    TenantSetting getTenantSetting();

    void saveTenantSetting(TenantSetting tenantSetting);
}
