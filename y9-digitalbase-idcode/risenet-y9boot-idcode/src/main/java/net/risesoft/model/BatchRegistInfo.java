package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRegistInfo{

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("company_idcode")
    private String idCode;

    @JsonProperty("batch_regist_list")
    private List<CategoryRegModel> list;
}
