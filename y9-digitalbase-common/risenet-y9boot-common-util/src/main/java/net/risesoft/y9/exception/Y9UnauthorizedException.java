package net.risesoft.y9.exception;

/**
 * Y9未经授权异常
 *
 * @author shidaobang
 * @date 2024/01/08
 */
public class Y9UnauthorizedException extends Y9BusinessException {
    
    private static final long serialVersionUID = -4442163206637529850L;

    public Y9UnauthorizedException(int code, String message) {
        super(code, message);
    }
}
