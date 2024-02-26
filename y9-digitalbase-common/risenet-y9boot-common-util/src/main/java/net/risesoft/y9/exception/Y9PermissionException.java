package net.risesoft.y9.exception;

/**
 * 权限异常
 *
 * @author shidaobang
 * @date 2022/09/27
 */
public class Y9PermissionException extends Y9BusinessException {

    private static final long serialVersionUID = -4918425846234037437L;

    public Y9PermissionException(int code, String message) {
        super(code, message);
    }

}
