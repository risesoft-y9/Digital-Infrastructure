package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 单位性质
 */
@Data
public class UnitTypeInfo {


    /**
     * 单位性质名称
     */
    @JsonProperty("unit_type_name")
    private String name;

    /**
     * 单位性质编码
     */
    private Integer code;
}
