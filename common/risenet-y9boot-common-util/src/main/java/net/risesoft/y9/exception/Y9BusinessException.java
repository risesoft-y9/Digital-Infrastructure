package net.risesoft.y9.exception;

import lombok.Getter;

/**
 * 业务异常 可继续细分
 *
 * @author shidaobang
 * @date 2022/09/21
 */
@Getter
public class Y9BusinessException extends Y9BaseException {

    private static final long serialVersionUID = 7938636025971118356L;

    protected int code;

    public Y9BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

}
