package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BaseIdCodeInfo extends Result {

    @JsonProperty("base_idcode_list")
    private List<CategoryRegModel> list;

    @JsonProperty("codefileurl_info")
    private int codeFileUrlInfo;
}
