package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrganUnitResult extends Result {

    @JsonProperty("organunit_list")
    private List<OrganUnit> list;
}
