package net.risesoft.y9.exception;

/**
 * 找不到资源异常
 *
 * @author shidaobang
 * @date 2022/3/3
 */
public class Y9NotFoundException extends Y9BusinessException {
    
    private static final long serialVersionUID = -6973279273770807478L;

    public Y9NotFoundException(int code, String message) {
        super(code, message);
    }
}
