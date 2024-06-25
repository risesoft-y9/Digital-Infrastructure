package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BatchRegistResult extends Result {

    @JsonProperty("batch_regist_list")
    private List<CategoryRegModel> list;
}
