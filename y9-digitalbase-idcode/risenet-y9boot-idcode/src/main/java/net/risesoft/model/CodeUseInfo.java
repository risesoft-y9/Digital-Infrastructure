package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 行政信息
 * 
 * @author qinman
 */
@Data
public class CodeUseInfo {

    /**
     * 用途ID
     */
    @JsonProperty("codeuse_id")
    private Integer id;

    /**
     * 用途名称
     */
    @JsonProperty("codeuse_name")
    private String name;
    /**
     * 用途代码
     */
    @JsonProperty("codeuse_code")
    private String code;

    /**
     * 人事物类型
     */
    @JsonProperty("type_id")
    private Integer typeId;
}
