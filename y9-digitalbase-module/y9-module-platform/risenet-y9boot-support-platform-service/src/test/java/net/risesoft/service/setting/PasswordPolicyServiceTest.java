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
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.model.platform.org.PasswordStrength;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

class PasswordPolicyServiceTest {

    private static final Date NOW = Date.from(Instant.parse("2026-10-27T00:00:00Z"));
    private TenantSetting setting;
    private PasswordPolicyService policy;
    private Y9SettingService settingService;

    @BeforeEach
    void setUp() {
        setting = new TenantSetting();
        settingService = mock(Y9SettingService.class);
        when(settingService.getTenantSetting()).thenReturn(setting);
        policy = new PasswordPolicyService(settingService);
    }

    @Test
    void strengthDefaultsRequireSixCharacters() {
        assertEquals(new PasswordStrength(6, false, false, false, false), policy.getStrength());
    }

    @Test
    void strengthResponseContainsOnlyStrengthRequirements() {
        setting.setDefaultPassword("sensitiveDefault");
        setting.setPositionNameTemplate("unrelatedSetting");
        setting.setDefaultPasswordChangeRequired(true);
        setting.setPasswordMaxAge(90);
        setting.setPasswordMinLength(8);
        setting.setPasswordRequireUppercase(true);
        setting.setPasswordRequireDigit(true);
        PasswordStrength result = policy.getStrength();
        assertEquals(new PasswordStrength(8, true, false, true, false), result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.valueToTree(result);
        assertFalse(json.has("defaultPasswordChangeRequired"));
        assertFalse(json.has("maxAge"));
        assertEquals(8, json.get("minLength").intValue());
        assertTrue(json.get("requireUppercase").booleanValue());
        assertFalse(json.get("requireLowercase").booleanValue());
        assertTrue(json.get("requireDigit").booleanValue());
        assertFalse(json.get("requireSymbol").booleanValue());
        assertFalse(json.has("defaultPassword"));
        assertFalse(json.has("positionNameTemplate"));
        assertFalse(json.toString().contains("sensitiveDefault"));
        assertEquals(5, json.size());
    }

    @Test
    void strengthReadsCurrentTenantSettingsOnEveryRequest() {
        PasswordStrength first = policy.getStrength();
        TenantSetting otherTenant = new TenantSetting();
        otherTenant.setPasswordMaxAge(365);
        otherTenant.setPasswordMinLength(32);
        otherTenant.setPasswordRequireLowercase(true);
        otherTenant.setPasswordRequireSymbol(true);
        when(settingService.getTenantSetting()).thenReturn(otherTenant);
        assertEquals(new PasswordStrength(32, false, true, false, true), policy.getStrength());
        assertEquals(6, first.getMinLength());
        verify(settingService, times(2)).getTenantSetting();
    }

    @Test
    void defaultsRequireSixCharactersWithoutRetroactivelyCheckingHistoricalPasswords() {
        assertTrue(policy.getValidity(new Y9Person(), setting, NOW).isValid());
        assertDoesNotThrow(() -> policy.validatePassword(null, "abcdef", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "abcde", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "", null, null));
    }

    @Test
    void unchangedPasswordIsRejectedRegardlessOfStrengthSettings() {
        String hash = Y9MessageDigestUtil.bcrypt("a");
        setting.setPasswordMinLength(32);
        setting.setPasswordRequireUppercase(true);
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> policy.validatePassword("a", "a", hash, PasswordHashAlgorithmConsts.BCRYPT));
        assertEquals(OrgUnitErrorCodeEnum.NEW_PASSWORD_SAME_AS_OLD.getCode(), exception.getCode());
        verifyNoInteractions(settingService);
    }

    @Test
    void differentPasswordStillMustMeetStrengthRequirements() {
        String hash = Y9MessageDigestUtil.bcrypt("currentPassword");
        assertDoesNotThrow(() -> policy.validatePassword("currentPassword", "Chosen1!", hash, PasswordHashAlgorithmConsts.BCRYPT));
        assertThrows(Y9BusinessException.class,
            () -> policy.validatePassword("currentPassword", "weak", hash, PasswordHashAlgorithmConsts.BCRYPT));
    }

    @Test
    void emptyOrIncorrectOldPasswordIsRejectedBeforeReadingSettings() {
        String hash = Y9MessageDigestUtil.bcrypt("currentPassword");
        for (String oldPassword : new String[] {"", " ", "incorrectPassword"}) {
            Y9BusinessException exception = assertThrows(Y9BusinessException.class,
                () -> policy.validatePassword(oldPassword, "Chosen1!", hash, PasswordHashAlgorithmConsts.BCRYPT));
            assertEquals(OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT.getCode(), exception.getCode());
        }
        verifyNoInteractions(settingService);
    }

