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
     * 日志级别 0=TRACE 1=DEBUG 2=INFO 3=WARN 4=ERROR
     */
    private String logLevel;

    /**
     * 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动
     */
    private String operateType;

    /**
     * 操作名称
     */
    private String operateName;

    /**
     * 模块名称，比如：公文就转-发文-授权管理
     */
    private String modularName;

    /**
     * 方法类和名称
     */
    private String methodName;

    /**
     * 用时
     */
    private String elapsedTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户ip
     */
    private String userHostIp;

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
     * 用户的承继关系
     */
    private String dn;

    /**
     * 用户的由ID组成的父子关系列表，之间用逗号分隔
     */
    private String guidPath;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     *
     * 登录名称
     */
    private String loginName;

    /**
     * 三员级别
     */
    private String managerLevel;
}
