package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UnitTypeInfoResult extends Result {

    @JsonProperty("unit_type_list")
    private List<UnitTypeInfo> list;
}
