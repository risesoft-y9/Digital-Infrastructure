package net.risesoft.model.log;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问日志
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessLog implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 日志记录时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logTime;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 模块名称，比如：公文就转-发文-授权管理
     */
    private String modularName;

    /**
     * 方法类和名称
     */
    private String methodName;

    /**
     * 参数
     */
    private String paramsJson;

    /**
     * 日志级别： 普通日志、管理日志、错误日志、警告日志、信息日志、调试日志、跟踪日志
     */
    private String logLevel;

    /**
     * 操作类别： 查看，增加，修改，删除，发送，活动，登录，退出，检查
     */
    private String operateType;

    /**
     * 操作名称
     */
    private String operateName;

    /**
     * 用时
     */
    private long elapsedTime;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 是否成功
     */
    private String success;

    /**
     * 访问路径
     */
    private String requestUrl;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 日志信息
     */
    private String logMessage;

    /**
     * 异常信息
     */
    private String throwable;

    /**
     * 用户ip
     */
    private String userHostIp;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * mac地址
     */
    private String macAddress;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     *
     * 登录名称
     */
    private String loginName;

    /**
     * 用户的承继关系
     */
    private String dn;

    /**
     * 用户的由ID组成的父子关系列表，之间用逗号分隔
     */
    private String guidPath;

    /**
     * 三员级别：0：一般用户，1：系统管理员，2：安全保密员，3：安全审计员，4：云系统管理员，5：云安全保密员，6：云安全审计员
     */
    private Integer managerLevel;
}
