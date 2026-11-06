package net.risesoft.service.setting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import net.risesoft.entity.setting.Y9Setting;
import net.risesoft.manager.setting.Y9SettingManager;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.service.setting.impl.Y9SettingServiceImpl;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.exception.Y9BusinessException;

class Y9SettingPasswordPolicyTest {

    private Y9SettingManager manager;
    private Y9SettingServiceImpl service;
    private Y9PlatformProperties properties;

    @BeforeEach
    void setUp() {
        Map<String, Y9Setting> settings = new HashMap<>();
        manager = mock(Y9SettingManager.class);
        when(manager.findById(anyString())).thenAnswer(call -> Optional.ofNullable(settings.get(call.getArgument(0))));
        when(manager.save(any())).thenAnswer(call -> {
            Y9Setting setting = call.getArgument(0);
            settings.put(setting.getKey(), setting);
            return setting;
        });
        properties = new Y9PlatformProperties();
        service = new Y9SettingServiceImpl(manager, mock(Environment.class), properties);
    }

    @Test
    void canSaveReadAndUpdatePasswordPolicy() {
        TenantSetting setting = service.getTenantSetting();
        assertEquals(6, setting.getPasswordMinLength());
        setting.setPasswordMinLength(8);
        setting.setPasswordMaxAge(365);
        setting.setPasswordRequireDigit(true);
        setting.setDefaultPasswordChangeRequired(true);
        service.saveTenantSetting(setting);
        verify(manager)
            .save(argThat(item -> "y9.app.platform.tenant-default.defaultPasswordChangeRequired".equals(item.getKey())
                && "true".equals(item.getValue())));
        TenantSetting saved = service.getTenantSetting();
        assertEquals(8, saved.getPasswordMinLength());
        assertEquals(365, saved.getPasswordMaxAge());
        assertTrue(saved.isPasswordRequireDigit());
        assertTrue(saved.isDefaultPasswordChangeRequired());
        saved.setPasswordMinLength(6);
        service.saveTenantSetting(saved);
        assertEquals(6, service.getTenantSetting().getPasswordMinLength());
    }

    @Test
    void readsAllDefaultsFromBoundTenantDefaultConfiguration() {
        MockEnvironment environment =
            new MockEnvironment().withProperty("y9.app.platform.tenant-default.default-password", "configuredPassword")
                .withProperty("y9.app.platform.tenant-default.position-name-template", "#personNames")
                .withProperty("y9.app.platform.tenant-default.default-password-change-required", "true")
                .withProperty("y9.app.platform.tenant-default.password-max-age", "90")
                .withProperty("y9.app.platform.tenant-default.password-min-length", "12")
                .withProperty("y9.app.platform.tenant-default.password-require-uppercase", "true")
                .withProperty("y9.app.platform.tenant-default.password-require-lowercase", "true")
                .withProperty("y9.app.platform.tenant-default.password-require-digit", "true")
                .withProperty("y9.app.platform.tenant-default.password-require-symbol", "true");
        Y9PlatformProperties bound = Binder.get(environment).bind("y9.app.platform", Y9PlatformProperties.class).get();
        service = new Y9SettingServiceImpl(manager, environment, bound);

        TenantSetting setting = service.getTenantSetting();
        assertEquals("configuredPassword", setting.getDefaultPassword());
        assertEquals("#personNames", setting.getPositionNameTemplate());
        assertTrue(setting.isDefaultPasswordChangeRequired());
        assertEquals(90, setting.getPasswordMaxAge());
        assertEquals(12, setting.getPasswordMinLength());
        assertTrue(setting.isPasswordRequireUppercase());
        assertTrue(setting.isPasswordRequireLowercase());
        assertTrue(setting.isPasswordRequireDigit());
        assertTrue(setting.isPasswordRequireSymbol());
    }

    @Test
    void persistedFalseAndZeroOverrideDefaultsWhileMissingFieldsUseDefaults() {
        properties.getTenantDefault().setDefaultPasswordChangeRequired(true);
        properties.getTenantDefault().setPasswordMaxAge(90);
        properties.getTenantDefault().setPasswordMinLength(12);
        properties.getTenantDefault().setPasswordRequireDigit(true);
        TenantSetting setting = service.getTenantSetting();
        setting.setDefaultPasswordChangeRequired(false);
        setting.setPasswordMaxAge(0);
        setting.setPasswordRequireDigit(false);
        service.saveTenantSetting(setting);

        // 后续调整默认值，已保存的租户设置仍优先。
        properties.getTenantDefault().setPasswordMinLength(16);
        TenantSetting saved = service.getTenantSetting();
        assertFalse(saved.isDefaultPasswordChangeRequired());
        assertEquals(0, saved.getPasswordMaxAge());
        assertFalse(saved.isPasswordRequireDigit());
        assertEquals(12, saved.getPasswordMinLength());

        when(manager.findById("y9.app.platform.tenant-default.passwordMinLength")).thenReturn(Optional.empty());
        assertEquals(16, service.getTenantSetting().getPasswordMinLength());
    }

    @Test
    void rejectsInvalidPolicyBeforeWritingAnySetting() {
        TenantSetting setting = new TenantSetting();
        setting.setPasswordMaxAge(366);
        assertThrows(Y9BusinessException.class, () -> service.saveTenantSetting(setting));
        verify(manager, never()).save(any());
    }
}
