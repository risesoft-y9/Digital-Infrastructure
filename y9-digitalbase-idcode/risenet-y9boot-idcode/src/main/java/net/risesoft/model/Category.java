package net.risesoft.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Category {

    @JsonProperty("category_reg_id")
    private BigDecimal category_reg_id;

    @JsonProperty("category_reg_idstr")
    private String category_reg_idstr;

    @JsonProperty("organunit_id")
    private BigDecimal organunit_id;

    @JsonProperty("organunit_idstr")
    private String organunit_idstr;

    @JsonProperty("bill_number")
    private String bill_number;

    @JsonProperty("guid_number")
    private String guid_number;

    @JsonProperty("codeuse_id")
    private Integer codeuse_id;

    @JsonProperty("category_code")
    private String category_code;

    @JsonProperty("category_memo")
    private String category_memo;

    @JsonProperty("model_number")
    private String model_number;

    @JsonProperty("model_number_code")
    private String model_number_code;

    @JsonProperty("code_pay_type")
    private Integer code_pay_type;

    @JsonProperty("model_number_en")
    private String model_number_en;

    @JsonProperty("introduction")
    private String introduction;

    @JsonProperty("complete_code")
    private String complete_code;

    @JsonProperty("detail_para")
    private String detail_para;

    @JsonProperty("picture_name")
    private String picture_name;
}
