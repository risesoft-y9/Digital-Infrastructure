package net.risesoft.log.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志信息
 *
 * @author shidaobang
 * @author guoweijun
 * @author mengjuhua
 */
@NoArgsConstructor
@Data
public class Y9LogFlowableAccessLogDO implements Serializable {

    private static final long serialVersionUID = 8085477796175696619L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动
     */
    private String operateType;

    /**
     * 标题
     */
    private String title;

    /**
     * 请求参数和值
     */
    private String arguments;

    /** 用户id */
    private String userId;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    private String guidPath;

    /** 登录名 */
    private String userName;

    /** 登录名称 */
    private String loginName;

    /**
     * 用户类型(涉密等级)
     */
    private String personType;

    /** 由name组成的父子关系列表(倒序)，之间用逗号分隔 */
    private String dn;

    /** 日志时间 */
    private Date logTime;

    /**
     * 日志级别 0=普通用户 1=部门管理员 2=全局管理员
     */
    private String logLevel;

    /** 操作名称 */
    private String operateName;

    /** 系统名称 */
    private String systemName;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    private String modularName;

    /** 方法类和名称 */
    private String methodName;

    /** 用时 */
    private long elapsedTime;

    /** 登录用户机器IP */
    private String userHostIp;

    /** 访问服务器IP */
    private String serverIp;

    /** 登录用户机器MAC */
    private String macAddress;

    /** 租户ID */
    private String tenantId;

    /** 租户名称 */
    private String tenantName;

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

    /** 用户登录浏览器信息 */
    private String userAgent;
}
