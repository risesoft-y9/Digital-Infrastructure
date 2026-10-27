package net.risesoft.service.setting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.consts.PasswordHashAlgorithmConsts;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.model.platform.org.PasswordPolicy;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

class UserPasswordPolicyServiceTest {

    private static final Date NOW = Date.from(Instant.parse("2026-10-27T00:00:00Z"));
    private TenantSetting setting;
    private UserPasswordPolicyService policy;
    private Y9SettingService settingService;

    @BeforeEach
    void setUp() {
        setting = new TenantSetting();
        settingService = mock(Y9SettingService.class);
        when(settingService.getTenantSetting()).thenReturn(setting);
        policy = new UserPasswordPolicyService(settingService);
    }

    @Test
    void policyDefaultsRequireSixCharacters() {
        assertEquals(new PasswordPolicy(false, 0, 6, false, false, false, false), policy.getPolicy());
    }

    @Test
    void policyResponseUsesConciseNamesWithoutDefaultPassword() {
        setting.setUserDefaultPassword("sensitiveDefault");
        setting.setPositionNameTemplate("unrelatedSetting");
        setting.setUserDefaultPasswordChangeRequired(true);
        setting.setUserPasswordMaxAge(90);
        setting.setUserPasswordMinLength(8);
        setting.setUserPasswordRequireUppercase(true);
        setting.setUserPasswordRequireDigit(true);
        PasswordPolicy result = policy.getPolicy();
        assertEquals(new PasswordPolicy(true, 90, 8, true, false, true, false), result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.valueToTree(result);
        assertTrue(json.get("defaultPasswordChangeRequired").booleanValue());
        assertEquals(90, json.get("maxAge").intValue());
        assertEquals(8, json.get("minLength").intValue());
        assertTrue(json.get("requireUppercase").booleanValue());
        assertFalse(json.get("requireLowercase").booleanValue());
        assertTrue(json.get("requireDigit").booleanValue());
        assertFalse(json.get("requireSymbol").booleanValue());
        assertFalse(json.has("userDefaultPassword"));
        assertFalse(json.has("positionNameTemplate"));
        assertFalse(json.toString().contains("sensitiveDefault"));
        assertEquals(7, json.size());
    }

    @Test
    void policyReadsCurrentTenantSettingsOnEveryRequest() {
        PasswordPolicy first = policy.getPolicy();
        TenantSetting otherTenant = new TenantSetting();
        otherTenant.setUserPasswordMaxAge(365);
        otherTenant.setUserPasswordMinLength(32);
        otherTenant.setUserPasswordRequireLowercase(true);
        otherTenant.setUserPasswordRequireSymbol(true);
        when(settingService.getTenantSetting()).thenReturn(otherTenant);
        assertEquals(new PasswordPolicy(false, 365, 32, false, true, false, true), policy.getPolicy());
        assertEquals(0, first.getMaxAge());
        assertEquals(6, first.getMinLength());
        verify(settingService, times(2)).getTenantSetting();
    }

    @Test
    void defaultsRequireSixCharactersWithoutRetroactivelyCheckingHistoricalPasswords() {
        assertTrue(policy.getValidity(new Y9Person(), setting, NOW).isValid());
        assertDoesNotThrow(() -> policy.validatePassword("abcdef"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("abcde"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(""));
    }

    @Test
    void settingsAcceptInclusiveBoundsAndRequireMinimumLength() {
        assertDoesNotThrow(() -> UserPasswordPolicyService.validateSettings(setting));
        setting.setUserPasswordMaxAge(365);
        setting.setUserPasswordMinLength(6);
        assertDoesNotThrow(() -> UserPasswordPolicyService.validateSettings(setting));
        setting.setUserPasswordMinLength(32);
        assertDoesNotThrow(() -> UserPasswordPolicyService.validateSettings(setting));
        for (int invalid : new int[] {-1, 366}) {
            setting.setUserPasswordMaxAge(invalid);
            assertThrows(Y9BusinessException.class, () -> UserPasswordPolicyService.validateSettings(setting));
        }
        setting.setUserPasswordMaxAge(0);
        for (int invalid : new int[] {0, 5, 33}) {
            setting.setUserPasswordMinLength(invalid);
            assertThrows(Y9BusinessException.class, () -> UserPasswordPolicyService.validateSettings(setting));
        }
    }

    @Test
    void minimumLengthDoesNotImposeAMaximumPasswordLength() {
        setting.setUserPasswordMinLength(6);
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("abcde"));
        assertDoesNotThrow(() -> policy.validatePassword("abcdef"));
        setting.setUserPasswordMinLength(32);
        assertDoesNotThrow(() -> policy.validatePassword("a".repeat(40)));
    }

    @Test
    void eachCharacterRequirementCanBeEnabledIndependently() {
        setting.setUserPasswordRequireUppercase(true);
        assertDoesNotThrow(() -> policy.validatePassword("AAAAAA"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("abc12!"));
        setting.setUserPasswordRequireUppercase(false);
        setting.setUserPasswordRequireLowercase(true);
        assertDoesNotThrow(() -> policy.validatePassword("aaaaaa"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("ABC12!"));
        setting.setUserPasswordRequireLowercase(false);
        setting.setUserPasswordRequireDigit(true);
        assertDoesNotThrow(() -> policy.validatePassword("111111"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("Abcde!"));
        setting.setUserPasswordRequireDigit(false);
        setting.setUserPasswordRequireSymbol(true);
        assertDoesNotThrow(() -> policy.validatePassword("!!!!!!"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("Abc123"));
    }

    @Test
    void symbolMeansAsciiPunctuationAndAllSelectedTypesAreRequired() {
        setting.setUserPasswordRequireSymbol(true);
        for (String symbol : new String[] {"!", "/", ":", "@", "[", "\\", "`", "{", "~"}) {
            assertDoesNotThrow(() -> policy.validatePassword("abcde" + symbol));
        }
        for (String symbol : new String[] {"中文", "。", "！", "Ａ", "１", "😀", " "}) {
            assertThrows(Y9BusinessException.class, () -> policy.validatePassword("abcde" + symbol));
        }
        setting.setUserPasswordMinLength(6);
        setting.setUserPasswordRequireUppercase(true);
        setting.setUserPasswordRequireLowercase(true);
        setting.setUserPasswordRequireDigit(true);
        assertDoesNotThrow(() -> policy.validatePassword("Abc12!"));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword("Abcdef!"));
    }

    @Test
    void defaultPasswordCheckUsesCurrentTenantDefault() {
        setting.setUserDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.setPassword(Y9MessageDigestUtil.bcrypt("oldDefault"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        setting.setUserDefaultPassword("oldDefault");
        assertFalse(policy.getValidity(person, setting, NOW).isValid());
        setting.setUserDefaultPassword("newDefault");
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.changePassword(null, "chosenPassword");
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.changePassword(null, "newDefault");
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
    }

    @Test
    void historicalAndExplicitlySetDefaultPasswordsRequireChange() {
        setting.setUserDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.setPassword(Y9MessageDigestUtil.bcrypt(setting.getUserDefaultPassword()));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.changePassword(null, setting.getUserDefaultPassword());
        assertFalse(policy.getValidity(person, setting, NOW).isValid());
        person.setPassword(Y9MessageDigestUtil.bcrypt("customPassword"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
    }

    @Test
    void defaultPasswordCheckUsesStoredAlgorithmWithoutBcryptFallback() {
        setting.setUserDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.changePassword(null, setting.getUserDefaultPassword());
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.setPasswordHashAlgorithm("unknown");
        assertFalse(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.setPasswordHashAlgorithm(null);
        assertFalse(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
    }

    @Test
    void missingChangeTimeIsExpiredOnlyWhenExpiryEnabled() {
        Y9Person person = new Y9Person();
        setting.setUserPasswordMaxAge(30);
        PasswordValidity result = policy.getValidity(person, setting, NOW);
        assertFalse(result.isValid());
        assertTrue(result.isExpired());
        setting.setUserPasswordMaxAge(0);
        assertEquals(new PasswordValidity(true, false, false), policy.getValidity(person, setting, NOW));
    }

    @Test
    void expiresAtExactDayBoundaryAndZeroMeansPermanent() {
        Y9Person person = new Y9Person();
        setting.setUserPasswordMaxAge(365);
        Instant boundary = NOW.toInstant().minus(365, ChronoUnit.DAYS);
        person.setLastModifyPasswordTime(Date.from(boundary.plusMillis(1)));
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.setLastModifyPasswordTime(Date.from(boundary));
        assertTrue(policy.getValidity(person, setting, NOW).isExpired());
        setting.setUserPasswordMaxAge(0);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
    }

    @Test
    void validityDoesNotRetroactivelyCheckStrengthAndReportsAllReasons() {
        Y9Person person = new Y9Person();
        person.changePassword(null, "a");
        setting.setUserPasswordRequireUppercase(true);
        setting.setUserPasswordMinLength(32);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        setting.setUserDefaultPasswordChangeRequired(true);
        setting.setUserPasswordMaxAge(1);
        person.setPassword(Y9MessageDigestUtil.bcrypt(setting.getUserDefaultPassword()));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        person.setLastModifyPasswordTime(Date.from(NOW.toInstant().minus(1, ChronoUnit.DAYS)));
        PasswordValidity result = policy.getValidity(person, setting, NOW);
        assertFalse(result.isValid());
        assertTrue(result.isDefaultPasswordChangeRequired());
        assertTrue(result.isExpired());
        JsonNode json = new ObjectMapper().valueToTree(result);
        assertTrue(json.get("defaultPasswordChangeRequired").booleanValue());
        assertEquals(3, json.size());
    }

    @Test
    void validityDependsOnlyOnDefaultPasswordRequirementAndExpiry() {
        Y9Person person = new Y9Person();
        person.changePassword(null, setting.getUserDefaultPassword());
        setting.setUserPasswordMaxAge(30);
        for (boolean defaultPasswordChangeRequired : new boolean[] {false, true}) {
            setting.setUserDefaultPasswordChangeRequired(defaultPasswordChangeRequired);
            for (boolean expired : new boolean[] {false, true}) {
                person.setLastModifyPasswordTime(Date.from(NOW.toInstant().minus(expired ? 30 : 1, ChronoUnit.DAYS)));
                assertEquals(new PasswordValidity(!defaultPasswordChangeRequired && !expired,
                    defaultPasswordChangeRequired, expired), policy.getValidity(person, setting, NOW));
            }
        }
    }
}
