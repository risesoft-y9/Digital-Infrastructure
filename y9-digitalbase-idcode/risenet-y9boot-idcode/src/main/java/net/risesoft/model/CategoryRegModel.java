package net.risesoft.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CategoryRegModel {

    /**
     * 主键ID
     */
    @JsonProperty("category_reg_id")
    private BigDecimal id;

    /**
     * 主键ID(字符串格式)
     */
    @JsonProperty("category_reg_idstr")
    private String idstr;

    /**
     * 品类ID（必填），可通过接口202/203获取
     */
    @JsonProperty("industrycategory_id")
    private Integer industryCategoryId;

    /**
     * 码用途编码（必填），可通过接口201获取
     */
    @JsonProperty("codeuse_id")
    private Integer codeUseId;

    /**
     * 品类编码（必填），可通过接口202/203获取
     */
    @JsonProperty("category_code")
    private String categoryCode;

    /**
     * 品类描述
     */
    @JsonProperty("category_memo")
    private String categoryMemo;

    /**
     * 申请类型（必填） 2：注册 5：备案
     */
    @JsonProperty("code_pay_type")
    private Integer codePayType;

    /**
     * 解析地址（必填）
     */
    @JsonProperty("gotourl")
    private String gotoUrl;

    /**
     * 示例地址
     */
    @JsonProperty("sample_url")
    private String sampleUrl;

    /**
     * 型号名称（必填）
     */
    @JsonProperty("model_number")
    private String modelNumber;

    /**
     * 型号编码（必填）
     */
    @JsonProperty("model_number_code")
    private String modelNumberCode;

    /**
     * 单位ID
     */
    @JsonProperty("organunit_id")
    private BigDecimal organUnitId;

    /**
     * 单位ID(字符串格式)
     */
    @JsonProperty("organunit_idstr")
    private String organUnitIdStr;

    /**
     * 批次号
     */
    @JsonProperty("bill_number")
    private String billNumber;

    /**
     * 流水号
     */
    @JsonProperty("guid_number")
    private String guidNumber;

    /**
     * 型号英文名称
     */
    @JsonProperty("model_number_en")
    private String modelNumberEn;

    /**
     * 描述
     */
    @JsonProperty("introduction")
    private String introduction;

    /**
     * 完整码号
     */
    @JsonProperty("complete_code")
    private String completeCode;

    /**
     * 详细参数
     */
    @JsonProperty("detail_para")
    private String detail_para;

    /**
     * 产品图片
     */
    @JsonProperty("picture_name")
    private String pictureName;

    /**
     * 申请码类型(细化类型) 1：通用标准二维码申请 2：商品二维码申请 3：农药二维码申请 -1：备案
     */
    @JsonProperty("regist_type")
    private Integer registType;

}
