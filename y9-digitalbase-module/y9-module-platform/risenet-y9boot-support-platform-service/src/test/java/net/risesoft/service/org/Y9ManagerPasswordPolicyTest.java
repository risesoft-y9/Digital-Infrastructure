package net.risesoft.service.org;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.delegatesTo;

import java.util.Optional;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import net.risesoft.consts.PasswordHashAlgorithmConsts;
import net.risesoft.entity.org.Y9Manager;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.repository.org.Y9ManagerRepository;
import net.risesoft.service.org.impl.Y9ManagerServiceImpl;
import net.risesoft.service.setting.PasswordPolicyService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.configuration.app.y9platform.manager.ManagerProperties;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/** 密码修改、重置和独立周期检查的集成测试。 */
class Y9ManagerPasswordPolicyTest {

    @Mock
    private Y9ManagerRepository repository;
    @Mock
    private Y9SettingService settings;
    @Mock
    private PasswordPolicyService policy;
    @Mock
    private Y9PlatformProperties properties;
    @InjectMocks
    private Y9ManagerServiceImpl service;

    private AutoCloseable mocks;
    private ApplicationContext previousContext;
    private Y9Manager manager;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(settings.getTenantSetting()).thenReturn(new TenantSetting());
        doAnswer(delegatesTo(new PasswordPolicyService(settings)))
            .when(policy).validatePassword(any(), any(), any(), any());
        when(properties.getManager()).thenReturn(new ManagerProperties());
        previousContext = Y9Context.getAc();
        new Y9Context().setApplicationContext(mock(ApplicationContext.class));
        manager = new Y9Manager();
        manager.setId("manager1");
        manager.setName("测试管理员");
        manager.setManagerLevel(ManagerLevelEnum.TENANT_SYSTEM_MANAGER);
        manager.setPassword(Y9MessageDigestUtil.bcrypt("currentPassword"));
        manager.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        when(repository.findById("manager1")).thenReturn(Optional.of(manager));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        new Y9Context().setApplicationContext(previousContext);
        mocks.close();
    }

    @Test
    void changeValidatesTenantPolicyAndRecordsTime() {
        service.changePassword("manager1", "currentPassword", "Chosen1!");
        verify(policy).validatePassword(eq("currentPassword"), eq("Chosen1!"), anyString(), eq(PasswordHashAlgorithmConsts.BCRYPT));
        verify(repository).save(manager);
        assertTrue(manager.isPasswordCorrect("Chosen1!"));
        assertEquals(PasswordHashAlgorithmConsts.BCRYPT, manager.getPasswordHashAlgorithm());
        assertNotNull(manager.getLastModifyPasswordTime());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void changeRejectsMissingOrUnsupportedStoredAlgorithm(String algorithm) {
        manager.setPasswordHashAlgorithm(algorithm);
        String hash = manager.getPassword();

        assertThrows(Y9BusinessException.class,
            () -> service.changePassword("manager1", "currentPassword", "Chosen1!"));

        verify(policy).validatePassword("currentPassword", "Chosen1!", hash, algorithm);
        verify(repository, never()).save(any());
        assertEquals(hash, manager.getPassword());
        assertEquals(algorithm, manager.getPasswordHashAlgorithm());
        assertNull(manager.getLastModifyPasswordTime());
    }

    /** 绕过页面直接调用改密服务，也不能跳过原密码校验。 */
    @Test
    void missingOrIncorrectCurrentPasswordDoesNotModifyAccount() {
        for (String oldPassword : new String[] {null, "", " ", "incorrectPassword"}) {
            assertThrows(Y9BusinessException.class,
                () -> service.changePassword("manager1", oldPassword, "Chosen1!"));
        }
        verify(policy, times(4)).validatePassword(anyString(), eq("Chosen1!"), anyString(),
            eq(PasswordHashAlgorithmConsts.BCRYPT));
        verifyNoInteractions(settings);
        verify(repository, never()).save(any());
        assertTrue(manager.isPasswordCorrect("currentPassword"));
        assertNull(manager.getLastModifyPasswordTime());
    }

    @Test
    void failedStrengthValidationDoesNotModifyAccount() {
        doThrow(Y9BusinessException.class).when(policy).validatePassword(eq("currentPassword"), eq("weak"), anyString(), eq(PasswordHashAlgorithmConsts.BCRYPT));
        assertThrows(Y9BusinessException.class,
            () -> service.changePassword("manager1", "currentPassword", "weak"));
        verify(repository, never()).save(any());
        assertTrue(manager.isPasswordCorrect("currentPassword"));
        assertNull(manager.getLastModifyPasswordTime());
    }

    @Test
    void unchangedPasswordDoesNotModifyAccountOrRenewExpiration() {
        String hash = manager.getPassword();
        Date passwordTime = new Date(0);
        manager.setLastModifyPasswordTime(passwordTime);
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> service.changePassword("manager1", "currentPassword", "currentPassword"));
        assertEquals(OrgUnitErrorCodeEnum.NEW_PASSWORD_SAME_AS_OLD.getCode(), exception.getCode());
        verify(repository, never()).save(any());
        assertEquals(hash, manager.getPassword());
        assertEquals(passwordTime, manager.getLastModifyPasswordTime());
        assertTrue(service.isPasswordExpired("manager1"));
    }

    @Test
    void resetRestoresDefaultPasswordWithoutPolicyValidation() {
        manager.setPasswordHashAlgorithm("unknown");
        TenantSetting setting = new TenantSetting();
        setting.setDefaultPassword("x");
        when(settings.getTenantSetting()).thenReturn(setting);
        Date passwordTime = new Date(0);
        manager.setLastModifyPasswordTime(passwordTime);

        service.resetDefaultPassword("manager1");

        verifyNoInteractions(policy);
        verify(repository).save(manager);
        assertTrue(Y9MessageDigestUtil.bcryptMatch("x", manager.getPassword()));
        assertEquals(PasswordHashAlgorithmConsts.BCRYPT, manager.getPasswordHashAlgorithm());
        assertTrue(manager.getLastModifyPasswordTime().after(passwordTime));
    }

    @Test
    void resetSucceedsWhenDefaultPasswordIsAlreadyCurrent() {
        TenantSetting setting = new TenantSetting();
        setting.setDefaultPassword("currentPassword");
        when(settings.getTenantSetting()).thenReturn(setting);
        Date passwordTime = new Date(0);
        manager.setLastModifyPasswordTime(passwordTime);

        service.resetDefaultPassword("manager1");

        verifyNoInteractions(policy);
        verify(repository).save(manager);
        assertTrue(Y9MessageDigestUtil.bcryptMatch("currentPassword", manager.getPassword()));
        assertTrue(manager.getLastModifyPasswordTime().after(passwordTime));
    }

    @Test
    void expirationKeepsIndependentManagerCycle() {
        assertTrue(service.isPasswordExpired("manager1"));
        manager.changePassword("Chosen1!");
        assertFalse(service.isPasswordExpired("manager1"));
        manager.setLastModifyPasswordTime(Date.from(Instant.now().minus(8, ChronoUnit.DAYS)));
        assertTrue(service.isPasswordExpired("manager1"));
        properties.getManager().setManagerModifyPasswordCycle(30);
        assertFalse(service.isPasswordExpired("manager1"));
        verifyNoInteractions(settings, policy);
        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(Y9NotFoundException.class, () -> service.isPasswordExpired("missing"));
    }

    @Test
    void allManagerLevelsKeepConfiguredModificationCycle() {
        for (ManagerLevelEnum level : ManagerLevelEnum.values()) {
            assertEquals(7, service.getPasswordModifiedCycle(level));
        }
        properties.getManager().setManagerModifyPasswordCycle(30);
        assertEquals(30, service.getPasswordModifiedCycle(manager.getManagerLevel()));
        verifyNoInteractions(settings, policy);
    }
}
