package net.risesoft.entity.org;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.PersonTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 人员实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSON")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员表", appliesTo = "Y9_ORG_PERSON")
@Data
@SuperBuilder
public class Y9Person extends Y9OrgBase {

    private static final long serialVersionUID = -6531424704457510017L;

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /**
     * 头像
     */
    @Column(name = "AVATOR", length = 100)
    @Comment("头像")
    private String avator;

    /**
     * CA认证码
     */
    @Column(name = "CAID", length = 255)
    @Comment("CA认证码")
    private String caid;

    /**
     * 电子邮箱
     */
    @Column(name = "EMAIL", length = 255)
    @Comment("电子邮箱")
    private String email;

    /**
     * 登录名
     */
    @Column(name = "LOGIN_NAME", length = 255, nullable = false)
    @Comment("登录名")
    private String loginName;

    /**
     * 手机号码
     */
    @Column(name = "MOBILE", length = 255, nullable = false)
    @Comment("手机号码")
    private String mobile;

    /**
     * 办公地址
     */
    @Column(name = "OFFICE_ADDRESS", length = 255)
    @Comment("办公地址")
    private String officeAddress;

    /**
     * 办公传真
     */
    @Column(name = "OFFICE_FAX", length = 255)
    @Comment("办公传真")
    private String officeFax;

    /**
     * 办公室电话
     */
    @Column(name = "OFFICE_PHONE", length = 255)
    @Comment("办公电话")
    private String officePhone;

    /**
     * 是否在编
     */
    @Column(name = "OFFICIAL")
    @Comment("是否在编")
    private Integer official;

    /**
     * 编制类型
     */
    @Column(name = "OFFICIAL_TYPE", length = 255)
    @Comment("编制类型")
    private String officialType;

    /**
     * 登录密码
     */
    @Column(name = "PASSWORD", length = 255)
    @Comment("登录密码")
    private String password;

    /** 性别 */
    @ColumnDefault("1")
    @Column(name = "SEX", nullable = false)
    @Comment("性别")
    @Convert(converter = EnumConverter.SexEnumConverter.class)
    private SexEnum sex = SexEnum.MALE;

    /**
     * 人员类型
     */
    @ColumnDefault("'deptPerson'")
    @Column(name = "PERSON_TYPE", length = 255, nullable = false)
    @Comment("人员类型")
    private String personType = PersonTypeEnum.DEPARTMENT.getValue();

    /**
     * 人员绑定微信的唯一标识
     */
    @Column(name = "WEIXIN_ID", length = 255)
    @Comment("人员绑定微信的唯一标识")
    private String weixinId;

    /**
     * 排序序列号
     */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序序列号")
    private String orderedPath;

    /**
     * 0:添加的人员，1：新增的人员
     */
    @Type(type = "numeric_boolean")
    @ColumnDefault("1")
    @Column(name = "ORIGINAL", length = 10, nullable = false)
    @Comment("0:添加的人员，1：新增的人员")
    private Boolean original = true;

    /**
     * 原始人员id
     */
    @Column(name = "ORIGINAL_ID", length = 255)
    @Comment("原始人员id")
    private String originalId;

    public Y9Person() {
        super.setOrgType(OrgTypeEnum.PERSON);
    }

}