package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateGotoUrlResult extends Result {

    /**
     * 带logo的企业码图
     */
    @JsonProperty("unit_qrcode")
    private String qrCode;
}
