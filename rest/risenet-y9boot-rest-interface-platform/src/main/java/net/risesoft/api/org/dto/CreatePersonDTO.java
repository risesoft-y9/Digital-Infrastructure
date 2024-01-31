package net.risesoft.api.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.platform.MaritalStatusEnum;
import net.risesoft.enums.platform.PersonTypeEnum;
import net.risesoft.enums.platform.SexEnum;

/**
 * @author shidaobang
 * @date 2023/11/06
 * @since 9.6.3
 */
@Getter
@Setter
public class CreatePersonDTO extends CreateOrgUnitBaseDTO {

    private static final long serialVersionUID = -6612983613465433450L;

    /** 父节点id */
    @NotBlank
    private String parentId;

    /**
     * 头像
     */
    private String avator;

    /**
     * CA认证码
     */
    private String caid;

    /**
     * 电子邮箱
     */
    @Email
    private String email;

    /**
     * 登录名
     */
    @NotBlank
    private String loginName;

    /**
     * 手机号码
     */
    @NotBlank
    private String mobile;

    /**
     * 办公地址
     */
    private String officeAddress;

    /**
     * 办公传真
     */
    private String officeFax;

    /**
     * 办公室电话
     */
    private String officePhone;

    /**
     * 是否在编
     */
    private Integer official;

    /**
     * 编制类型
     */
    private String officialType;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 性别
     * <p>
     * {@link SexEnum}
     */
    private SexEnum sex = SexEnum.MALE;

    /**
     * 人员类型
     */
    private String personType = PersonTypeEnum.DEPARTMENT.getValue();

    /**
     * 人员绑定微信的唯一标识
     */
    private String weixinId;

    /** 照片 */
    private byte[] photo;

    /** 签名 */
    private byte[] sign;

    /** 出生年月日 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /** 居住城市 */
    private String city;

    /** 居住国家 */
    private String country;

    /** 学历 */
    private String education;

    /** 家庭地址 */
    private String homeAddress;

    /** 家庭电话 */
    private String homePhone;

    /** 证件号码 */
    private String idNum;

    /** 证件类型 */
    private String idType;

    /**
     * 婚姻状况
     *
     * {@link MaritalStatusEnum}
     */
    private MaritalStatusEnum maritalStatus = MaritalStatusEnum.SECRET;

    /** 政治面貌 */
    private String politicalStatus;

    /** 专业 */
    private String professional;

    /** 人员籍贯 */
    private String province;

    /** 入职时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workTime;
}
