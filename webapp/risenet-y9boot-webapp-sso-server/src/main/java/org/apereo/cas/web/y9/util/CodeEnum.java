package org.apereo.cas.web.y9.util;

/**
 * 操作码
 * 
 * @author shidaobang
 * @date 2022/12/29
 */
public enum CodeEnum {
    /** 操作成功代码 */
    SUCCESS(200, "操作成功"),
    /** 服务器内部错误代码 */
    FAILURE(500, "服务器内部错误");

    private int code;
    private String msg;

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
