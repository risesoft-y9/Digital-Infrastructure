package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 行政信息
 */
@Data
public class AreaInfo {

    /**
     * 区划ID
     */
    @JsonProperty("address_id")
    private Integer id;

    /**
     * 区划名称
     */
    @JsonProperty("address_name")
    private String name;
    /**
     * 区划级别
     */
    @JsonProperty("address_level")
    private Integer level;

    /**
     * 父级区划ID
     */
    @JsonProperty("address_id_parent")
    private Integer parentId;

    /**
     * 区划编码
     */
    @JsonProperty("address_code")
    private String code;
}
