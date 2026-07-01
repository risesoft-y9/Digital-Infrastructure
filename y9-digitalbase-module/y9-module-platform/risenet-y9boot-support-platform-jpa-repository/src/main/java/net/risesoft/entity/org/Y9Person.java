package net.risesoft.entity.org;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.PersonTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Person;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/**
 * 人员实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSON",
    uniqueConstraints = {@UniqueConstraint(name = Y9Person.UK_CUSTOM_ID, columnNames = {"CUSTOM_ID"})})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员表", appliesTo = "Y9_ORG_PERSON")
@Data
@NoArgsConstructor
public class Y9Person extends Y9OrgBase {

    private static final long serialVersionUID = -6531424704457510017L;

    public static final String UK_CUSTOM_ID = "UK_PERSON_CUSTOM_ID";

    {
        super.setOrgType(OrgTypeEnum.PERSON);
    }

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
    private String caId;

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
    private Boolean original = Boolean.TRUE;

    /**
     * 原始人员id
     */
    @Column(name = "ORIGINAL_ID", length = 38)
    @Comment("原始人员id")
    private String originalId;

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getCustomId() {
        if (StringUtils.equals(this.customId, this.id)) {
            // 对上层隐藏“非必填”的 customId 字段唯一索引的实现细节
            return null;
        }
        return this.customId;
    }

    public Y9Person(
        Person person,
        Y9OrgBase parent,
        List<Y9OrgBase> ancestorList,
        Integer nextSubTabIndex,
        String defaultPassword) {
        Y9BeanUtil.copyProperties(person, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId();
        }
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        if (StringUtils.isBlank(this.email)) {
            this.email = null;
        }
        if (StringUtils.isEmpty(this.password)) {
            this.password = Y9MessageDigestUtil.bcrypt(defaultPassword);
        } else {
            if (!Y9MessageDigestUtil.BCRYPT_PATTERN.matcher(this.password).matches()) {
                // 避免重复加密（导入的情况直接使用原密文）
                this.password = Y9MessageDigestUtil.bcrypt(this.password);
            }
        }
        this.official = 1;
        this.parentId = parent.getId();
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextSubTabIndex;
        }
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    public Boolean changeDisabled() {
        boolean targetStatus = !this.disabled;
        this.disabled = targetStatus;
        return targetStatus;
    }

    public void update(Person person, Y9OrgBase parent, List<Y9OrgBase> ancestorList) {
        Y9BeanUtil.copyProperties(person, this);

        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);

        if (StringUtils.isBlank(this.email)) {
            this.email = null;
        }
    }

    public void changeTabIndex(int tabIndex, List<Y9OrgBase> ancestorList) {
        this.tabIndex = tabIndex;
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (StringUtils.isNotBlank(oldPassword)) {
            // 兼容旧接口（无 oldPassword）或重设密码
            Y9AssertUtil.isTrue(Y9MessageDigestUtil.bcryptMatch(oldPassword, this.password),
                OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT);
        }
        this.password = Y9MessageDigestUtil.bcrypt(newPassword);
    }

    public void changeParent(Y9OrgBase parent, Integer nextSubTabIndex, List<Y9OrgBase> ancestorList) {
        this.parentId = parent.getId();
        this.tabIndex = nextSubTabIndex;
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);
    }

    public void changeAvatar(String avatorUrl) {
        this.avator = avatorUrl;
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public void changeWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }
}