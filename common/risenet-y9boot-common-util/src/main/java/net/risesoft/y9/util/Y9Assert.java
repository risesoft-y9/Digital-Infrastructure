package net.risesoft.y9.util;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

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
public class Y9Assert {

    /**
     * 当前值需小于或等于最大值 反之则抛出异常 {@link Y9BusinessException}
     * 
     * @param currentValue
     * @param maxValue
     * @param errorCode
     * @param arguments
     */
    public static void lessThanOrEqualTo(int currentValue, int maxValue, ErrorCode errorCode, Object... arguments) {
        if (currentValue > maxValue) {
            throw Y9ExceptionUtil.businessException(errorCode, arguments);
        }
    }

    /**
     * 当 object 为空时抛出异常 {@link Y9NotFoundException}
     * 
     * @param object
     * @param errorCode
     * @param arguments
     */
    public static void notNull(Object object, ErrorCode errorCode, Object... arguments) {
        if (object == null) {
            throw Y9ExceptionUtil.notFoundException(errorCode, arguments);
        }
    }

    /**
     * 当 object 不为空时抛出异常 {@link Y9BusinessException}
     *
     * @param object
     * @param errorCode
     * @param arguments
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
}
