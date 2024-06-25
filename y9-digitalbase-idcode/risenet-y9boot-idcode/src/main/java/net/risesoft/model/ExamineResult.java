package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExamineResult extends Result {

    @JsonProperty("examine_content")
    private String content;

    @JsonProperty("examine_status")
    private Integer status;

    @JsonProperty("examine_remarks")
    private String remarks;
}
