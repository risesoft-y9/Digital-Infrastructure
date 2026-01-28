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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志信息
 *
 * @author shidaobang
 * @author guoweijun
 * @author mengjuhua
 */
@Entity
@Table(name = "Y9_LOG_FLOWABLE_ACCESS_LOG",
    indexes = {@Index(name = "index_flowable_logInfo_userId", columnList = "USER_ID ASC", unique = false)}, comment = "工作流日志信息表")
@NoArgsConstructor
@Data
public class Y9LogFlowableAccessLog implements Serializable {

    private static final long serialVersionUID = 8085477796175696619L;
    /**
     * 主键
     */
    @Id
    @Column(name = "ID", comment = "主键")
    private String id;

    /**
     * 流程序列号
     */
    @Column(name = "PROCESS_SERIAL_NUMBER", comment = "流程序列号")
    private String processSerialNumber;

    /**
     * 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动
     */
    @Column(name = "OPERATE_TYPE", length = 38, comment = "操作类别")
    private String operateType;

    /**
     * 标题
     */
    @Column(name = "TITLE", length = 1000, comment = "标题")
    private String title;

    /**
     * 请求参数和值
     */
    @Column(name = "ARGUMENTS", length = 5000, comment = "请求参数和值")
    private String arguments;

    /** 用户id */
    @Column(name = "USER_ID", length = 38, comment = "用户id")
    private String userId;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    @Column(name = "GUID_PATH", length = 400, comment = "由ID组成的父子关系列表")
    private String guidPath;

    /** 用户名 */
    @Column(name = "USER_NAME", length = 100, comment = "用户名")
    private String userName;

    /** 登录名称 */
    @Column(name = "LOGIN_NAME", length = 50, comment = "登录名称")
    private String loginName;

    /**
     * 用户类型(涉密等级)
     */
    @Column(name = "PERSON_TYPE", length = 50, comment = "用户类型")
    private String personType;

    /** 由name组成的父子关系列表(倒序)，之间用逗号分隔 */
    @Column(name = "DN", length = 2000, comment = "由name组成的父子关系列表")
    private String dn;

    /** 日志时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "LOG_TIME", comment = "日志时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;

    /**
     * 日志级别 0=普通用户 1=部门管理员 2=全局管理员
     */
    @Column(name = "LOG_LEVEL", length = 38, comment = "日志级别")
    private String logLevel;

    /** 操作名称 */
    @Column(name = "OPERATE_NAME", length = 200, comment = "操作名称")
    private String operateName;

    /** 系统名称 */
    @Column(name = "SYSTEM_NAME", length = 50, comment = "系统名称")
    private String systemName;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    @Column(name = "MODULAR_NAME", length = 200, comment = "模块名称")
    private String modularName;

    /** 方法类和名称 */
    @Column(name = "METHOD_NAME", length = 200, comment = "方法类和名称")
    private String methodName;

    /** 用时 */
    @Column(name = "ELAPSED_TIME", comment = "用时")
    private long elapsedTime;

    /** 登录用户机器IP */
    @Column(name = "USER_HOST_IP", length = 50, comment = "登录用户机器IP")
    private String userHostIp;

    /** 访问服务器IP */
    @Column(name = "SERVER_IP", length = 50, comment = "访问服务器IP")
    private String serverIp;

    /** 登录用户机器MAC */
    @Column(name = "MAC_ADDRESS", length = 100, comment = "登录用户机器MAC")
    private String macAddress;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38, comment = "租户ID")
    private String tenantId;

    /** 租户名称 */
    @Column(name = "TENANT_NAME", length = 50, comment = "租户名称")
    private String tenantName;

    /** 登录是否成功 */
    @Column(name = "SUCCESS", nullable = false, comment = "登录是否成功")
    private String success;

    /** 访问路径 */
    @Column(name = "REQUEST_URL", length = 1000, comment = "访问路径")
    private String requestUrl;

    /** 错误日志信息 */
    @Lob
    @Column(name = "ERROR_MESSAGE", comment = "错误日志信息")
    private String errorMessage;

    /** 日志信息 */
    @Lob
    @Column(name = "LOG_MESSAGE", comment = "日志信息")
    private String logMessage;

    /** 异常信息 */
    @Lob
    @Column(name = "THROWABLE", comment = "异常信息")
    private String throwable;

    /** 用户登录浏览器信息 */
    @Column(name = "USER_AGENT", length = 200, comment = "用户登录浏览器信息")
    private String userAgent;
}
