package net.risesoft.model.userlogininfo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 登录详细信息表
 *
 * @author mengjuhua
 */
@Data
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /**
     * 浏览器账号密码登录
     */
    public static final String LOGIN_TYPE_BROWSER_LOGINNAME = "browser_loginName";

    /**
     * 浏览器手机号码登录
     */
    public static final String LOGIN_TYPE_BROWSER_MOBILE = "browser_mobile";

    /**
     * CA登录
     */
    public static final String LOGIN_TYPE_BROWSER_CA = "ca";

    /**
     * 移动端登录
     */
    public static final String LOGIN_TYPE_MOBILE = "mobile";

    /**
     * VPN登录
     */
    public static final String LOGIN_TYPE_BROWSER_VPN = "vpn";

    /** 主键 */
    private String id;

    /** 登录时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    /** 登录方式 */
    private String loginType;

    /** 登录人id */
    private String userId;

    /** 登录人姓名 */
    private String userName;

    /** 登录人机器ip */
    private String userHostIp;

    /** 登录用户机器MAC */
    private String userHostMac;

    /** 登录用户机器名称 */
    private String userHostName;

    /** 登录机器的硬盘ID */
    private String userHostDiskId;

    /** 租户id */
    private String tenantId;

    /** 租户名称 */
    private String tenantName;

    /** 访问单点登录服务器Ip */
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

    /** 客户端地址 */
    private String clientIpSection;

}
