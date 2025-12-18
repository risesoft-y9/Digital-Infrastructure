package net.risesoft.y9.util;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.risesoft.exception.ErrorCode;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 断言工具类
 *
 * @author shidaobang
 * @date 2022/09/21
 * @since 9.6.2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9AssertUtil {

    /**
     * 当前值需小于或等于最大值 反之则抛出异常 {@link Y9BusinessException}
     * 
     * @param currentValue 当前值
     * @param maxValue 最大值
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void lessThanOrEqualTo(int currentValue, int maxValue, ErrorCode errorCode, Object... arguments) {
        if (currentValue > maxValue) {
            throw Y9ExceptionUtil.businessException(errorCode, arguments);
        }
    }

    /**
     * 当 object 为空时抛出异常 {@link Y9NotFoundException}
     * 
     * @param object 判断对象
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void notNull(Object object, ErrorCode errorCode, Object... arguments) {
        if (object == null) {
            throw Y9ExceptionUtil.notFoundException(errorCode, arguments);
        }
    }

    /**
     * 当 object 不为空时抛出异常 {@link Y9BusinessException}
     *
     * @param object 判断对象
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void isNull(Object object, ErrorCode errorCode, Object... arguments) {
        if (object != null) {
            throw Y9ExceptionUtil.businessException(errorCode, arguments);
        }
    }

    /**
     * 当 list 不为空时抛出异常 {@link Y9BusinessException}
     *
     * @param list 列表
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void isEmpty(Collection<?> list, ErrorCode errorCode, Object... arguments) {
        if (CollectionUtils.isNotEmpty(list)) {
            throw Y9ExceptionUtil.businessException(errorCode, arguments);
        }
    }

    /**
     * 当 isTrue 为假时抛出异常 {@link Y9BusinessException}
     *
     * @param isTrue 是否为真
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void isTrue(boolean isTrue, ErrorCode errorCode, Object... arguments) {
        if (!isTrue) {
            throw Y9ExceptionUtil.businessException(errorCode, arguments);
        }
    }

    /**
     * 当 isTrue 为真时抛出异常 {@link Y9BusinessException}
     *
     * @param isTrue 是否为真
     * @param errorCode 错误代码
     * @param arguments 参数
     */
    public static void isNotTrue(boolean isTrue, ErrorCode errorCode, Object... arguments) {
        isTrue(!isTrue, errorCode, arguments);
    }
}
