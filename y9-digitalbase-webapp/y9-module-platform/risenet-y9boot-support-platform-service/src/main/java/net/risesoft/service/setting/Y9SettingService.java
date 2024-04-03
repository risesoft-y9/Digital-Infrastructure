package net.risesoft.service.setting;

import java.util.List;

import net.risesoft.entity.Y9Setting;
import net.risesoft.enums.SettingEnum;

/**
 * 设置 Service
 *
 * @author shidaobang
 * @date 2024/03/27
 */
public interface Y9SettingService {
    <T> T get(SettingEnum settingEnum, Class<T> tClass);

    <T> T getObjectFromSetting(Class<T> tClass);

    List<Y9Setting> list();

    void saveObjectFiledAsSetting(Object object);

    void saveOrUpdate(List<Y9Setting> settingList);

    Y9Setting saveOrUpdate(Y9Setting y9Setting);
}
