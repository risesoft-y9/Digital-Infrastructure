package net.risesoft.service.setting;

import net.risesoft.service.setting.impl.TenantSetting;

/**
 * 设置 Service
 *
 * @author shidaobang
 * @date 2024/03/27
 */
public interface Y9SettingService {

    /**
     * 根据键获取设置值
     *
     * @param key 设置键
     * @return 设置值
     */
    String get(String key);

    /**
     * 获取租户设置
     *
     * @return {@link TenantSetting}
     */
    TenantSetting getTenantSetting();

    /**
     * 保存租户设置
     *
     * @param tenantSetting 租户设置
     */
    void saveTenantSetting(TenantSetting tenantSetting);
}
