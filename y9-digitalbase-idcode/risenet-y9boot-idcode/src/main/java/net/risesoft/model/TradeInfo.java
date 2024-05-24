package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 行政信息
 */
@Data
public class TradeInfo {


    /**
     * 行业ID
     */
    @JsonProperty("trade_id")
    private Integer tradeId;

    /**
     * 行业名称
     */
    @JsonProperty("trade_name")
    private String tradeName;
    /**
     * 行业级别
     */
    @JsonProperty("trade_level")
    private Integer tradeLevel;

    /**
     * 父级行业ID
     */
    @JsonProperty("trade_id_parent")
    private Integer parentId;
}
