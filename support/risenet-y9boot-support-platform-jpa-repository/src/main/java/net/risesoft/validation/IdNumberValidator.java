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
        if (isValidHKCard(idNumber)) {
            return true;
        }
        return false;
    }

    /**
     * 验证香港身份证号码(存在Bug，部份特殊身份证无法检查)
     *
     * 如：身份证号码是：C668668（9） 香港身份证号码由三部分组成：一个英文字母；6个数字；括号及0-9中的任一个数字，或者字母A。
     * 括号中的数字或字母A，是校验码，用于检验括号前面的号码的逻辑正确性。
     *
     * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58, 前2位英文字符A-Z分别对应数字10-35
     * 最后一位校验码为0-9的数字加上字符"A"，"A"代表10 将身份证号码全部转换为数字，分别对应乘9、8...1相加的总和，整除11则证件号码有效
     *
     * @param idCard 身份证号码
     * @return 验证码是否符合
     */
    public static boolean isValidHKCard(String idCard) {
        String regularExpression = "^([A-Z]\\d{6,10}(\\(\\w{1}\\))?)$";
        return idCard.matches(regularExpression);
    }
}