    @Test
    void oldPasswordMustMatchUsingRecordedHashAlgorithm() {
        String hash = Y9MessageDigestUtil.bcrypt("currentPassword");
        for (String algorithm : new String[] {null, "", "unknown"}) {
            Y9BusinessException exception = assertThrows(Y9BusinessException.class,
                () -> policy.validatePassword("currentPassword", "Chosen1!", hash, algorithm));
            assertEquals(OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT.getCode(), exception.getCode());
        }
        verifyNoInteractions(settingService);
    }

    @Test
    void storedHashRejectsSamePasswordWithoutOldInput() {
        String hash = Y9MessageDigestUtil.bcrypt("a");
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> policy.validatePassword(null, "a", hash, PasswordHashAlgorithmConsts.BCRYPT));
        assertEquals(OrgUnitErrorCodeEnum.NEW_PASSWORD_SAME_AS_OLD.getCode(), exception.getCode());
        verifyNoInteractions(settingService);
    }

    @Test
    void incorrectOldPasswordIsRejectedBeforeNewPasswordChecks() {
        String hash = Y9MessageDigestUtil.bcrypt("currentPassword");
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> policy.validatePassword("differentInput", "currentPassword", hash, PasswordHashAlgorithmConsts.BCRYPT));
        assertEquals(OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT.getCode(), exception.getCode());
        verifyNoInteractions(settingService);
    }

    @Test
    void storedHashAppliesStrengthToDifferentPasswords() {
        String hash = Y9MessageDigestUtil.bcrypt("currentPassword");
        assertThrows(Y9BusinessException.class,
            () -> policy.validatePassword(null, "x", hash, PasswordHashAlgorithmConsts.BCRYPT));
        assertDoesNotThrow(() -> policy.validatePassword("currentPassword", "Chosen1!", hash, PasswordHashAlgorithmConsts.BCRYPT));
    }

    @Test
    void settingsAcceptInclusiveBoundsAndRequireMinimumLength() {
        assertDoesNotThrow(() -> PasswordPolicyService.validateSettings(setting));
        setting.setPasswordMaxAge(365);
        setting.setPasswordMinLength(6);
        assertDoesNotThrow(() -> PasswordPolicyService.validateSettings(setting));
        setting.setPasswordMinLength(32);
        assertDoesNotThrow(() -> PasswordPolicyService.validateSettings(setting));
        for (int invalid : new int[] {-1, 366}) {
            setting.setPasswordMaxAge(invalid);
            assertThrows(Y9BusinessException.class, () -> PasswordPolicyService.validateSettings(setting));
        }
        setting.setPasswordMaxAge(0);
        for (int invalid : new int[] {0, 5, 33}) {
            setting.setPasswordMinLength(invalid);
            assertThrows(Y9BusinessException.class, () -> PasswordPolicyService.validateSettings(setting));
        }
    }

    @Test
    void minimumLengthDoesNotImposeAMaximumPasswordLength() {
        setting.setPasswordMinLength(6);
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "abcde", null, null));
        assertDoesNotThrow(() -> policy.validatePassword(null, "abcdef", null, null));
        setting.setPasswordMinLength(32);
        assertDoesNotThrow(() -> policy.validatePassword(null, "a".repeat(40), null, null));
    }

    @Test
    void eachCharacterRequirementCanBeEnabledIndependently() {
        setting.setPasswordRequireUppercase(true);
        assertDoesNotThrow(() -> policy.validatePassword(null, "AAAAAA", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "abc12!", null, null));
        setting.setPasswordRequireUppercase(false);
        setting.setPasswordRequireLowercase(true);
        assertDoesNotThrow(() -> policy.validatePassword(null, "aaaaaa", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "ABC12!", null, null));
        setting.setPasswordRequireLowercase(false);
        setting.setPasswordRequireDigit(true);
        assertDoesNotThrow(() -> policy.validatePassword(null, "111111", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "Abcde!", null, null));
        setting.setPasswordRequireDigit(false);
        setting.setPasswordRequireSymbol(true);
        assertDoesNotThrow(() -> policy.validatePassword(null, "!!!!!!", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "Abc123", null, null));
    }

    @Test
    void symbolMeansAsciiPunctuationAndAllSelectedTypesAreRequired() {
        setting.setPasswordRequireSymbol(true);
        for (String symbol : new String[] {"!", "/", ":", "@", "[", "\\", "`", "{", "~"}) {
            assertDoesNotThrow(() -> policy.validatePassword(null, "abcde" + symbol, null, null));
        }
        for (String symbol : new String[] {"中文", "。", "！", "Ａ", "１", "😀", " "}) {
            assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "abcde" + symbol, null, null));
        }
        setting.setPasswordMinLength(6);
        setting.setPasswordRequireUppercase(true);
        setting.setPasswordRequireLowercase(true);
        setting.setPasswordRequireDigit(true);
        assertDoesNotThrow(() -> policy.validatePassword(null, "Abc12!", null, null));
        assertThrows(Y9BusinessException.class, () -> policy.validatePassword(null, "Abcdef!", null, null));
    }

    @Test
    void defaultPasswordCheckUsesCurrentTenantDefault() {
        setting.setDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.setPassword(Y9MessageDigestUtil.bcrypt("oldDefault"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        setting.setDefaultPassword("oldDefault");
        assertFalse(policy.getValidity(person, setting, NOW).isValid());
        setting.setDefaultPassword("newDefault");
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.changePassword("chosenPassword");
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.changePassword("newDefault");
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
    }

    @Test
    void historicalAndExplicitlySetDefaultPasswordsRequireChange() {
        setting.setDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.setPassword(Y9MessageDigestUtil.bcrypt(setting.getDefaultPassword()));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.changePassword(setting.getDefaultPassword());
        assertFalse(policy.getValidity(person, setting, NOW).isValid());
        person.setPassword(Y9MessageDigestUtil.bcrypt("customPassword"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
    }

    @Test
    void defaultPasswordCheckUsesStoredAlgorithmWithoutBcryptFallback() {
        setting.setDefaultPasswordChangeRequired(true);
        Y9Person person = new Y9Person();
        person.changePassword(setting.getDefaultPassword());
        assertTrue(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.setPasswordHashAlgorithm("unknown");
        assertFalse(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
        person.setPasswordHashAlgorithm(null);
        assertFalse(policy.getValidity(person, setting, NOW).isDefaultPasswordChangeRequired());
    }

    @Test
    void missingChangeTimeIsExpiredOnlyWhenExpiryEnabled() {
        Y9Person person = new Y9Person();
        setting.setPasswordMaxAge(30);
        PasswordValidity result = policy.getValidity(person, setting, NOW);
        assertFalse(result.isValid());
        assertTrue(result.isExpired());
        setting.setPasswordMaxAge(0);
        assertEquals(new PasswordValidity(true, false, false), policy.getValidity(person, setting, NOW));
    }

    @Test
    void expiresOnlyAfterExactDayBoundaryAndZeroMeansPermanent() {
        Y9Person person = new Y9Person();
        setting.setPasswordMaxAge(365);
        Instant boundary = NOW.toInstant().minus(365, ChronoUnit.DAYS);
        person.setLastModifyPasswordTime(Date.from(boundary.plusMillis(1)));
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.setLastModifyPasswordTime(Date.from(boundary));
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        person.setLastModifyPasswordTime(Date.from(boundary.minusMillis(1)));
        assertTrue(policy.getValidity(person, setting, NOW).isExpired());
        setting.setPasswordMaxAge(0);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
    }

    @Test
    void validityDoesNotRetroactivelyCheckStrengthAndReportsAllReasons() {
        Y9Person person = new Y9Person();
        person.changePassword("a");
        setting.setPasswordRequireUppercase(true);
        setting.setPasswordMinLength(32);
        assertTrue(policy.getValidity(person, setting, NOW).isValid());
        setting.setDefaultPasswordChangeRequired(true);
        setting.setPasswordMaxAge(1);
        person.setPassword(Y9MessageDigestUtil.bcrypt(setting.getDefaultPassword()));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        person.setLastModifyPasswordTime(Date.from(NOW.toInstant().minus(1, ChronoUnit.DAYS).minusMillis(1)));
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
        person.changePassword(setting.getDefaultPassword());
        setting.setPasswordMaxAge(30);
        for (boolean defaultPasswordChangeRequired : new boolean[] {false, true}) {
            setting.setDefaultPasswordChangeRequired(defaultPasswordChangeRequired);
            for (boolean expired : new boolean[] {false, true}) {
                person.setLastModifyPasswordTime(
                    Date.from(NOW.toInstant().minus(expired ? 30 : 1, ChronoUnit.DAYS).minusMillis(1)));
                assertEquals(new PasswordValidity(!defaultPasswordChangeRequired && !expired,
                    defaultPasswordChangeRequired, expired), policy.getValidity(person, setting, NOW));
            }
        }
    }
    /** 强度接口仅返回强度字段，不包含有效期和默认密码配置。 */
    @Test
    void strengthResponseContainsOnlySharedRequirements() {
        setting.setDefaultPassword("sensitiveDefault");
        setting.setDefaultPasswordChangeRequired(true);
        setting.setPasswordMaxAge(90);
        setting.setPasswordMinLength(12);
        setting.setPasswordRequireUppercase(true);
        setting.setPasswordRequireLowercase(true);
        setting.setPasswordRequireDigit(true);
        setting.setPasswordRequireSymbol(true);
        PasswordStrength strength = policy.getStrength();
        assertEquals(new PasswordStrength(12, true, true, true, true), strength);
        JsonNode json = new ObjectMapper().valueToTree(strength);
        assertEquals(5, json.size());
        assertFalse(json.has("maxAge"));
        assertFalse(json.has("defaultPasswordChangeRequired"));
        assertFalse(json.has("defaultPassword"));
        assertFalse(json.toString().contains("sensitiveDefault"));
        setting.setDefaultPasswordChangeRequired(false);
        setting.setPasswordMaxAge(0);
        assertEquals(strength, policy.getStrength());
        setting.setPasswordMinLength(8);
        assertEquals(8, policy.getStrength().getMinLength());
    }

}
