package net.risesoft.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 三员 不在组织架构树中展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_MANAGER")
@DynamicUpdate
@Comment("三员表")
@Data
@SuperBuilder
public class Y9Manager extends Y9OrgBase {

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
     * 电子邮件
     */
    @Email
    @Column(name = "EMAIL", length = 255)
    @Comment("电子邮箱")
    private String email;

    /**
     * 登录名
     */
    @NotBlank
    @Column(name = "LOGIN_NAME", length = 255, nullable = false)
    @Comment("登录名")
    private String loginName;

    /**
     * 手机号码
     */
    @Column(name = "MOBILE", length = 255)
    @Comment("手机号码")
    private String mobile;

    /**
     * 登录密码
     */
    @Column(name = "PASSWORD", length = 255)
    @Comment("登录密码")
    private String password;

    /**
     * 排序序列号
     */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序序列号")
    private String orderedPath;

    /** 性别 */
    @ColumnDefault("1")
    @Column(name = "SEX", nullable = false)
    @Comment("性别")
    @Convert(converter = EnumConverter.SexEnumConverter.class)
    private SexEnum sex = SexEnum.MALE;

    /** 管理员类型 */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("管理员类型")
    @ColumnDefault("0")
    @Convert(converter = EnumConverter.ManagerLevelEnumConverter.class)
    private ManagerLevelEnum managerLevel = ManagerLevelEnum.GENERAL_USER;

    /**
     * 区分大三员（true），小三员（false）
     */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "GLOBAL_MANAGER", nullable = false)
    @Comment("是否全局管理员")
    @ColumnDefault("0")
    private Boolean globalManager = false;

    /**
     * 允许访问的客户端IP
     */
    @Comment(value = "允许访问的客户端IP")
    @Column(name = "USER_HOST_IP", length = 150)
    private String userHostIp;

    /**
     * 修改密码时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("上一次密码修改时间")
    @Column(name = "LAST_MODIFY_PASSWORD_TIME")
    private Date lastModifyPasswordTime;

    /**
     * 上一次审查时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment(value = "上一次审查时间")
    @Column(name = "LAST_REVIEW_LOG_TIME")
    private Date lastReviewLogTime;

    public Y9Manager() {
        super.setOrgType(OrgTypeEnum.MANAGER);
    }
}