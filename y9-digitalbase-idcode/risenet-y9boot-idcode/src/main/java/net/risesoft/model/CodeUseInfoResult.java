package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CodeUseInfoResult extends Result {

    @JsonProperty("codeuse_list")
    private List<CodeUseInfo> list;
}
