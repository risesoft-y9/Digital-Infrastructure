package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 短信验证码
 * @author qinman
 */
@Data
public class SmsVerifyCode extends Result {

    /**
     * 验证码
     */
    @JsonProperty("verify_code")
    private String verifyCode;
}
