package net.risesoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author qinman
 */
@Data
public class CategoryResult extends Result {

    @JsonProperty("base_idcode_list")
    private List<Category> list;
}
