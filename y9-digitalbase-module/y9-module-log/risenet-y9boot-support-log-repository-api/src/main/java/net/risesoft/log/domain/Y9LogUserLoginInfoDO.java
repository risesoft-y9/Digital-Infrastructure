package net.risesoft.log.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;

/**
 * 人员登录日志表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9LogUserLoginInfoDO implements Serializable {
    private static final long serialVersionUID = -6476891120156676097L;

    /** 主键，唯一标识 */
    private String id;

    /** 登录时间 */
    private Date loginTime;

    /** 登录方式 */
    private String loginType;

    /** 用户id */
    private String userId;

    /** 登录名 */
    private String userName;

    /** 登录名称 */
    private String userLoginName;

    /** 登录用户机器IP */
    private String userHostIp;

    /** 登录用户机器MAC */
    private String userHostMac;

    /** 登录用户机器名称 */
    private String userHostName;

    /** 登录机器的硬盘ID */
    private String userHostDiskId;

    /** 租户ID */
    private String tenantId;

    /** 租户名称 */
    private String tenantName;

    /** 访问单点登录服务器IP */
    private String serverIp;

    /** 登录是否成功 */
    private String success = "true";

    /** 登陆日志信息 */
    private String logMessage;

    /** 浏览器名称 */
    private String browserName;

    /** 浏览器版本 */
    private String browserVersion;

    /** 客户端电脑操作系统版本信息 */
    private String osName;

    /** 访问用户的电脑分辨率 */
    private String screenResolution;

    /** clientIp的ABC段 */
    private String clientIpSection;

    /**
     * 三员级别 {@link ManagerLevelEnum}
     */
    private String managerLevel;
}
