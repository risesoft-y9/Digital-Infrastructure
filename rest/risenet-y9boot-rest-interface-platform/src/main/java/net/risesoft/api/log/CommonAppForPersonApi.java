package net.risesoft.api.log;

/**
 * 个人常用应用组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
public interface CommonAppForPersonApi {

    /**
     * 根据用户id，获取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String 常用应用名称列表
     * @since 9.6.0
     */
    public String getAppNamesByPersonId(String tenantId, String personId);

    /**
     * 根据用户id，从日志中提取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String 常用应用名称列表
     * @since 9.6.0
     */
    public String getAppNamesByPersonIdFromLog(String tenantId, String personId);

    /**
     * 半年应用点击次数
     *
     * @return long 点击次数
     * @since 9.6.0
     */
    public long getCount();

    /**
     * 保存常用应用
     *
     * @return String 操作结果
     * @since 9.6.0
     */
    public String saveCommonApp();

    /**
     * 同步近半年的数据
     *
     * @return String 操作结果
     * @since 9.6.0
     */
    public String syncData();
}
