package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrganUnitResult extends Result {

    @JsonProperty("organunit_list")
    private List<OrganUnit> list;
}
