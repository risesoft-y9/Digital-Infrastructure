package y9.support;

import net.risesoft.y9.exception.Y9BusinessException;

/**
 * 调用接口异常 <br>
 * 调用方可根据自己的情况捕获异常进行处理
 * 
 * @author shidaobang
 * @date 2023/08/07
 * @since 9.6.3
 */
public class Y9ApiException extends Y9BusinessException {

    public Y9ApiException(int code, String message) {
        super(code, message);
    }
}
