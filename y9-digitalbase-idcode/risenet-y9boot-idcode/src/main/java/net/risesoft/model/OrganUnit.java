package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 单位基本信息
 */
@Data
public class OrganUnit extends Result {

    /**
     * 单位ID
     */
    @JsonProperty("organunit_id")
    private String id;

    /**
     * 单位ID
     */
    @JsonProperty("organunit_idstr")
    private String idStr;
    /**
     * 平台角色类型
     */
    @JsonProperty("plateformrole_id")
    private Integer roleId;
    /**
     * 省级ID（可与接口101返回值对照）
     */
    @JsonProperty("province_id")
    private Integer provinceId;
    /**
     * 市级ID（可与接口101返回值对照）
     */
    @JsonProperty("city_id")
    private Integer cityId;
    /**
     * 区县ID（可与接口101返回值对照）
     */
    @JsonProperty("area_id")
    private Integer areaId;
    /**
     * 行业ID（可与接口103返回值对照）
     */
    @JsonProperty("trade_id")
    private Integer tradeId;
    /**
     * 单位状态
     * 100：正式用户
     * 0：待审核 -1：审核失败 -2：禁用 -100：待完善资料
     */
    @JsonProperty("status_id")
    private Integer statusId;
    /**
     * SP服务商状态
     * -4：尚未申请SP
     * -1：审核失败
     * 0：待审核
     * 1：已成为SP
     */
    @JsonProperty("issc")
    private Integer issc;
    /**
     * 单位组织名称
     */
    @JsonProperty("organunit_name")
    private String name;
    /**
     * 单位组织英文名称
     */
    @JsonProperty("organunit_name_en")
    private String nameEn;
    /**
     * 单位组织地址
     */
    @JsonProperty("organunit_address")
    private String address;
    /**
     * 单位组织英文地址
     */
    @JsonProperty("organunit_address_en")
    private String addressEn;
    /**
     * 单位组织主码
     */
    @JsonProperty("organunit_oid")
    private String idCode;
    /**
     * 单位组织性质（可与接口105返回值对照）
     */
    @JsonProperty("unittype_id")
    private Integer typeId;
    /**
     * 联系人
     */
    @JsonProperty("linkman")
    private String linkman;
    /**
     * 联系人英文名
     */
    @JsonProperty("linkman_en")
    private String linkmanEn;
    /**
     * 联系电话
     */
    @JsonProperty("linkphone")
    private String linkPhone;
    /**
     * 固定电话
     */
    @JsonProperty("fax")
    private String fax;
    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;
    /**
     * 最后审核时间
     */
    @JsonProperty("finallyexam_date")
    private String finallyExamDate;
    /**
     * 证件号码
     */
    @JsonProperty("organization_code")
    private String orgCode;
    /**
     * 证件类型
     * 1：组织/单位机构代码
     * 2：统一社会信用代码
     * 3：个体工商户营业执照号
     * 0：其他
     */
    @JsonProperty("code_pay_type")
    private Integer payType;
    /**
     * 单位办公地址
     */
    @JsonProperty("unit_workaddress")
    private String workAddress;
    /**
     * 单位办公地址英文名
     */
    @JsonProperty("unit_workaddress_en")
    private String workAddressEn;
    /**
     * 单位规模
     * 1：50人以下
     * 2：51-100人
     * 3: 101-500人
     * 4: 500人以上
     */
    @JsonProperty("unit_size_type_id")
    private Integer sizeTypeId;
    /**
     * 单位注册资金（单位：万元）
     */
    @JsonProperty("registered_capital")
    private Integer registeredCapital;
    /**
     * 单位图标（图片名称）
     */
    @JsonProperty("unit_icon")
    private String unitIcon;
    /**
     * 码图颜色
     * 1：彩色
     * 0：黑白
     */
    @JsonProperty("qrcode_color")
    private Integer qrCodeColor;
    /**
     * 是否启用logo
     * 1：启用
     * 0：不启用
     */
    @JsonProperty("qrcode_logo")
    private Integer qrCodeLogo;
    /**
     * 单位主码对应的解析地址
     */
    @JsonProperty("gotourl")
    private String gotoUrl;
    /**
     * 解析地址状态
     * 1：启用
     * 0：禁用
     */
    @JsonProperty("url_status")
    private Integer urlStatus;


    @JsonProperty("unittype_code")
    private Integer unitTypeCode;

    @JsonProperty("unittypedisplay_code")
    private Integer unitTypeDisplayCode;

    @JsonProperty("author_key")
    private Integer authorKey;

    @JsonProperty("author_code")
    private Integer authorCode;

    @JsonProperty("utcPlatform")
    private Integer utcPlatform;

    @JsonProperty("utcUrl")
    private Integer utcUrl;
}
