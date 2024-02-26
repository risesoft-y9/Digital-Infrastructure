package net.risesoft.y9.exception.util;

import net.risesoft.exception.ErrorCode;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.exception.Y9PermissionException;

import cn.hutool.core.util.StrUtil;

/**
 * 异常工具类 新建异常并格式化错误信息
 *
 * @author shidaobang
 * @date 2023/06/06
 * @since 9.6.2
 */
public class Y9ExceptionUtil {

    public static Y9NotFoundException notFoundException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9NotFoundException(errorCode.getCode(), message);
    }

    public static Y9BusinessException businessException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9BusinessException(errorCode.getCode(), message);
    }

    public static Y9PermissionException permissionException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9PermissionException(errorCode.getCode(), message);
    }

}
