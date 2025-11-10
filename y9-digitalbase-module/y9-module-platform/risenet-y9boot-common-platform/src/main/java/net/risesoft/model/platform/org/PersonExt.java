package net.risesoft.model.platform.org;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.platform.org.MaritalStatusEnum;
import net.risesoft.model.BaseModel;
import net.risesoft.y9.validation.IdNumber;

/**
 * 人员信息扩展
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class PersonExt extends BaseModel {
    private static final long serialVersionUID = 1274186459521666925L;

    /**
     * 人员ID
     */
    private String personId;

    /**
     * 名称
     */
    private String name;

    /**
     * 照片
     */
    private byte[] photo;

    /**
     * 签名
     */
    private byte[] sign;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 居住城市
     */
    private String city;

    /**
     * 居住国家
     */
    private String country;

    /**
     * 学历
     */
    private String education;

    /**
     * 家庭地址
     */
    private String homeAddress;

    /**
     * 家庭电话
     */
    private String homePhone;

    /**
     * 证件号码
     */
    @IdNumber
    private String idNum;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 婚姻状况
     */
    private MaritalStatusEnum maritalStatus;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 专业
     */
    private String professional;

    /**
     * 人员籍贯
     */
    private String province;

    /**
     * 入职时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date workTime;

}