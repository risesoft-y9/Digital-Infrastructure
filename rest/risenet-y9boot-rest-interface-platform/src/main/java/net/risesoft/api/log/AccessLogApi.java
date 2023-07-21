package net.risesoft.api.log;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import net.risesoft.model.AccessLog;
import net.risesoft.pojo.Y9Page;

/**
 * 访问日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
public interface AccessLogApi {

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @since 9.6.0
     */
    void asyncSaveLog(AccessLog accessLog);

    /**
     * 异步保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     * @since 9.6.0
     */
    void asyncSaveLogByJson(String accessLogJson);

    /**
     * 获取模块访问次数
     *
     * @param orgId 机构id
     * @param orgType 机构类型
     * @param tenantId 租户id
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return Map&lt;String, Object&gt; 模块访问次数详情
     * @since 9.6.0
     */
    Map<String, Object> getModuleCount(String orgId, String orgType, String tenantId, String startDay, String endDay);

    /**
     * 根据操作类型分页查找日志
     *
     * @param operateType 操作类型
     * @param page 页码数
     * @param rows 每页条数
     * @return Y9Page&lt;AccessLog&gt; 日志列表
     * @since 9.6.0
     */
    Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows);

    /**
     * 根据组织架构类型分页查找日志
     *
     * @param tenantId 组织id
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param operateType 操作类型
     * @param page 页码数
     * @param rows 每页条数
     * @return Y9Page&lt;AccessLog&gt; 日志列表
     * @since 9.6.0
     */
    Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType, Integer page, Integer rows);

    /**
     * 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    boolean saveLog(AccessLog accessLog);

    /**
     * 保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    boolean saveLogByJson(String accessLogJson);

    /**
     * 多条件分页查询
     *
     * @param logLevel 日志级别
     * @param success 是否成功
     * @param operateType 操作类型
     * @param operateName 操作名称
     * @param userName 用户名
     * @param userHostIp 用户ip
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码数
     * @param rows 每页条数
     * @return Y9Page&lt;AccessLog&gt; 日志列表
     * @throws ParseException 解析异常
     * @since 9.6.0
     */
    Y9Page<AccessLog> search(String logLevel, String success, String operateType, String operateName, String userName, String userHostIp, String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    /**
     * 获取日志
     *
     * @param loginName 登录名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tenantId 租户id
     * @return List&lt;String&gt; 返回日志数据
     * @since 9.6.0
     */
    List<String> searchLog(String loginName, Long startTime, Long endTime, String tenantId);
}