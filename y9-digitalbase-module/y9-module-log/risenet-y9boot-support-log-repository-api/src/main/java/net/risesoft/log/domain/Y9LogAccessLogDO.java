package net.risesoft.log.domain;

import java.io.Serializable;
import java.util.Date;

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
@NoArgsConstructor
@Data
public class Y9LogAccessLogDO implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /** 主键，唯一标识 */
    private String id;

    /** 日志时间 */
    private Date logTime;

    /** 系统名称 */
    private String systemName;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    private String modularName;

    /** 方法类和名称 */
    private String methodName;

    /** 日志级别 0=TRACE 1=DEBUG 2=INFO 3=WARN 4=ERROR */
    private String logLevel;

    /** 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动 */
    private String operateType;

    /** 操作名称 */
    private String operateName;

    /** 用时 */
    private long elapsedTime;

    /** 访问服务器IP */
    private String serverIp;

    /** 登录是否成功 */
    private String success;

    /** 访问路径 */
    private String requestUrl;

    /** 错误日志信息 */
    private String errorMessage;

    /** 日志信息 */
    private String logMessage;

    /** 异常信息 */
    private String throwable;

    /** 登录用户机器IP */
    private String userHostIp;

    /** 用户登录浏览器信息 */
    private String userAgent;

    /** 登录用户机器MAC */
    private String macAddress;

    /** 租户ID */
    private String tenantId;

    /** 用户id */
    private String userId;

    /** 登录名称 */
    private String loginName;

    /** 登录名 */
    private String userName;

    /** 租户名称 */
    private String tenantName;

    /** 由name组成的父子关系列表(倒序)，之间用逗号分隔 */
    private String dn;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    private String guidPath;

    /**
     * 管理员类型 {@link ManagerLevelEnum}
     */
    private Integer managerLevel;

}
