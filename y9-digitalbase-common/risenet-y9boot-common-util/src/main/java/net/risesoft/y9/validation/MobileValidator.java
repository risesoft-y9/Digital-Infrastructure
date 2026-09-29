package net.risesoft.y9.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * 手机号验证器
 *
 * @author shidaobang
 * @date 2022/08/04
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(mobile)) {
            // 手机号非必填 为空直接验证通过
            return true;
        }
        return ValidateUtil.isMobile(mobile);
    }
}
