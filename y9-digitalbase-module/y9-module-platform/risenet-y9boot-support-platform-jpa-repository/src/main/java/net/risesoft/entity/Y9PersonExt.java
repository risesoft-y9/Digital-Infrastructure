package net.risesoft.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.MaritalStatusEnum;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.persistence.FieldBase64Converter;
import net.risesoft.y9.validation.IdNumber;

/**
 * 人员实体扩展信息表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSON_EXT")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员信息扩展表", appliesTo = "Y9_ORG_PERSON_EXT")
@NoArgsConstructor
@Data
public class Y9PersonExt extends BaseEntity {

    private static final long serialVersionUID = 1501707924703496602L;

    /** 人员ID */
    @Id
    @Column(name = "PERSON_ID", length = 38, nullable = false, unique = true)
    @Comment("人员ID")
    private String personId;

    /** 登录名，拥于查找 */
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("登录名，拥于查找")
    private String name;

    /** 照片 */
    @Lob
    @Column(name = "PHOTO")
    @Comment("照片")
    private byte[] photo;

    /** 签名 */
    @Lob
    @Column(name = "SIGN")
    @Comment("签名")
    private byte[] sign;

    /** 出生年月日 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDAY")
    @Comment("出生年月日")
    private Date birthday;

    /** 居住城市 */
    @Column(name = "CITY", length = 255)
    @Comment("居住城市")
    private String city;

    /** 居住国家 */
    @Column(name = "COUNTRY", length = 255)
    @Comment("居住国家")
    private String country;

    /** 学历 */
    @Column(name = "EDUCATION", length = 255)
    @Comment("学历")
    private String education;

    /** 家庭地址 */
    @Column(name = "HOME_ADDRESS", length = 255)
    @Comment("家庭地址")
    private String homeAddress;

    /** 家庭电话 */
    @Column(name = "HOME_PHONE", length = 255)
    @Comment("家庭电话")
    private String homePhone;

    /** 证件号码 */
    @IdNumber
    @Convert(converter = FieldBase64Converter.class)
    @Column(name = "ID_NUM", length = 255)
    @Comment("证件号码")
    private String idNum;

    /**
     * 证件类型 在数据字典中
     */
    @Column(name = "ID_TYPE", length = 255)
    @Comment("证件类型")
    private String idType;

    /** 婚姻状况 */
    @Column(name = "MARITAL_STATUS", nullable = false)
    @Comment("婚姻状况")
    @ColumnDefault("0")
    @Convert(converter = EnumConverter.MaritalStatusEnumConverter.class)
    private MaritalStatusEnum maritalStatus = MaritalStatusEnum.SECRET;

    /** 政治面貌 */
    @Column(name = "POLITICAL_STATUS", length = 255)
    @Comment("政治面貌")
    private String politicalStatus;

    /** 专业 */
    @Column(name = "PROFESSIONAL", length = 255)
    @Comment("专业")
    private String professional;

    /** 人员籍贯 */
    @Column(name = "PROVINCE", length = 255)
    @Comment("人员籍贯")
    private String province;

    /** 入职时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Column(name = "WORK_TIME")
    @Comment("入职时间")
    private Date workTime;

}