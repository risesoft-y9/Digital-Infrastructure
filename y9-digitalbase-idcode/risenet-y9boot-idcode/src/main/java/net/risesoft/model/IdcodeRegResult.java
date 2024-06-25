package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * idcode406接口对应的返回对象
 *
 */
@Data
public class IdcodeRegResult extends Result {

    @JsonProperty("organunit_idcode")
    private String organUnitIdCode;

    @JsonProperty("category_reg_id")
    private String categoryRegId;
}
