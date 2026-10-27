package net.risesoft.service.setting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        assertEquals(6, setting.getUserPasswordMinLength());
        setting.setUserPasswordMinLength(8);
        setting.setUserPasswordMaxAge(365);
        setting.setUserPasswordRequireDigit(true);
        setting.setUserDefaultPasswordChangeRequired(true);
        service.saveTenantSetting(setting);
        verify(manager).save(argThat(item ->
            "y9.app.platform.tenant-default.userDefaultPasswordChangeRequired".equals(item.getKey())
                && "true".equals(item.getValue())));
        TenantSetting saved = service.getTenantSetting();
        assertEquals(8, saved.getUserPasswordMinLength());
        assertEquals(365, saved.getUserPasswordMaxAge());
        assertTrue(saved.isUserPasswordRequireDigit());
        assertTrue(saved.isUserDefaultPasswordChangeRequired());
        saved.setUserPasswordMinLength(6);
        service.saveTenantSetting(saved);
        assertEquals(6, service.getTenantSetting().getUserPasswordMinLength());
    }

    @Test
    void readsAllDefaultsFromBoundTenantDefaultConfiguration() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("y9.app.platform.tenant-default.user-default-password", "configuredPassword")
            .withProperty("y9.app.platform.tenant-default.position-name-template", "#personNames")
            .withProperty("y9.app.platform.tenant-default.user-default-password-change-required", "true")
            .withProperty("y9.app.platform.tenant-default.user-password-max-age", "90")
            .withProperty("y9.app.platform.tenant-default.user-password-min-length", "12")
            .withProperty("y9.app.platform.tenant-default.user-password-require-uppercase", "true")
            .withProperty("y9.app.platform.tenant-default.user-password-require-lowercase", "true")
            .withProperty("y9.app.platform.tenant-default.user-password-require-digit", "true")
            .withProperty("y9.app.platform.tenant-default.user-password-require-symbol", "true");
        Y9PlatformProperties bound = Binder.get(environment).bind("y9.app.platform", Y9PlatformProperties.class).get();
        service = new Y9SettingServiceImpl(manager, environment, bound);

        TenantSetting setting = service.getTenantSetting();
        assertEquals("configuredPassword", setting.getUserDefaultPassword());
        assertEquals("#personNames", setting.getPositionNameTemplate());
        assertTrue(setting.isUserDefaultPasswordChangeRequired());
        assertEquals(90, setting.getUserPasswordMaxAge());
        assertEquals(12, setting.getUserPasswordMinLength());
        assertTrue(setting.isUserPasswordRequireUppercase());
        assertTrue(setting.isUserPasswordRequireLowercase());
        assertTrue(setting.isUserPasswordRequireDigit());
        assertTrue(setting.isUserPasswordRequireSymbol());
    }

    @Test
    void persistedFalseAndZeroOverrideDefaultsWhileMissingFieldsUseDefaults() {
        properties.getTenantDefault().setUserDefaultPasswordChangeRequired(true);
        properties.getTenantDefault().setUserPasswordMaxAge(90);
        properties.getTenantDefault().setUserPasswordMinLength(12);
        properties.getTenantDefault().setUserPasswordRequireDigit(true);
        TenantSetting setting = service.getTenantSetting();
        setting.setUserDefaultPasswordChangeRequired(false);
        setting.setUserPasswordMaxAge(0);
        setting.setUserPasswordRequireDigit(false);
        service.saveTenantSetting(setting);

        // 后续调整默认值，已保存的租户设置仍优先。
        properties.getTenantDefault().setUserPasswordMinLength(16);
        TenantSetting saved = service.getTenantSetting();
        assertFalse(saved.isUserDefaultPasswordChangeRequired());
        assertEquals(0, saved.getUserPasswordMaxAge());
        assertFalse(saved.isUserPasswordRequireDigit());
        assertEquals(12, saved.getUserPasswordMinLength());

        when(manager.findById("y9.app.platform.tenant-default.userPasswordMinLength"))
            .thenReturn(Optional.empty());
        assertEquals(16, service.getTenantSetting().getUserPasswordMinLength());
    }

    @Test
    void rejectsInvalidPolicyBeforeWritingAnySetting() {
        TenantSetting setting = new TenantSetting();
        setting.setUserPasswordMaxAge(366);
        assertThrows(Y9BusinessException.class, () -> service.saveTenantSetting(setting));
        verify(manager, never()).save(any());
    }
}
