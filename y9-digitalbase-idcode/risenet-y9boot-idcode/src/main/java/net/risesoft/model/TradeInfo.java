package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 行政信息
 * @author qinman
 */
@Data
public class TradeInfo {

    /**
     * 行业ID
     */
    @JsonProperty("trade_id")
    private Integer id;

    /**
     * 行业名称
     */
    @JsonProperty("trade_name")
    private String name;
    /**
     * 行业级别
     */
    @JsonProperty("trade_level")
    private Integer level;

    /**
     * 父级行业ID
     */
    @JsonProperty("trade_id_parent")
    private Integer parentId;
}
