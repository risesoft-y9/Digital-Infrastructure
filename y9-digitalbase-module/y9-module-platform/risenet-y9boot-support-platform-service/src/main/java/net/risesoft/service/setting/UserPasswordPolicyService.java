package net.risesoft.service.setting;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.model.platform.org.PasswordPolicy;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/** 按当前租户配置检查普通用户密码，管理员不使用此策略。 */
@Service
@RequiredArgsConstructor
public class UserPasswordPolicyService {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    // ASCII 标点范围，不包含空格、中文标点及其他 Unicode 字符。
    private static final Pattern SYMBOL = Pattern.compile("[\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7E]");

    private final Y9SettingService y9SettingService;

    /** 仅返回可展示的策略字段，不包含默认密码等租户设置。 */
    public PasswordPolicy getPolicy() {
        TenantSetting tenantSetting = y9SettingService.getTenantSetting();
        return new PasswordPolicy(tenantSetting.isUserDefaultPasswordChangeRequired(),
            tenantSetting.getUserPasswordMaxAge(), tenantSetting.getUserPasswordMinLength(),
            tenantSetting.isUserPasswordRequireUppercase(), tenantSetting.isUserPasswordRequireLowercase(),
            tenantSetting.isUserPasswordRequireDigit(), tenantSetting.isUserPasswordRequireSymbol());
    }

    public static void validateSettings(TenantSetting setting) {
        Y9AssertUtil.isTrue(setting.getUserPasswordMaxAge() >= 0 && setting.getUserPasswordMaxAge() <= 365,
            OrgUnitErrorCodeEnum.PASSWORD_POLICY_INVALID, "有效期必须为 0～365 天的整数");
        int minLength = setting.getUserPasswordMinLength();
        Y9AssertUtil.isTrue(minLength >= 6 && minLength <= 32, OrgUnitErrorCodeEnum.PASSWORD_POLICY_INVALID,
            "最小长度须设置为 6～32 的整数");
    }

    public void validatePassword(String password) {
        TenantSetting setting = y9SettingService.getTenantSetting();
        Y9AssertUtil.isTrue(StringUtils.isNotBlank(password), OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID, "密码不能为空");
        int minLength = setting.getUserPasswordMinLength();
        Y9AssertUtil.isTrue(password.length() >= minLength, OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID,
            "长度至少为 " + minLength + " 位");
        checkCharacter(setting.isUserPasswordRequireUppercase(), UPPERCASE, password, "必须包含大写字母");
        checkCharacter(setting.isUserPasswordRequireLowercase(), LOWERCASE, password, "必须包含小写字母");
        checkCharacter(setting.isUserPasswordRequireDigit(), DIGIT, password, "必须包含数字");
        checkCharacter(setting.isUserPasswordRequireSymbol(), SYMBOL, password, "必须包含英文标点符号");
    }

    private void checkCharacter(boolean required, Pattern pattern, String password, String message) {
        Y9AssertUtil.isTrue(!required || pattern.matcher(password).find(),
            OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID, message);
    }

    public PasswordValidity getValidity(Y9Person person) {
        return getValidity(person, y9SettingService.getTenantSetting(), new Date());
    }

    PasswordValidity getValidity(Y9Person person, TenantSetting setting, Date now) {
        // 按人员当前的哈希算法识别租户默认密码。
        boolean defaultPasswordChangeRequired =
            setting.isUserDefaultPasswordChangeRequired() && StringUtils.isNotBlank(person.getPassword())
                && Y9MessageDigestUtil.passwordMatch(setting.getUserDefaultPassword(), person.getPassword(),
                    person.getPasswordHashAlgorithm());
        Date lastModifyPasswordTime = person.getLastModifyPasswordTime();
        boolean expired = setting.getUserPasswordMaxAge() > 0 && (lastModifyPasswordTime == null || !now.toInstant()
            .isBefore(lastModifyPasswordTime.toInstant().plus(setting.getUserPasswordMaxAge(), ChronoUnit.DAYS)));
        return new PasswordValidity(!defaultPasswordChangeRequired && !expired, defaultPasswordChangeRequired, expired);
    }
}
