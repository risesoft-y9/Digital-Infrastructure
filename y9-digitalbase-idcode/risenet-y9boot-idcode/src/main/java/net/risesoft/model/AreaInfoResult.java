package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AreaInfoResult extends Result {

    @JsonProperty("address_list")
    private List<AreaInfo> list;
}
