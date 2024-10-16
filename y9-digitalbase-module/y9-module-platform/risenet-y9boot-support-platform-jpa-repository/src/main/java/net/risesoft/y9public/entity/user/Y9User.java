package net.risesoft.y9public.entity.user;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 人员信息表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_ACCOUNT", indexes = {@Index(columnList = "LOGIN_NAME")})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员信息表", appliesTo = "Y9_COMMON_ACCOUNT")
@NoArgsConstructor
@Data
public class Y9User extends BaseEntity {

    private static final long serialVersionUID = 1095290600488048713L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    private String tenantId;

    /** 租户英文名称，冗余字段，为了显示用 */
    @Column(name = "TENANT_SHORT_NAME", length = 255, nullable = false)
    @Comment("租户英文名称，冗余字段，为了显示用")
    private String tenantShortName;

    /** 租户名称，冗余字段，为了显示用 */
    @Column(name = "TENANT_NAME", length = 255, nullable = false)
    @Comment("租户名称，冗余字段，为了显示用")
    private String tenantName;

    /** 人员id */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员id")
    private String personId;

    /** 登录名 */
    @Column(name = "LOGIN_NAME", length = 255, nullable = false)
    @Comment("登录名")
    private String loginName;

    /** 密码 */
    @Column(name = "PASSWORD", length = 255)
    @Comment("密码")
    private String password;

    /** ca认证号 */
    @Column(name = "CAID", length = 255)
    @Comment("ca认证号")
    private String caid;

    /** 邮箱 */
    @Column(name = "EMAIL", length = 255)
    @Comment("邮箱")
    private String email;

    /** 性别 */
    @Column(name = "SEX")
    @Comment("性别：1为男，0为女")
    @Convert(converter = EnumConverter.SexEnumConverter.class)
    private SexEnum sex;

    /** 移动电话 */
    @Column(name = "MOBILE", length = 255)
    @Comment("移动电话")
    private String mobile;

    /** 乐观锁 */
    @Column(name = "VERSION")
    @Comment("乐观锁")
    private int version;

    /** jsonStr */
    @Lob
    @Column(name = "JSON_STR")
    @Comment("jsonStr")
    private String jsonStr;

    /** 承继关系 */
    @Column(name = "DN", length = 2000)
    @Comment("承继关系")
    private String dn;

    /** 排序 */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序")
    private String orderedPath;

    /** 由ID组成的父子关系列表，之间用逗号分隔,40*20 */
    @Column(name = "GUID_PATH", length = 800)
    @Comment("由ID组成的父子关系列表，之间用逗号分隔,40*20")
    private String guidPath;

    /** 用户绑定的微信id */
    @Column(name = "WEIXIN_ID", length = 255)
    @Comment("用户绑定的微信id")
    private String weixinId;

    /** 名称 */
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    private String name;

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 255)
    @Comment("父节点id")
    private String parentId;

    /** 证件号码 */
    @Column(name = "ID_NUM", length = 255)
    @Comment("证件号码")
    private String idNum;

    /** 头像 */
    @Column(name = "AVATOR", length = 255)
    @Comment("头像")
    private String avator;

    /** 人员类型 */
    @Column(name = "PERSON_TYPE", length = 255)
    @Comment("人员类型")
    private String personType;

    /** 原始人员 */
    @Type(type = "numeric_boolean")
    @Column(name = "ORIGINAL", nullable = false)
    @Comment("原始人员")
    @ColumnDefault("1")
    private Boolean original;

    /** 原始人员id */
    @Column(name = "ORIGINAL_ID", length = 38)
    @Comment("原始人员id")
    private String originalId;

    /** 是否全局管理员 */
    @Type(type = "numeric_boolean")
    @Column(name = "GLOBAL_MANAGER", nullable = false)
    @Comment("是否全局管理员")
    @ColumnDefault("0")
    private Boolean globalManager = false;

    /** 三员类型 **/
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("是否三员管理员")
    @ColumnDefault("0")
    @Convert(converter = EnumConverter.ManagerLevelEnumConverter.class)
    private ManagerLevelEnum managerLevel = ManagerLevelEnum.GENERAL_USER;

    /** 拥有的角色列表 */
    @Lob
    @Column(name = "ROLES")
    @Comment("拥有的角色列表")
    private String roles;

    /** 拥有的岗位列表 */
    @Lob
    @Column(name = "POSITIONS")
    @Comment("拥有的岗位列表")
    private String positions;

}