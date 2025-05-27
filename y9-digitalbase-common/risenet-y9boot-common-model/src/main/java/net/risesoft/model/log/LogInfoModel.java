package net.risesoft.model.log;

import java.io.Serializable;

import lombok.Data;

/**
 * 日子查询条件字段
 *
 * @author Administrator
 *
 */
@Data
public class LogInfoModel implements Serializable {

    private static final long serialVersionUID = -1987196293603346966L;

    /** 用户名称 */
    private String userName;
    /** 租户名称 */
    private String tenantName;
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
    /** 模块名称 */
    private String modularName;
    /** 操作名称 */
    private String operateName;
    /** 操作类型 */
    private String operateType;
    /** 日志级别 */
    private String logLevel;
    /** 排序名称 */
    private String sortName;
    /** 标题 */
    private String title;
    /** 请求参数及值json字符串 */
    private String arguments;

}
