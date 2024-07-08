package net.risesoft.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author qinman
 */
@Data
public class CodeRecord {

    /**
     * 上传码批次ID
     */
    @JsonProperty("uploadcode_id")
    private BigDecimal uploadCodeId;

    /**
     * 单位ID
     */
    @JsonProperty("organunit_id")
    private BigDecimal organUnitId;
    /**
     * 解析地址审核 ID（与型号码ID相对应）
     */
    @JsonProperty("ioidcategory_id")
    private BigDecimal ioIdCategoryId;

    /**
     * 码类型 1：产品码 2：非产品码
     */
    @JsonProperty("code_type")
    private Integer codeType;
    /**
     * 码段ID，固定值：1
     */
    @JsonProperty("codesegment_id")
    private Integer codeSegmentId;
    /**
     * 上传码数量
     */
    @JsonProperty("code_num")
    private Integer codeNum;
    /**
     * 上传类型 1：接口5011方式 2：接口5012/5013方式 3：特定业务
     */
    @JsonProperty("upload_type")
    private Integer uploadType;
    /**
     * 码文件名称
     */
    @JsonProperty("code_file_name")
    private String codeFileName;
    /**
     * 是否可以转码，固定值：1
     */
    @JsonProperty("iftrans")
    private Integer ifTrans;
    /**
     * 是否转换标记 1：已转换 0：未转换
     */
    @JsonProperty("trans_flag")
    private Integer transFlag;
    /**
     * 转码后批量下载LB码图文件
     */
    @JsonProperty("transdown_lb_picname")
    private String transDownLbPicName;
    /**
     * 转码后文件
     */
    @JsonProperty("trans_file_name")
    private Integer transFileName;
    /**
     * 起始序号
     */
    @JsonProperty("start_number")
    private Integer startNumber;
    /**
     * 终止序号
     */
    @JsonProperty("end_number")
    private Integer endNumber;
    /**
     * 转码后批量下载QR码图文件
     */
    @JsonProperty("transdown_qr_picname")
    private String transDownQrPicName;
    /**
     * 上传码时间
     */
    @JsonProperty("create_code")
    private Long createCode;
}
