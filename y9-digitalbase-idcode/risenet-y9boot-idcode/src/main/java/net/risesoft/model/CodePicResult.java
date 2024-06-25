package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodePicResult extends Result {

    @JsonProperty("code_pic_address")
    private String code_pic_address;

    public String getCode_pic_address() {
        return code_pic_address;
    }

    public void setCode_pic_address(String code_pic_address) {
        this.code_pic_address = code_pic_address;
    }
}
