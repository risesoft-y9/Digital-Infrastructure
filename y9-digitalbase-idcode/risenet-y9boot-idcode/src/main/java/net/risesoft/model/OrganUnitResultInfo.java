package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 行政信息
 */
@Data
public class OrganUnitResultInfo {

    /**
     * 企业名称
     */
    @JsonProperty("organunit_name")
    private String name;

    /**
     * 联系人姓名
     */
    @JsonProperty("linkman")
    private String linkman;
    /**
     * 办公电话
     */
    @JsonProperty("fax")
    private String fax;

    /**
     * 手机号
     */
    @JsonProperty("linkphone")
    private String linkPhone;
}
