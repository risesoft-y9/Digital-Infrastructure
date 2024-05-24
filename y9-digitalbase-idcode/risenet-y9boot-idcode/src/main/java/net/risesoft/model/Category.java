package net.risesoft.model;

import java.math.BigDecimal;

public class Category {

    private BigDecimal category_reg_id;

    private String category_reg_idstr;

    private BigDecimal organunit_id;

    private String organunit_idstr;

    private String bill_number;

    private String guid_number;

    private Integer codeuse_id;

    private String category_code;

    private String category_memo;

    private String model_number;

    private String model_number_code;

    private Integer code_pay_type;

    private String model_number_en;

    private String introduction;

    private String complete_code;

    private String detail_para;

    private String picture_name;

    public String getCategory_reg_idstr() {
        return category_reg_idstr;
    }

    public void setCategory_reg_idstr(String category_reg_idstr) {
        this.category_reg_idstr = category_reg_idstr;
    }

    public String getOrganunit_idstr() {
        return organunit_idstr;
    }

    public void setOrganunit_idstr(String organunit_idstr) {
        this.organunit_idstr = organunit_idstr;
    }

    public String getBill_number() {
        return bill_number;
    }

    public void setBill_number(String bill_number) {
        this.bill_number = bill_number;
    }

    public String getGuid_number() {
        return guid_number;
    }

    public void setGuid_number(String guid_number) {
        this.guid_number = guid_number;
    }

    public Integer getCodeuse_id() {
        return codeuse_id;
    }

    public void setCodeuse_id(Integer codeuse_id) {
        this.codeuse_id = codeuse_id;
    }

    public String getCategory_code() {
        return category_code;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public String getCategory_memo() {
        return category_memo;
    }

    public void setCategory_memo(String category_memo) {
        this.category_memo = category_memo;
    }

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getModel_number_code() {
        return model_number_code;
    }

    public void setModel_number_code(String model_number_code) {
        this.model_number_code = model_number_code;
    }

    public Integer getCode_pay_type() {
        return code_pay_type;
    }

    public void setCode_pay_type(Integer code_pay_type) {
        this.code_pay_type = code_pay_type;
    }

    public String getModel_number_en() {
        return model_number_en;
    }

    public void setModel_number_en(String model_number_en) {
        this.model_number_en = model_number_en;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getComplete_code() {
        return complete_code;
    }

    public void setComplete_code(String complete_code) {
        this.complete_code = complete_code;
    }

    public String getDetail_para() {
        return detail_para;
    }

    public void setDetail_para(String detail_para) {
        this.detail_para = detail_para;
    }

    public String getPicture_name() {
        return picture_name;
    }

    public void setPicture_name(String picture_name) {
        this.picture_name = picture_name;
    }

    public void setCategory_reg_id(BigDecimal category_reg_id) {
        this.category_reg_id = category_reg_id;
    }

    public BigDecimal getOrganunit_id() {
        return organunit_id;
    }

    public void setOrganunit_id(BigDecimal organunit_id) {
        this.organunit_id = organunit_id;
    }

    public BigDecimal getCategory_reg_id() {
        return category_reg_id;
    }
}
