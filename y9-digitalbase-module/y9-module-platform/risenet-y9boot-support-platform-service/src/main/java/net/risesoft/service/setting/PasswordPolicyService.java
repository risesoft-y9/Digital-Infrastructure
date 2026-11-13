package net.risesoft.service.setting;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.model.platform.org.PasswordStrength;
import net.risesoft.model.platform.org.PasswordValidity;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.setting.impl.TenantSetting;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/** 密码策略校验与人员密码有效性检查。 */
@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    /** 大写英文字母匹配规则。 */
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");

    /** 小写英文字母匹配规则。 */
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");

    /** 数字匹配规则。 */
    private static final Pattern DIGIT = Pattern.compile("[0-9]");

    /** ASCII 标点匹配规则，不包含空格、中文标点及其他 Unicode 字符。 */
    private static final Pattern SYMBOL = Pattern.compile("[\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7E]");

    private final Y9SettingService y9SettingService;

    private final Y9PlatformProperties y9PlatformProperties;

    /**
     * 获取密码强度要求，运维管理员使用平台默认配置，其他用户使用当前租户设置。
     *
     * @return 密码强度要求
     */
    public PasswordStrength getStrength() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        TenantSetting setting = userInfo != null && userInfo.isOperationManager()
            ? new TenantSetting(y9PlatformProperties.getTenantDefault())
            : y9SettingService.getTenantSetting();
        return new PasswordStrength(setting.getPasswordMinLength(), setting.isPasswordRequireUppercase(),
            setting.isPasswordRequireLowercase(), setting.isPasswordRequireDigit(), setting.isPasswordRequireSymbol());
    }

    /**
     * 保存前校验密码有效期和最小长度的配置范围。
     *
     * @param setting 待保存的租户设置
     * @throws net.risesoft.y9.exception.Y9BusinessException 密码策略配置超出允许范围
     */
    public static void validateSettings(TenantSetting setting) {
        Y9AssertUtil.isTrue(setting.getPasswordMaxAge() >= 0 && setting.getPasswordMaxAge() <= 365,
            OrgUnitErrorCodeEnum.PASSWORD_POLICY_INVALID, "有效期必须为 0～365 天的整数");
        int minLength = setting.getPasswordMinLength();
        Y9AssertUtil.isTrue(minLength >= 6 && minLength <= 32, OrgUnitErrorCodeEnum.PASSWORD_POLICY_INVALID,
            "最小长度须设置为 6～32 的整数");
    }

    /**
     * 校验输入的旧密码正确、新旧密码不同，以及新密码的非空、最小长度及必需字符类型。
     *
     * @param oldPassword 用户输入的旧明文密码，null 表示新建或兼容无旧密码输入的入口；空字符串不合法
     * @param newPassword 用户输入的新明文密码
     * @param oldPasswordHash 已保存的旧密码哈希，新建时为 null
     * @param oldPasswordHashAlgorithm 已保存的旧密码哈希算法，新建时为 null
     * @throws net.risesoft.y9.exception.Y9BusinessException 旧密码不正确、新旧密码相同或新密码不符合强度要求
     */
    public void validatePassword(String oldPassword, String newPassword, String oldPasswordHash,
        String oldPasswordHashAlgorithm) {
        if (oldPassword != null) {
            Y9AssertUtil.isTrue(
                StringUtils.isNotBlank(oldPassword)
                    && Y9MessageDigestUtil.passwordMatch(oldPassword, oldPasswordHash, oldPasswordHashAlgorithm),
                OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT);
        }
        Y9AssertUtil.isTrue(StringUtils.isNotBlank(newPassword), OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID,
            "密码不能为空");
        boolean samePassword = StringUtils.equals(oldPassword, newPassword)
            || Y9MessageDigestUtil.passwordMatch(newPassword, oldPasswordHash, oldPasswordHashAlgorithm);
        Y9AssertUtil.isTrue(!samePassword, OrgUnitErrorCodeEnum.NEW_PASSWORD_SAME_AS_OLD);
        PasswordStrength strength = getStrength();
        int minLength = strength.getMinLength();
        Y9AssertUtil.isTrue(newPassword.length() >= minLength, OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID,
            "长度至少为 " + minLength + " 位");
        checkCharacter(strength.isRequireUppercase(), UPPERCASE, newPassword, "必须包含大写字母");
        checkCharacter(strength.isRequireLowercase(), LOWERCASE, newPassword, "必须包含小写字母");
        checkCharacter(strength.isRequireDigit(), DIGIT, newPassword, "必须包含数字");
        checkCharacter(strength.isRequireSymbol(), SYMBOL, newPassword, "必须包含英文标点符号");
    }

    /**
     * 校验新密码的强度。
     *
     * @param newPassword 用户输入的新明文密码
     */
    public void validateStrength(String newPassword) {
        validatePassword(null, newPassword, null, null);
    }

    /**
     * 仅在启用对应字符要求时检查密码是否至少包含一个匹配字符。
     *
     * @param required 是否启用该字符要求
     * @param pattern 字符匹配规则
     * @param password 待校验的明文密码
     * @param message 缺少该类字符时的提示信息
     */
    private void checkCharacter(boolean required, Pattern pattern, String password, String message) {
        Y9AssertUtil.isTrue(!required || pattern.matcher(password).find(),
            OrgUnitErrorCodeEnum.PASSWORD_STRENGTH_INVALID, message);
    }

    /**
     * 按当前租户配置和当前时间判断密码是否需要修改，不追溯校验已有密码的强度。
     *
     * @param person 待检查的人员
     * @return 默认密码强制修改和密码过期的检查结果
     */
    public PasswordValidity getValidity(Y9Person person) {
        return getValidity(person, y9SettingService.getTenantSetting(), new Date());
    }

    /**
     * 使用指定配置和时间检查密码有效性；启用有效期但无修改时间记录时视为过期。
     *
     * @param person 待检查的人员
     * @param setting 检查使用的租户设置
     * @param now 检查基准时间
     * @return 默认密码强制修改和密码过期的检查结果
     */
    PasswordValidity getValidity(Y9Person person, TenantSetting setting, Date now) {
        // 按人员当前的哈希算法识别租户默认密码。
        boolean defaultPasswordChangeRequired = setting.isDefaultPasswordChangeRequired()
            && StringUtils.isNotBlank(person.getPassword()) && Y9MessageDigestUtil
                .passwordMatch(setting.getDefaultPassword(), person.getPassword(), person.getPasswordHashAlgorithm());
        Date lastModifyPasswordTime = person.getLastModifyPasswordTime();
        boolean expired = setting.getPasswordMaxAge() > 0 && (lastModifyPasswordTime == null || now.toInstant()
            .isAfter(lastModifyPasswordTime.toInstant().plus(setting.getPasswordMaxAge(), ChronoUnit.DAYS)));
        return new PasswordValidity(!defaultPasswordChangeRequired && !expired, defaultPasswordChangeRequired, expired);
    }
}
