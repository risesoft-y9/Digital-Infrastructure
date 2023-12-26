package org.apereo.cas.web.y9.util;

import java.io.Serializable;

import org.apereo.cas.web.y9.util.exception.ErrorCode;
import org.apereo.cas.web.y9.util.exception.GlobalErrorCodeEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9Result<T> implements Serializable {

    private static final long serialVersionUID = -852918533474167060L;
    /**
     * 操作是否成功
     */
    private boolean success;
    /**
     * 错误代码
     */
    private int code;
    /**
     * 操作描述
     */
    private String msg;
    /**
     * 操作成功返回的数据
     */
    private T data;

    /**
     * 对于大多数不需要关注 code 的情况下，推荐直接静态方法创建对象，而不是通过新建对象然后设值 直接静态方法创建对象可以统一 code ，方便 code 的维护
     */
    private Y9Result() {}

    public static <T> Y9Result<T> failure(ErrorCode errorCode) {
        return failure(errorCode, errorCode.getDescription());
    }

    public static <T> Y9Result<T> failure(ErrorCode errorCode, String msg) {
        return new Y9Result<>(Boolean.FALSE, errorCode.getCode(), msg, null);
    }

    public static <T> Y9Result<T> failure(int code, String msg) {
        return new Y9Result<>(Boolean.FALSE, code, msg, null);
    }

    public static <T> Y9Result<T> failure(String msg) {
        return failure(GlobalErrorCodeEnum.FAILURE, msg);
    }

    public static <T> Y9Result<T> success() {
        return success(null);
    }

    public static <T> Y9Result<T> success(T data) {
        return success(data, GlobalErrorCodeEnum.SUCCESS.getDescription());
    }

    public static <T> Y9Result<T> success(T data, String msg) {
        return new Y9Result<>(Boolean.TRUE, GlobalErrorCodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> Y9Result<T> successMsg(String msg) {
        return success(null, msg);
    }
}
