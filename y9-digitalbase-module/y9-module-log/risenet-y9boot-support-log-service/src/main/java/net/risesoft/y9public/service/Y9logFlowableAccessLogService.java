package net.risesoft.y9public.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.FlowableAccessLogQuery;
import net.risesoft.pojo.Y9Page;

/**
 * 日志管理
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logFlowableAccessLogService {

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
     * @return {@code Page<Y9logFlowableAccessLog>}
     */
    Page<Y9LogFlowableAccessLogDO> page(int page, int rows, String sort);

    /**
     * 多条件分页查询访问日志
     *
     * @param search 查询条件
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<AccessLog>}
     */
    Y9Page<FlowableAccessLog> pageByCondition(FlowableAccessLogQuery search, Integer page, Integer rows);

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
     * @return {@code Page<Y9logFlowableAccessLog>}
     * @throws ParseException -an error has been reached unexpectedly while parsing.
     */
    Page<Y9LogFlowableAccessLogDO> pageElapsedTimeByCondition(FlowableAccessLogQuery search, String startDay,
        String endDay, String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    /**
     * 获取操作状态分页列表数据
     *
     * @param search 搜索条件
     * @param operateStatus 操作状态
     * @param date 时
     * @param hour 分
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logFlowableAccessLog>}
     * @throws ParseException -an error has been reached unexpectedly while parsing.
     */
    Page<Y9LogFlowableAccessLogDO> pageOperateStatusByOperateStatus(FlowableAccessLogQuery search, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException;

    /**
     * 搜索日志信息
     *
     * @param search 搜索条件
     * @param startTime 日志开始时间
     * @param endTime 日志结束时间
     * @param page 页数
     * @param rows 条数
     * @return {@code Page<Y9logFlowableAccessLog>}
     */
    Page<Y9LogFlowableAccessLogDO> pageSearchByCondition(FlowableAccessLogQuery search, String startTime,
        String endTime, Integer page, Integer rows);

    void save(Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO);
}
