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
import net.risesoft.service.setting.UserPasswordPolicyService;
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
        return this.fillObjectFiledWithSettingItem(new TenantSetting(y9PlatformProperties.getTenantDefault()));
    }

    @Override
    @Transactional
    public void saveTenantSetting(TenantSetting tenantSetting) {
        UserPasswordPolicyService.validateSettings(tenantSetting);
        this.saveObjectFiledAsSettingItem(tenantSetting);
    }
}
