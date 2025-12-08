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

import net.risesoft.enums.platform.org.ManagerLevelEnum;

/**
 * 日志信息
 *
 * @author shidaobang
 * @author guoweijun
 * @author mengjuhua
 *
 */
@Entity
@Table(name = "Y9_LOG_ACCESS_LOG",
    indexes = {@Index(name = "index_logInfo_userId", columnList = "USER_ID ASC", unique = false)})
@Comment("日志信息表")
@NoArgsConstructor
@Data
public class Y9LogAccessLog implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    /** 日志级别 0=TRACE 1=DEBUG 2=INFO 3=WARN 4=ERROR */
    @Column(name = "LOG_LEVEL", length = 38)
    @Comment(value = "日志级别 0=TRACE 1=DEBUG 2=INFO 3=WARN 4=ERROR")
    private String logLevel;

    /** 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动 */
    @Column(name = "OPERATE_TYPE", length = 38)
    @Comment(value = "操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动")
    private String operateType;

    /** 操作名称 */
    @Column(name = "OPERATE_NAME", length = 200)
    @Comment(value = "操作名称")
    private String operateName;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    @Column(name = "MODULAR_NAME", length = 200)
    @Comment(value = "模块名称，比如：公文就转-发文-授权管理")
    private String modularName;

    /** 方法类和名称 */
    @Column(name = "METHOD_NAME", length = 200)
    @Comment(value = "方法类和名称")
    private String methodName;

    /** 日志时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "LOG_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("日志时间")
    private Date logTime;

    /** 用时 */
    @Column(name = "ELAPSED_TIME")
    @Comment(value = "用时")
    private long elapsedTime;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38)
    @Comment(value = "租户ID")
    private String tenantId;

    /** 用户id */
    @Column(name = "USER_ID", length = 38)
    @Comment(value = "用户id")
    private String userId;

    /** 登录名称 */
    @Column(name = "LOGIN_NAME", length = 50)
    @Comment(value = "登录名称")
    private String loginName;

    /** 登录名 */
    @Column(name = "USER_NAME", length = 100)
    @Comment(value = "登录名")
    private String userName;

    /** 登录用户机器IP */
    @Column(name = "USER_HOST_IP", length = 50)
    @Comment(value = "登录用户机器IP")
    private String userHostIp;

    /** 租户名称 */
    @Column(name = "TENANT_NAME", length = 50)
    @Comment(value = "租户名称")
    private String tenantName;

    /** 系统名称 */
    @Column(name = "SYSTEM_NAME", length = 50)
    @Comment(value = "系统名称")
    private String systemName;

    /** 访问服务器IP */
    @Column(name = "SERVER_IP", length = 50)
    @Comment(value = "访问服务器IP")
    private String serverIp;

    /** 登录是否成功 */
    @Column(name = "SUCCESS", nullable = false)
    @Comment(value = "登录是否成功")
    private String success;

    /** 访问路径 */
    @Column(name = "REQUEST_URL", length = 1000)
    @Comment(value = "访问路径")
    private String requestUrl;

    /** 错误日志信息 */
    @Lob
    @Column(name = "ERROR_MESSAGE")
    @Comment(value = "错误日志信息")
    private String errorMessage;

    /** 日志信息 */
    @Lob
    @Column(name = "LOG_MESSAGE")
    @Comment(value = "日志信息")
    private String logMessage;

    /** 异常信息 */
    @Lob
    @Column(name = "THROWABLE")
    @Comment(value = "异常信息")
    private String throwable;

    /** 由name组成的父子关系列表(倒序)，之间用逗号分隔 */
    @Column(name = "DN", length = 2000)
    @Comment("由name组成的父子关系列表(倒序)，之间用逗号分隔")
    private String dn;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    @Column(name = "GUID_PATH", length = 400)
    @Comment("由ID组成的父子关系列表(正序)，之间用逗号分隔")
    private String guidPath;

    /** 用户登录浏览器信息 */
    @Column(name = "USER_AGENT", length = 200)
    @Comment(value = "用户登录浏览器信息")
    private String userAgent;

    /** 登录用户机器MAC */
    @Column(name = "MAC_ADDRESS", length = 100)
    @Comment(value = "登录用户机器MAC")
    private String macAddress;

    /**
     * 管理员类型 {@link ManagerLevelEnum}
     */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("管理员类型")
    @ColumnDefault("0")
    private Integer managerLevel;

}
