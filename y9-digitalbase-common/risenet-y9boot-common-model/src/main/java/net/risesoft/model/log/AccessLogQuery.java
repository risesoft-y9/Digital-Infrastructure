package net.risesoft.model.log;

import java.io.Serializable;

import lombok.Data;

/**
 * 访问日志查询条件
 *
 * @author shidaobang
 * @date 2025/12/08
 */
@Data
public class AccessLogQuery implements Serializable {

    private static final long serialVersionUID = -1987196293603346966L;

    /** 用户名称 */
    protected String userName;

    /** 客户端IP */
    protected String userHostIp;

    /** 操作状态 */
    protected String success;

    /** 开始时间 */
    protected String startTime;

    /** 结束时间 */
    protected String endTime;

    protected String systemName;

    /** 模块名称 */
    protected String modularName;

    /** 操作名称 */
    protected String operateName;

    /** 操作类型 */
    protected String operateType;

    /** 日志级别 */
    protected String logLevel;

}
