package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.platform.ManagerLevelEnum;

/**
 * 人员登录日志表
 * 
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_USER_LOGIN_INFO",
    indexes = {@Index(name = "index_loginInfo_userHostIp", columnList = "USER_HOST_IP ASC", unique = false)})
@Comment("用户登录历史表")
@NoArgsConstructor
@Data
public class Y9logUserLoginInfo implements Serializable {
    private static final long serialVersionUID = -6476891120156676097L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "LOGIN_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment(value = "登录时间")
    private Date loginTime;

    @Column(name = "LOGIN_TYPE", length = 38)
    @Comment(value = "登录方式")
    private String loginType;

    /** 用户id */
    @Column(name = "USER_ID", length = 38)
    @Comment(value = "用户id")
    private String userId;

    /** 登录名 */
    @Column(name = "USER_NAME", length = 100)
    @Comment(value = "登录名")
    private String userName;

    /** 登录名称 */
    @Column(name = "USER_LOGIN_NAME", length = 50)
    @Comment(value = "登录名称")
    private String userLoginName;

    /** 登录用户机器IP */
    @Column(name = "USER_HOST_IP", length = 50)
    @Comment(value = "登录用户机器IP")
    private String userHostIp;

    /** 登录用户机器MAC */
    @Column(name = "USER_HOST_MAC", length = 100)
    @Comment(value = "登录用户机器MAC")
    private String userHostMac;

    /** 登录用户机器名称 */
    @Column(name = "USER_HOST_NAME", length = 50)
    @Comment(value = "登录用户机器名称")
    private String userHostName;

    /** 登录机器的硬盘ID */
    @Column(name = "USER_HOST_DISKID", length = 50)
    @Comment(value = "登录机器的硬盘ID")
    private String userHostDiskId;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38)
    @Comment(value = "租户ID")
    private String tenantId;

    /** 租户名称 */
    @Column(name = "TENANT_NAME", length = 50)
    @Comment(value = "租户名称")
    private String tenantName;

    /** 访问单点登录服务器IP */
    @Column(name = "SERVER_IP", length = 50, nullable = false)
    @Comment(value = "访问单点登录服务器IP")
    private String serverIp;

    /** 登录是否成功 */
    @Column(name = "SUCCESS", nullable = false)
    @Comment(value = "登录是否成功")
    private String success = "true";

    /** 登陆日志信息 */
    @Lob
    @Column(name = "LOG_MESSAGE")
    @Comment(value = "登陆日志信息")
    private String logMessage;

    /** 浏览器名称 */
    @Column(name = "BROWSER_NAME", length = 50)
    @Comment(value = "浏览器名称")
    private String browserName;

    /** 浏览器版本 */
    @Column(name = "BROWSER_VERSION", length = 50)
    @Comment(value = "浏览器版本")
    private String browserVersion;

    /** 客户端电脑操作系统版本信息 */
    @Column(name = "OS_NAME", length = 30)
    @Comment(value = "客户端电脑操作系统版本信息")
    private String osName;

    /** 访问用户的电脑分辨率 */
    @Column(name = "SCREEN_RESOLUTION", length = 30)
    @Comment(value = "访问用户的电脑分辨率")
    private String screenResolution;

    /** clientIp的ABC段 */
    @Column(name = "CLIENT_IP_SECTION", length = 50)
    @Comment(value = "ip的前三位，如ip:192.168.1.114,则clientIp4ABC为192.168.1")
    private String clientIpSection;

    /**
     * 三员级别 {@link ManagerLevelEnum}
     */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("三员级别")
    @ColumnDefault("0")
    private String managerLevel;
}
