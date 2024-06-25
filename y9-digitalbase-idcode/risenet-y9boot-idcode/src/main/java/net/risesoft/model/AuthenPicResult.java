package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthenPicResult extends Result {

    /**
     * 认证图片地址URL
     */
    @JsonProperty("authen_pic_url")
    private String url;
}
