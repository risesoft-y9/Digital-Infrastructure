package net.risesoft.service.setting.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.setting.Y9Setting;
import net.risesoft.manager.setting.Y9SettingManager;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.service.setting.PasswordPolicyService;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;

/**
 * 设置 Service 实现类
 *
 * @author shidaobang
 * @date 2024/03/27
 */
@Service
@RequiredArgsConstructor
public class Y9SettingServiceImpl implements Y9SettingService {

    private final Y9SettingManager y9SettingManager;
    private final Environment environment;
    private final Y9PlatformProperties y9PlatformProperties;

    /**
     * 将持久化的字符串设置值转换为字段类型，非数值和布尔类型保留字符串值。
     *
     * @param stringValue 持久化的设置值
     * @param tClass 目标字段类型
     * @return 转换后的字段值
     */
    private Object convert(String stringValue, Class<?> tClass) {
        if (tClass == Integer.class || tClass == int.class) {
            return Integer.valueOf(stringValue);
        } else if (tClass == Long.class || tClass == long.class) {
            return Long.valueOf(stringValue);
        } else if (tClass == Boolean.class || tClass == boolean.class) {
            return Boolean.valueOf(stringValue);
        } else if (tClass == Float.class || tClass == float.class) {
            return Float.valueOf(stringValue);
        } else if (tClass == Double.class || tClass == double.class) {
            return Double.valueOf(stringValue);
        }
        return stringValue;
    }

    @Override
    public String get(String key) {
        Optional<Y9Setting> y9SettingOptional = y9SettingManager.findById(key);
        if (y9SettingOptional.isPresent()) {
            return y9SettingOptional.get().getValue();
        }

        return environment.getProperty(key);
    }

    /**
     * 按“配置前缀 + 字段名”读取租户已保存的设置，未保存或值为 null 的字段保留传入对象的默认值。
     *
     * @param setting 已填充默认值的设置对象
     * @param <T> 设置对象类型
     * @return 由租户已保存值覆盖后的设置对象
     */
    private <T extends AbstractSetting> T fillObjectFiledWithSettingItem(T setting) {
        Field[] declaredFields = setting.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                String value = y9SettingManager.findById(setting.getPrefix() + declaredField.getName())
                    .map(Y9Setting::getValue).orElse(null);
                try {
                    if (value != null) {
                        declaredField.setAccessible(true);
                        declaredField.set(setting, convert(value, declaredField.getType()));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return setting;
    }

    /**
     * 将设置对象的非静态字段逐项保存，配置键由前缀和当前字段名组成。
     *
     * @param setting 待保存的设置对象
     */
    @Transactional
    public void saveObjectFiledAsSettingItem(AbstractSetting setting) {
        Field[] declaredFields = setting.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);

            if (!Modifier.isStatic(declaredField.getModifiers())) {
                Y9Setting y9Setting = new Y9Setting();
                y9Setting.setKey(setting.getPrefix() + declaredField.getName());
                try {
                    Object value = declaredField.get(setting);
                    y9Setting.setValue(value == null ? null : String.valueOf(value));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                this.saveOrUpdate(y9Setting);
            }
        }
    }

    private Y9Setting saveOrUpdate(Y9Setting y9Setting) {
        return y9SettingManager.save(y9Setting);
    }

    @Override
    public TenantSetting getTenantSetting() {
        // 先使用绑定的租户默认配置，再由数据库中已保存的租户设置覆盖。
        return this.fillObjectFiledWithSettingItem(new TenantSetting(y9PlatformProperties.getTenantDefault()));
    }

    @Override
    @Transactional
    public void saveTenantSetting(TenantSetting tenantSetting) {
        PasswordPolicyService.validateSettings(tenantSetting);
        this.saveObjectFiledAsSettingItem(tenantSetting);
    }
}
