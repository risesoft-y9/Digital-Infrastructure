package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseIdCodeInfo extends Result {

    @JsonProperty("base_idcode_list")
    private List<CategoryRegModel> list;

    @JsonProperty("codefileurl_info")
    private int codeFileUrlInfo;
}
