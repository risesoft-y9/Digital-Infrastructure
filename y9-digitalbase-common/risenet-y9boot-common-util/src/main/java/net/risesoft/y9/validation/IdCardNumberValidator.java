package net.risesoft.y9.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * 身份证件号验证器
 *
 * @author shidaobang
 * @date 2022/08/04
 */
public class IdCardNumberValidator implements ConstraintValidator<IdCardNumber, String> {

    @Override
    public boolean isValid(String idCardNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(idCardNumber)) {
            // 身份证号非必填 为空直接验证通过
            return true;
        }
        return ValidateUtil.isIdCardNumber(idCardNumber);
    }
}
