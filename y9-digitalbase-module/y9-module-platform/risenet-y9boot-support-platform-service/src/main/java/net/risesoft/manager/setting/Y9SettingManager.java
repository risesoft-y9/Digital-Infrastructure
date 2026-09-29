package net.risesoft.manager.setting;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.setting.Y9Setting;

/**
 * 设置 Manager
 *
 * @author shidaobang
 * @date 2024/03/28
 */
public interface Y9SettingManager {
    /**
     * 获取所有设置
     *
     * @return 设置列表
     */
    List<Y9Setting> findAll();

    /**
     * 根据id获取设置
     *
     * @param id 设置id
     * @return {@code Optional<Y9Setting>}
     */
    Optional<Y9Setting> findById(String id);

    /**
     * 保存设置
     *
     * @param y9Setting 设置信息
     * @return {@link Y9Setting}
     */
    Y9Setting save(Y9Setting y9Setting);
}
