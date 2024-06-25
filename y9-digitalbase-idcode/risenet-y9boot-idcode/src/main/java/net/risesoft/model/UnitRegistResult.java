package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UnitRegistResult extends Result {

    @JsonProperty("organunit_idcode")
    private String idCode;

    @JsonProperty("login_name")
    private String loginName;

    @JsonProperty("unit_qrcode")
    private String qrCode;

}
