package net.risesoft.y9.exception;

/**
 * Y9 异常基类
 *
 * @author shidaobang
 * @date 2022/3/3
 */
public class Y9BaseException extends RuntimeException {

    private static final long serialVersionUID = 8475272281204909095L;

    public Y9BaseException() {
    }

    public Y9BaseException(String message) {
        super(message);
    }
}
