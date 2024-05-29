package net.risesoft.service.setting.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Setting;
import net.risesoft.enums.SettingEnum;
import net.risesoft.manager.setting.Y9SettingManager;
import net.risesoft.service.setting.Y9SettingService;

/**
 * 设置 Service 实现类
 *
 * @author shidaobang
 * @date 2024/03/27
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9SettingServiceImpl implements Y9SettingService {

    private final String PLATFORM_KEY_PREFIX = "y9.app.platform.";

    private final Y9SettingManager y9SettingManager;
    private final Environment environment;

    private Object convert(String stringValue, Class<?> tClass) {
        if (tClass == Integer.class || tClass == int.class) {
            return Integer.valueOf(stringValue);
        } else if (tClass == Long.class || tClass == long.class) {
            return Long.valueOf(stringValue);
        } else if (tClass == Boolean.class || tClass == boolean.class) {
            return Boolean.valueOf(stringValue);
        }
        return stringValue;
    }

    @Override
    public <T> T get(SettingEnum settingEnum, Class<T> tClass) {
        Optional<Y9Setting> y9SettingOptional = y9SettingManager.findById(settingEnum.getKey());
        if (y9SettingOptional.isPresent()) {
            return (T)this.convert(y9SettingOptional.get().getValue(), tClass);
        }

        String key = PLATFORM_KEY_PREFIX + settingEnum.getKey();
        return environment.getProperty(key, tClass, (T)settingEnum.getDefaultValue());
    }

    @Override
    public <T> T getObjectFromSetting(Class<T> tClass) {
        T t;
        try {
            t = tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                SettingEnum settingEnum = SettingEnum.getByKey(declaredField.getName());
                if (settingEnum != null) {
                    Object value = this.get(settingEnum, declaredField.getType());
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(t, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return t;
    }

    @Override
    public List<Y9Setting> list() {
        return y9SettingManager.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveObjectFiledAsSetting(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);

            if (!Modifier.isStatic(declaredField.getModifiers())) {
                Y9Setting y9Setting = new Y9Setting();
                y9Setting.setKey(declaredField.getName());
                try {
                    y9Setting.setValue(String.valueOf(declaredField.get(object)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                this.saveOrUpdate(y9Setting);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(List<Y9Setting> settingList) {
        for (Y9Setting y9Setting : settingList) {
            saveOrUpdate(y9Setting);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Setting saveOrUpdate(Y9Setting y9Setting) {
        return y9SettingManager.save(y9Setting);
    }
}
