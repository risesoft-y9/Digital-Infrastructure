package net.risesoft.log.constant;

/**
 * 日志搜索常量
 *
 * @author mengjuhua
 *
 */
public class Y9LogSearchConsts {
    /** 租户id */
    public static final String TENANT_ID = "tenantId";

    /** 租户名称 */
    public static final String TENANT_NAME = "tenantName";
    /** 用户Id */
    public static final String USER_ID = "userId";
    /** 用户名称 */
    public static final String USER_NAME = "userName";
    /** 用户id全路径 */
    public static final String GUID_PATH = "guidPath";
    /** 管理员级别 */
    public static final String MANAGER_LEVEL = "managerLevel";
    /** 客户端IP */
    public static final String USER_HOST_IP = "userHostIp";
    /** 方法类和名称 */
    public static final String METHOD_NAME = "methodName";
    /** 模块名称 */
    public static final String MODULAR_NAME = "modularName";
    /** 日志时间 */
    public static final String LOG_TIME = "logTime";
    /** 日志级别 */
    public static final String LOG_LEVEL = "logLevel";
    /** 登录时间 */
    public static final String LOGIN_TIME = "loginTime";
    /** 操作类型 */
    public static final String OPERATE_TYPE = "operateType";
    /** 操作名称 */
    public static final String OPERATE_NAME = "operateName";
    /** 操作状态 */
    public static final String SUCCESS = "success";
    /** 用时 */
    public static final String ELAPSED_TIME = "elapsedTime";

    private Y9LogSearchConsts() {
        throw new IllegalStateException("Utility class");
    }

}
