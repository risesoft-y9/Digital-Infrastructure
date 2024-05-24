package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UnitTypeInfoResult extends Result {

    @JsonProperty("unit_type_list")
    private List<UnitTypeInfo> list;
}
