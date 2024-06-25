package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AreaInfoResult extends Result {

    @JsonProperty("address_list")
    private List<AreaInfo> list;
}
