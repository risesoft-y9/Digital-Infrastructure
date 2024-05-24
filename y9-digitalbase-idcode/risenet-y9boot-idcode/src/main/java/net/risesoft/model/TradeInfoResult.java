package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TradeInfoResult extends Result {

    @JsonProperty("trade_list")
    private List<TradeInfo> list;
}
