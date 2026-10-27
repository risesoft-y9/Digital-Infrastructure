package net.risesoft.service.org;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import net.risesoft.consts.PasswordHashAlgorithmConsts;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.model.platform.org.PasswordPolicy;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.service.org.impl.Y9PersonServiceImpl;
import net.risesoft.service.setting.UserPasswordPolicyService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

class Y9PersonPasswordPolicyTest {

    @Mock
    private Y9PersonManager manager;
    @Mock
    private Y9SettingService settings;
    @Mock
    private UserPasswordPolicyService policy;
    @InjectMocks
    private Y9PersonServiceImpl service;

    private AutoCloseable mocks;
    private ApplicationContext previousContext;
    private Y9Person person;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        previousContext = Y9Context.getAc();
        new Y9Context().setApplicationContext(mock(ApplicationContext.class));
        person = new Y9Person();
        person.setId("person1");
        person.setName("测试用户");
        person.setPassword(Y9MessageDigestUtil.bcrypt("default"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        when(manager.getById("person1")).thenReturn(person);
        when(manager.update(any(), any())).thenAnswer(call -> call.getArgument(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        new Y9Context().setApplicationContext(previousContext);
        mocks.close();
    }

    @Test
    void changeValidatesStrengthAndRecordsTime() {
        service.modifyPassword("person1", "default", "Chosen1!");
        verify(policy).validatePassword("Chosen1!");
        assertNotNull(person.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("Chosen1!", person.getPassword()));
    }

    @Test
    void failedStrengthValidationDoesNotChangePasswordOrRecordTime() {
        doThrow(Y9BusinessException.class).when(policy).validatePassword("weak");
        assertThrows(Y9BusinessException.class, () -> service.modifyPassword("person1", "default", "weak"));
        assertNull(person.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("default", person.getPassword()));
        verify(manager, never()).update(any(), any());
    }

    @Test
    void resetBypassesStrengthAndRestoresDefaultPassword() {
        person.changePassword(null, "Chosen1!");
        TenantSetting setting = new TenantSetting();
        setting.setUserDefaultPassword("x");
        when(settings.getTenantSetting()).thenReturn(setting);
        service.resetDefaultPassword("person1");
        verifyNoInteractions(policy);
        assertNotNull(person.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("x", person.getPassword()));
    }

    @Test
    void validityAndPasswordChangesUseRequestedAccountForPartTimeIdentity() {
        Y9Person partTime = new Y9Person();
        partTime.setId("partTime");
        partTime.setOriginal(false);
        partTime.setOriginalId("person1");
        partTime.setPassword(Y9MessageDigestUtil.bcrypt("default"));
        partTime.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        when(manager.getById("partTime")).thenReturn(partTime);
        PasswordValidity expected = new PasswordValidity(false, true, false);
        when(policy.getValidity(partTime)).thenReturn(expected);
        assertSame(expected, service.checkPasswordValidity("partTime"));
        verify(policy).getValidity(partTime);
        service.modifyPassword("partTime", "default", "Chosen1!");
        verify(manager).update(eq(partTime), any());
        verify(manager, never()).getById("person1");
        assertNotNull(partTime.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("Chosen1!", partTime.getPassword()));
        assertTrue(Y9MessageDigestUtil.bcryptMatch("default", person.getPassword()));
    }

    @Test
    void returnsTenantPolicyWithoutLookingUpPerson() {
        PasswordPolicy expected = new PasswordPolicy(true, 90, 8, true, true, true, false);
        when(policy.getPolicy()).thenReturn(expected);
        assertSame(expected, service.getPasswordPolicy("person1"));
        assertSame(expected, service.getPasswordPolicy("partTime"));
        assertSame(expected, service.getPasswordPolicy("missing"));
        verify(policy, times(3)).getPolicy();
        verifyNoInteractions(manager);
    }

    @Test
    void missingOrdinaryUserDoesNotReturnPasswordValidity() {
        when(manager.getById("missing")).thenThrow(Y9NotFoundException.class);
        assertThrows(Y9NotFoundException.class, () -> service.checkPasswordValidity("missing"));
        verifyNoInteractions(policy);
    }
}
