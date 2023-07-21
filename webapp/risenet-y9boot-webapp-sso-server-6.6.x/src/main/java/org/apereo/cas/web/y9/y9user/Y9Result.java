package org.apereo.cas.web.y9.y9user;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Y9Result<T> implements Serializable {

    private static final long serialVersionUID = -852918533474167060L;

    public static Y9Result<Object> failure(CodeEnum codeEnum, String msg) {
        Y9Result<Object> y9Result = new Y9Result<>();
        y9Result.setCode(codeEnum.getCode());
        y9Result.setSuccess(Boolean.FALSE);
        y9Result.setMsg(msg);
        return y9Result;
    }

    public static Y9Result<Object> failure(String msg) {
        return failure(CodeEnum.FAILURE, msg);
    }

    public static <T> Y9Result<T> success() {
        return success(null);
    }

    public static <T> Y9Result<T> success(T data) {
        return success(data, CodeEnum.SUCCESS.getMsg());
    }

    public static <T> Y9Result<T> success(T data, String msg) {
        Y9Result<T> y9Result = new Y9Result<>();
        y9Result.setCode(CodeEnum.SUCCESS.getCode());
        y9Result.setSuccess(Boolean.TRUE);
        y9Result.setData(data);
        y9Result.setMsg(msg);
        return y9Result;
    }

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 错误代码
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 返回的数据
     */
    private T data;
}
