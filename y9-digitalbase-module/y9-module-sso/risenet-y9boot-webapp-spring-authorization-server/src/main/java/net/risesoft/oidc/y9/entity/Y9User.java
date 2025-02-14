package net.risesoft.oidc.y9.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity(name = Y9User.ENTITY_NAME)
@Table(name = "Y9_COMMON_ACCOUNT", indexes = {@Index(columnList = "LOGIN_NAME")})
@Comment("用户账号表")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
@JsonRootName("y9User")
public class Y9User implements Serializable {
    /**
     * Th JPA entity name.
     */
    public static final String ENTITY_NAME = "Y9User";

    @Serial
    private static final long serialVersionUID = 1095290600488048713L;

    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    protected String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @JsonIgnore
    @Column(name = "CREATE_TIME", updatable = false)
    protected Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("更新时间")
    @UpdateTimestamp
    @JsonIgnore
    @Column(name = "UPDATE_TIME")
    protected Date updateTime;

    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    protected String tenantId;

    @Column(name = "TENANT_SHORT_NAME", length = 255, nullable = false)
    @Comment("租户英文名称，冗余字段，为了显示用")
    protected String tenantShortName;

    @Column(name = "TENANT_NAME", length = 255, nullable = false)
    @Comment("租户名称，冗余字段，为了显示用")
    protected String tenantName;

    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员id")
    protected String personId;

    @Column(name = "LOGIN_NAME", length = 255, nullable = false)
    @Comment("登录名")
    protected String loginName;

    @Column(name = "PASSWORD", length = 255)
    @Comment("密码")
    protected String password;

    @Column(name = "CAID", length = 255)
    @Comment("ca认证号")
    protected String caid;

    @Column(name = "EMAIL", length = 255)
    @Comment("邮箱")
    protected String email;

    @Column(name = "SEX")
    @Comment("性别：1为男，0为女")
    protected Integer sex;

    @Column(name = "MOBILE", length = 255)
    @Comment("移动电话")
    protected String mobile;

    @Column(name = "VERSION")
    @Comment("乐观锁")
    protected Integer version;

    @Lob
    @Column(name = "JSON_STR")
    @Comment("jsonStr")
    protected String jsonStr;

    @Column(name = "DN", length = 2000)
    @Comment("承继关系")
    protected String dn;

    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序")
    protected String orderedPath;

    @Column(name = "GUID_PATH", length = 800)
    @Comment("由ID组成的父子关系列表，之间用逗号分隔,40*20")
    protected String guidPath;

    @Column(name = "WEIXIN_ID", length = 255)
    @Comment("用户绑定的微信id")
    protected String weixinId;

    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    protected String name;

    @Column(name = "PARENT_ID", length = 255)
    @Comment("父节点id")
    protected String parentId;

    @Column(name = "ID_NUM", length = 255)
    @Comment("证件号码")
    protected String idNum;

    @Column(name = "AVATOR", length = 255)
    @Comment("头像")
    protected String avator;

    @Column(name = "PERSON_TYPE", length = 255)
    @Comment("人员类型")
    protected String personType;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "ORIGINAL", nullable = false)
    @Comment("原始人员")
    @ColumnDefault("1")
    protected Boolean original;

    @Column(name = "ORIGINAL_ID", length = 38)
    @Comment("原始人员id")
    protected String originalId;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "GLOBAL_MANAGER", nullable = false)
    @Comment("是否全局管理员")
    @ColumnDefault("0")
    protected Boolean globalManager;

    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("是否三员管理员")
    @ColumnDefault("0")
    protected Integer managerLevel;

    @Lob
    @Column(name = "ROLES")
    @Comment("拥有的角色列表")
    protected String roles;

    @Lob
    @Column(name = "POSITIONS")
    @Comment("拥有的岗位列表")
    protected String positions;

}