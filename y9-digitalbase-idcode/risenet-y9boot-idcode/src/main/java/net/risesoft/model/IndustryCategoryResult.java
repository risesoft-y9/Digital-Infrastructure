package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IndustryCategoryResult extends Result {

    @JsonProperty("industrycategory_list")
    private List<IndustryCategory> list;
}
