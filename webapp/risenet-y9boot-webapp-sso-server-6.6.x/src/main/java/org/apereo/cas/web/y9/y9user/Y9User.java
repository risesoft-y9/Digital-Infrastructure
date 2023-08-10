package org.apereo.cas.web.y9.y9user;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Y9_COMMON_ACCOUNT", indexes = {@Index(columnList = "LOGIN_NAME")})
@NoArgsConstructor
@Data
public class Y9User implements Serializable {
    private static final long serialVersionUID = -5304438794959962884L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 255, nullable = false)
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 255)
    private String tenantId;

    /** 租户英文名称，登录验证使用 */
    @Column(name = "TENANT_SHORT_NAME", length = 255)
    private String tenantShortName;

    /** 租户名称，冗余字段，为了显示用 */
    @Column(name = "TENANT_NAME", length = 255)
    private String tenantName;

    /** 人员id */
    @Column(name = "PERSON_ID", length = 255)
    private String personId;

    /** 登录名 */
    @Column(name = "LOGIN_NAME", length = 255)
    private String loginName;

    /** 密码 */
    @Column(name = "PASSWORD", length = 255)
    private String password;

    /** ca认证号 */
    @Column(name = "CAID", length = 255)
    private String caid;

    /** 邮箱 */
    @Column(name = "EMAIL", length = 255)
    private String email;

    /** 性别：1为男，0为女 */
    @Column(name = "SEX")
    private int sex;

    /** 移动电话 */
    @Column(name = "MOBILE", length = 255)
    private String mobile;

    // @Version
    /** 乐观锁 */
    @Column(name = "VERSION")
    private int version;

    /** jsonStr */
    @Lob
    @Column(name = "JSON_STR")
    private String jsonStr;

    /** 承继关系 */
    @Column(name = "DN", length = 2000)
    protected String dn;

    /** 排序 */
    @Column(name = "ORDERED_PATH", length = 500)
    private String orderedPath;

    /** 由ID组成的父子关系列表，之间用逗号分隔,40*20 */
    @Column(name = "GUID_PATH", length = 800)
    protected String guidPath;

    /** 用户绑定的微信id */
    @Column(name = "WEIXIN_ID", length = 255)
    private String weixinId;

    /** 名称 */
    @Column(name = "NAME", length = 255)
    private String name;

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 255)
    private String parentId;

    /** 证件号码 */
    @Column(name = "ID_NUM", length = 255)
    private String idNum;

    /** 头像 */
    @Column(name = "AVATOR", length = 255)
    private String avator;

    /** 人员类型 */
    @Column(name = "PERSON_TYPE", length = 255)
    private String personType;

    /** 原始人员 */
    @Type(type = "numeric_boolean")
    @Column(name = "ORIGINAL", nullable = false)
    @ColumnDefault("1")
    private Boolean original = Boolean.TRUE;

    /** 原始人员id */
    @Column(name = "ORIGINAL_ID", length = 255)
    private String originalId;

    /** 是否全局管理员 */
    @Type(type = "numeric_boolean")
    @Column(name = "GLOBAL_MANAGER", nullable = false)
    @ColumnDefault("0")
    private Boolean globalManager = false;

    /** 三员类型 */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @ColumnDefault("0")
    private Integer managerLevel = 0;

    /** 拥有的角色列表 */
    @Lob
    @Column(name = "ROLES")
    private String roles;

    /** 拥有的岗位列表 */
    @Lob
    @Column(name = "POSITIONS")
    @Comment("拥有的岗位列表")
    private String positions;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false)
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("更新时间")
    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    protected Date updateTime;

}