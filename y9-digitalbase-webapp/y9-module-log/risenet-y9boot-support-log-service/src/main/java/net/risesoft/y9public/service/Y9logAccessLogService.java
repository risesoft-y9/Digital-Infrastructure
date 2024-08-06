package net.risesoft.y9public.service;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logAccessLog;

/**
 * 日志管理
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logAccessLogService {

    /**
     * 统计APP被点击的排行情况
     *
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param tenantId 租户id
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return {@code Map<String, Object>} APP被点击的排行情况统计信息
     * @throws UnknownHostException -Thrown to indicate that the IP address of a host could not be determined.
     * @see UnknownHostException
     */
    Map<String, Object> getAppClickCount(String orgId, String orgType, String tenantId, String startDay, String endDay)
        throws UnknownHostException;

    /**
     * 统计模块访问情况
     *
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param tenantId 租户id
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return {@code Map<String, Object>} 模块访问情况统计信息
     */
    Map<String, Object> getModuleNameCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay);

    /**
     * 统计操作状态的用时情况
     *
     * @param selectedDate 时间段
     * @return {@code Map<String, Object>} 操作状态用时情况统计信息
     */
    Map<String, Object> getOperateStatusCount(String selectedDate);

    List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId);

    /**
     * 获取操作用时的柱状图数据
     *
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return {@code List<Long>} 操作用时的柱状图数据
     */
    List<Long> listOperateTimeCount(String startDay, String endDay);

    /**
     * 获取日志分页列表
     *
     * @param page 页数
     * @param rows 条数
     * @param sort 排序方式
     * @return {@code Page<Y9logAccessLog>}
     */
    Page<Y9logAccessLog> page(int page, int rows, String sort);

    /**
     * 多条件分页查询访问日志
     *
     * @param search 搜索结果
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<AccessLog>}
     */
    Y9Page<AccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    /**
     * 根据操作类型分页查找访问日志
     *
     * @param operateType 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<AccessLog>}
     */
    Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows);

    /**
     * 根据组织架构类型分页查找访问日志
     *
     * @param tenantId 租户id
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param operateType 操作状态
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<AccessLog>}
     */
    Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType, Integer page,
        Integer rows);

    /**
     * 根据租户id和三元级别，获取管理员操作分页日志
     *
     * @param tenantId 租户id
     * @param managerLevel 三元级别
     * @param userId 人员id
     * @param page 页数
     * @param rows 条数
     * @param sort 排序条件
     * @return {@code Page<Y9logAccessLog>}
     */
    Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel, String userId,
        Integer page, Integer rows, String sort);

    /**
     * 搜索操作用时分页列表
     *
     * @param search 搜索条件
     * @param startDay 日志开始时间
     * @param endDay 日志结束时间
     * @param startTime 操作用时开始时间
     * @param endTime 操作用时结束时间
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logAccessLog>}
     * @throws ParseException -an error has been reached unexpectedly while parsing.
     */
    Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    /**
     * 获取操作状态分页列表数据
     *
     * @param search 搜索条件
     * @param operateStatus 操作状态
     * @param date 时
     * @param hour 分
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logAccessLog>}
     * @throws ParseException -an error has been reached unexpectedly while parsing.
     */
    Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus, String date,
        String hour, Integer page, Integer rows) throws ParseException;

    /**
     * 搜索日志信息
     *
     * @param search 搜索条件
     * @param startTime 日志开始时间
     * @param endTime 日志结束时间
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logAccessLog>}
     */
    Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    void save(Y9logAccessLog y9logAccessLog);

    /**
     * 根据租户id，三元级别，查询操作日志
     *
     * @param tenantId 租户id
     * @param managerLevel 三元级别
     * @param loginInfoModel 搜索信息
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logAccessLog>}
     */
    Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, Integer page,
        Integer rows);
}
