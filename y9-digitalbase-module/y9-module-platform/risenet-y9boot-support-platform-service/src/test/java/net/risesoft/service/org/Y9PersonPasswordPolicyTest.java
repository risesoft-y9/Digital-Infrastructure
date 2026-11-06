package net.risesoft.service.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import net.risesoft.consts.PasswordHashAlgorithmConsts;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.model.platform.org.PasswordStrength;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.service.org.impl.Y9PersonServiceImpl;
import net.risesoft.service.setting.PasswordPolicyService;
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
    private PasswordPolicyService policy;
    @Mock
    private CompositeOrgBaseManager orgManager;
    @Mock
    private Y9PersonExtManager personExtManager;
    @InjectMocks
    private Y9PersonServiceImpl service;

    private AutoCloseable mocks;
    private ApplicationContext previousContext;
    private Y9Person person;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(settings.getTenantSetting()).thenReturn(new TenantSetting());
        doAnswer(delegatesTo(new PasswordPolicyService(settings))).when(policy)
            .validatePassword(any(), any(), any(), any());
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
        verify(policy).validatePassword(eq("default"), eq("Chosen1!"), anyString(),
            eq(PasswordHashAlgorithmConsts.BCRYPT));
        assertNotNull(person.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("Chosen1!", person.getPassword()));
    }

    @Test
    void failedStrengthValidationDoesNotChangePasswordOrRecordTime() {
        doThrow(Y9BusinessException.class).when(policy)
            .validatePassword(eq("default"), eq("weak"), anyString(), eq(PasswordHashAlgorithmConsts.BCRYPT));
        assertThrows(Y9BusinessException.class, () -> service.modifyPassword("person1", "default", "weak"));
        assertNull(person.getLastModifyPasswordTime());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("default", person.getPassword()));
        verify(manager, never()).update(any(), any());
    }

    @Test
    void incorrectOldPasswordDoesNotModifyAccount() {
        String hash = person.getPassword();
        Date passwordTime = new Date(0);
        person.setLastModifyPasswordTime(passwordTime);
        for (String oldPassword : new String[] {"", " ", "incorrectPassword"}) {
            Y9BusinessException exception = assertThrows(Y9BusinessException.class,
                () -> service.modifyPassword("person1", oldPassword, "Chosen1!"));
            assertEquals(OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT.getCode(), exception.getCode());
        }
        verify(manager, never()).update(any(), any());
        assertEquals(hash, person.getPassword());
        assertEquals(passwordTime, person.getLastModifyPasswordTime());
    }

    @Test
    void oldPasswordVerificationUsesStoredAlgorithm() {
        person.setPasswordHashAlgorithm("unknown");
        String hash = person.getPassword();
        Y9BusinessException exception =
            assertThrows(Y9BusinessException.class, () -> service.modifyPassword("person1", "default", "Chosen1!"));
        assertEquals(OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT.getCode(), exception.getCode());
        verify(manager, never()).update(any(), any());
        assertEquals(hash, person.getPassword());
        assertEquals("unknown", person.getPasswordHashAlgorithm());
        assertNull(person.getLastModifyPasswordTime());
    }

    @Test
    void unchangedPasswordIsRejectedForNormalAndLegacyChanges() {
        String hash = person.getPassword();
        Date passwordTime = new Date(0);
        person.setLastModifyPasswordTime(passwordTime);
        for (String oldPassword : new String[] {"default", null}) {
            Y9BusinessException exception = assertThrows(Y9BusinessException.class,
                () -> service.modifyPassword("person1", oldPassword, "default"));
            assertEquals(OrgUnitErrorCodeEnum.NEW_PASSWORD_SAME_AS_OLD.getCode(), exception.getCode());
            assertEquals(hash, person.getPassword());
            assertEquals(passwordTime, person.getLastModifyPasswordTime());
        }
        verify(manager, never()).update(any(), any());
    }

    @Test
    void profileUpdatePreservesPasswordAndDoesNotValidateStrength() {
        Y9Organization parent = new Y9Organization();
        parent.setId("org1");
        parent.setDn("o=测试组织");
        parent.setGuidPath("org1");
        person.setParentId(parent.getId());
        Date passwordTime = new Date(0);
        person.setLastModifyPasswordTime(passwordTime);
        when(manager.findById("person1")).thenReturn(Optional.of(person));
        when(orgManager.getOrgUnitAsParent("org1")).thenReturn(parent);
        when(orgManager.listOrgUnitAndAncestor("org1")).thenReturn(Collections.singletonList(parent));
        Person update = new Person();
        update.setId("person1");
        update.setParentId("org1");
        update.setName("更新后的用户");
        String hash = person.getPassword();

        Person saved = service.saveOrUpdate(update, new PersonExt());

        assertEquals("更新后的用户", saved.getName());
        assertEquals(hash, person.getPassword());
        assertEquals(PasswordHashAlgorithmConsts.BCRYPT, person.getPasswordHashAlgorithm());
        assertEquals(passwordTime, person.getLastModifyPasswordTime());
        verify(manager).update(eq(person), any());
        verifyNoInteractions(policy);
    }

    @Test
    void resetRestoresDefaultPasswordWithoutPolicyValidation() {
        TenantSetting setting = new TenantSetting();
        setting.setDefaultPassword("x");
        when(settings.getTenantSetting()).thenReturn(setting);
        Date passwordTime = new Date(0);
        person.setLastModifyPasswordTime(passwordTime);

        service.resetDefaultPassword("person1");

        verifyNoInteractions(policy);
        verify(manager).update(eq(person), any());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("x", person.getPassword()));
        assertTrue(person.getLastModifyPasswordTime().after(passwordTime));
    }

    @Test
    void resetSucceedsWhenDefaultPasswordIsAlreadyCurrent() {
        TenantSetting setting = new TenantSetting();
        setting.setDefaultPassword("default");
        when(settings.getTenantSetting()).thenReturn(setting);
        Date passwordTime = new Date(0);
        person.setLastModifyPasswordTime(passwordTime);

        service.resetDefaultPassword("person1");

        verifyNoInteractions(policy);
        verify(manager).update(eq(person), any());
        assertTrue(Y9MessageDigestUtil.bcryptMatch("default", person.getPassword()));
        assertTrue(person.getLastModifyPasswordTime().after(passwordTime));
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
    void returnsTenantStrengthWithoutLookingUpPerson() {
        PasswordStrength expected = new PasswordStrength(8, true, true, true, false);
        when(policy.getStrength()).thenReturn(expected);
        assertSame(expected, service.getPasswordStrength("person1"));
        assertSame(expected, service.getPasswordStrength("partTime"));
        assertSame(expected, service.getPasswordStrength("missing"));
        verify(policy, times(3)).getStrength();
        verifyNoInteractions(manager);
    }

    @Test
    void missingOrdinaryUserDoesNotReturnPasswordValidity() {
        when(manager.getById("missing")).thenThrow(Y9NotFoundException.class);
        assertThrows(Y9NotFoundException.class, () -> service.checkPasswordValidity("missing"));
        verifyNoInteractions(policy);
    }
}
