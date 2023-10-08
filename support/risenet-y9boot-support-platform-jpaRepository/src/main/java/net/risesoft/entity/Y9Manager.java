package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import lombok.Data;

import net.risesoft.enums.ManagerLevelEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.enums.SexEnum;

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
@org.hibernate.annotations.Table(comment = "三员表", appliesTo = "Y9_ORG_MANAGER")
@Data
public class Y9Manager extends Y9OrgBase {

    private static final long serialVersionUID = -6531424704457510017L;

    public static final int DEFAULT_PWD_CYCLE = 7;

    public Y9Manager() {
        super.setOrgType(OrgTypeEnum.MANAGER.getEnName());
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

    /**
     * 性别
     * <p>
     * {@link net.risesoft.enums.SexEnum}
     */
    @ColumnDefault("1")
    @Column(name = "SEX", nullable = false)
    @Comment("性别")
    private Integer sex = SexEnum.MALE.getValue();

    /**
     * 管理员类型
     * <p>
     * {@link ManagerLevelEnum}
     */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("管理员类型")
    @ColumnDefault("0")
    private Integer managerLevel = ManagerLevelEnum.GENERAL_USER.getValue();

    /**
     * 区分大三员（true），小三员（false）
     */
    @Type(type = "numeric_boolean")
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
     * 修改密码周期（天）
     */
    @Comment(value = "修改密码周期（天）")
    @Column(name = "PWD_CYCLE")
    private Integer pwdCycle = DEFAULT_PWD_CYCLE;

    /**
     * 审查周期
     */
    @Comment(value = "审查周期")
    @Column(name = "CHECK_CYCLE")
    private Integer checkCycle;

    /**
     * 审查时间
     */
    @Comment(value = "审查时间")
    @Column(name = "CHECK_TIME", length = 50)
    private String checkTime;

    /**
     * 修改密码时间
     */
    @Comment(value = "修改密码时间")
    @Column(name = "MODIFY_PWD_TIME", length = 50)
    private String modifyPwdTime;

}