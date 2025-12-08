package net.risesoft.model.log;

import java.io.Serializable;

import lombok.Data;

/**
 * 登录日志查询条件
 *
 * @author shidaobang
 * @date 2025/12/08
 */
@Data
public class LoginLogQuery implements Serializable {

    private static final long serialVersionUID = 8573787270889342862L;

    /** 用户名称 */
    private String userName;

    /** 客户端IP */
    private String userHostIp;

    /** 操作系统 */
    private String osName;

    /** 分辨率 */
    private String screenResolution;

    /** 操作状态 */
    private String success;

    /** 浏览器名称 */
    private String browserName;

    /** 浏览器版本 */
    private String browserVersion;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;

}
