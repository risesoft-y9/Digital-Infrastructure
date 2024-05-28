package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CodeRecordResult extends Result {

    @JsonProperty("code_record_list")
    private List<CodeRecord> list;

    @JsonProperty("code_record_tab")
    private List<CodeRecord> tab;
}
