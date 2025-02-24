package org.apereo.cas.services;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity(name = Y9User.ENTITY_NAME)
@Table(name = "Y9_COMMON_ACCOUNT", indexes = { @Index(columnList = "LOGIN_NAME") })
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
public class Y9User implements Serializable {
	/**
	 * Th JPA entity name.
	 */
	public static final String ENTITY_NAME = "Y9User";

	private static final long serialVersionUID = 1095290600488048713L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment("主键")
	private String id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Comment("创建时间")
	@CreationTimestamp
	@Column(name = "CREATE_TIME", updatable = false)
	protected Instant createTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Comment("更新时间")
	@UpdateTimestamp
	@Column(name = "UPDATE_TIME")
	protected Instant updateTime;

	@Column(name = "TENANT_ID", length = 38, nullable = false)
	@Comment("租户id")
	private String tenantId;

	@Column(name = "TENANT_SHORT_NAME", length = 255, nullable = false)
	@Comment("租户英文名称，冗余字段，为了显示用")
	private String tenantShortName;

	@Column(name = "TENANT_NAME", length = 255, nullable = false)
	@Comment("租户名称，冗余字段，为了显示用")
	private String tenantName;

	@Column(name = "PERSON_ID", length = 38, nullable = false)
	@Comment("人员id")
	private String personId;

	@Column(name = "LOGIN_NAME", length = 255, nullable = false)
	@Comment("登录名")
	private String loginName;

	@Column(name = "PASSWORD", length = 255)
	@Comment("密码")
	private String password;

	@Column(name = "CAID", length = 255)
	@Comment("ca认证号")
	private String caid;

	@Column(name = "EMAIL", length = 255)
	@Comment("邮箱")
	private String email;

	@Column(name = "SEX")
	@Comment("性别：1为男，0为女")
	private Integer sex;

	@Column(name = "MOBILE", length = 255)
	@Comment("移动电话")
	private String mobile;

	@Column(name = "VERSION")
	@Comment("乐观锁")
	private Integer version;

	@Lob
	@Column(name = "JSON_STR")
	@Comment("jsonStr")
	private String jsonStr;

	@Column(name = "DN", length = 2000)
	@Comment("承继关系")
	private String dn;

	@Column(name = "ORDERED_PATH", length = 500)
	@Comment("排序")
	private String orderedPath;

	@Column(name = "GUID_PATH", length = 800)
	@Comment("由ID组成的父子关系列表，之间用逗号分隔,40*20")
	private String guidPath;

	@Column(name = "WEIXIN_ID", length = 255)
	@Comment("用户绑定的微信id")
	private String weixinId;

	@Column(name = "NAME", length = 255, nullable = false)
	@Comment("名称")
	private String name;

	@Column(name = "PARENT_ID", length = 255)
	@Comment("父节点id")
	private String parentId;

	@Column(name = "ID_NUM", length = 255)
	@Comment("证件号码")
	private String idNum;

	@Column(name = "AVATOR", length = 255)
	@Comment("头像")
	private String avator;

	@Column(name = "PERSON_TYPE", length = 255)
	@Comment("人员类型")
	private String personType;

	@Type(type = "numeric_boolean")
	@Column(name = "ORIGINAL", nullable = false)
	@Comment("原始人员")
	@ColumnDefault("1")
	private Boolean original;

	@Column(name = "ORIGINAL_ID", length = 38)
	@Comment("原始人员id")
	private String originalId;

	@Type(type = "numeric_boolean")
	@Column(name = "GLOBAL_MANAGER", nullable = false)
	@Comment("是否全局管理员")
	@ColumnDefault("0")
	private Boolean globalManager;

	@Column(name = "MANAGER_LEVEL", nullable = false)
	@Comment("是否三员管理员")
	@ColumnDefault("0")
	private Integer managerLevel;

	@Lob
	@Column(name = "ROLES")
	@Comment("拥有的角色列表")
	private String roles;

	@Lob
	@Column(name = "POSITIONS")
	@Comment("拥有的岗位列表")
	private String positions;

}