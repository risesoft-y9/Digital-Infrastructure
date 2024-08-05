package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 单位状态信息
 * 
 * @author qinman
 */
@Data
public class OrganUnitStatusInfo extends Result {

    /**
     * 认证状态
     */
    @JsonProperty("is_authen")
    private Integer authen;
    /**
     * 审核状态
     */
    @JsonProperty("is_audit")
    private Integer audit;
    /**
     * 剩余解析天数
     */
    @JsonProperty("surplus_time")
    private String surplusTime;
}
