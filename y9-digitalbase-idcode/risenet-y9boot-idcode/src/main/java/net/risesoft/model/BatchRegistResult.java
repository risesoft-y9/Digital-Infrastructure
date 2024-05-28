package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRegistResult  extends Result {

    @JsonProperty("batch_regist_list")
    private List<CategoryRegModel> list;
}
