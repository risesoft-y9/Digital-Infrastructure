package net.risesoft.manager.setting;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Setting;

/**
 * 设置 Manager
 *
 * @author shidaobang
 * @date 2024/03/28
 */
public interface Y9SettingManager {
    List<Y9Setting> findAll();

    Optional<Y9Setting> findById(String id);

    Y9Setting save(Y9Setting y9Setting);
}
