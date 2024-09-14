package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author qinman
 */
@Data
public class CodeAddress extends Result {

    @JsonProperty("code_pic_address")
    private String address;
}
