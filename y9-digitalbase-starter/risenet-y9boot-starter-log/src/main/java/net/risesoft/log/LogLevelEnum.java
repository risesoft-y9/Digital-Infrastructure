package net.risesoft.log;

/**
 * 日志级别枚举
 *
 * @author shidaobang
 * @date 2022/09/22
 */
public enum LogLevelEnum {
    /** 普通日志 */
    RSLOG("普通日志"),
    /** 管理日志 */
    MANAGERLOG("管理日志"),
    /** 错误 */
    ERROR("错误日志"),
    /** 警告 */
    WARN("警告日志"),
    /** 信息 */
    INFO("信息日志"),
    /** 调试 */
    DEBUG("调试日志"),
    /** 跟踪日志 */
    TRACE("跟踪日志");

    private final String value;

    LogLevelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
