package net.risesoft.y9.exception.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9ExceptionUtil {

    /**
     * 创建资源不存在异常
     *
     * @param errorCode 错误码，其描述用于格式化异常信息
     * @param arguments 格式化错误描述的参数
     * @return 资源不存在异常
     */
    public static Y9NotFoundException notFoundException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9NotFoundException(errorCode.getCode(), message);
    }

    /**
     * 创建业务异常
     *
     * @param errorCode 错误码，其描述用于格式化异常信息
     * @param arguments 格式化错误描述的参数
     * @return 业务异常
     */
    public static Y9BusinessException businessException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9BusinessException(errorCode.getCode(), message);
    }

    /**
     * 创建权限异常
     *
     * @param errorCode 错误码，其描述用于格式化异常信息
     * @param arguments 格式化错误描述的参数
     * @return 权限异常
     */
    public static Y9PermissionException permissionException(ErrorCode errorCode, Object... arguments) {
        String message = StrUtil.format(errorCode.getDescription(), arguments);
        return new Y9PermissionException(errorCode.getCode(), message);
    }

}
