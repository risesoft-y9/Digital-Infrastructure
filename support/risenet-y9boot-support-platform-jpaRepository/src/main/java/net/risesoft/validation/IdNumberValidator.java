package net.risesoft.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.util.IdcardUtil;

/**
 * 身份证件号验证器
 *
 * @author shidaobang
 * @date 2022/08/04
 */
public class IdNumberValidator implements ConstraintValidator<IdNumber, String> {

    @Override
    public boolean isValid(String idNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(idNumber)) {
            // 身份证号非必填 为空直接验证通过
            return true;
        }
        if (IdcardUtil.isValidCard(idNumber)) {
            return true;
        }
        return false;
    }
}
