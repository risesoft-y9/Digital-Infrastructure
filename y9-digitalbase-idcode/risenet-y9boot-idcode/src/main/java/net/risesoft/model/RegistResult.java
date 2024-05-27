package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 单位主码（若注册激活成功），否则返回空字符串
 */
@Data
public class RegistResult extends Result {


    /**
     * 单位主码
     */
    @JsonProperty("organunit_idcode")
    private String idCode;

    @JsonProperty("organunit_result_info")
    private OrganUnitResultInfo resultInfo;
}
