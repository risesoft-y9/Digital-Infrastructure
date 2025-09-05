package net.risesoft.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;

/**
 * 工作流日志管理
 * 
 * @author qinman
 */
@RestController
@RequestMapping(value = "/admin/flowable/accessLog")
@Slf4j
@RequiredArgsConstructor
public class FlowableAccessLogController {

    private final Y9logFlowableAccessLogService logService;

    /**
     * 获取操作用时的柱状图数据
     *
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return {@code Y9Result<List<Long>>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取操作用时的柱状图数据", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/listElapsedTimeData")
    public Y9Result<List<Long>> listElapsedTimeData(String startDay, String endDay) {
        List<Long> list = logService.listOperateTimeCount(startDay, endDay);
        return Y9Result.success(list);
    }

    /**
     * 搜索操作用时列表
     *
     * @param searchDto 搜索条件
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @param sTime 开始时间
     * @param lTime 结束时间
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logAccessLog>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "搜索操作用时列表", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/pageByElapsedTime")
    public Y9Page<Y9LogFlowableAccessLogDO> pageByElapsedTime(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, Y9PageQuery pageQuery) {
        try {
            Page<Y9LogFlowableAccessLogDO> pageResult = logService.pageElapsedTimeByCondition(searchDto, startDay,
                endDay, sTime, lTime, pageQuery.getPage(), pageQuery.getSize());
            return Y9Page.success(pageQuery.getPage(), pageResult.getTotalPages(), pageResult.getTotalElements(),
                pageResult.getContent());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取操作状态列表数据
     *
     * @param searchDto 搜索条件
     * @param date 搜索时间
     * @param hour 分
     * @param operateStatus 操作状态
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logAccessLog>}
     * @throws ParseException an error has been reached unexpectedly while parsing.
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取操作状态列表数据", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/pageByOperateStatus")
    public Y9Page<Y9LogFlowableAccessLogDO> pageByOperateStatus(LogInfoModel searchDto, String date, String hour,
        String operateStatus, Y9PageQuery pageQuery) throws ParseException {
        Page<Y9LogFlowableAccessLogDO> pageResult = logService.pageOperateStatusByOperateStatus(searchDto,
            operateStatus, date, hour, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageResult.getTotalPages(), pageResult.getTotalElements(),
            pageResult.getContent());
    }

    /**
     * 获取日志分页列表
     *
     * @param pageQuery 搜索信息
     * @param sort 排序字段
     * @return {@code Y9Page<Y9logAccessLog>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取日志分页列表", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/page")
    public Y9Page<Y9LogFlowableAccessLogDO> page(Y9PageQuery pageQuery, String sort) {
        Page<Y9LogFlowableAccessLogDO> pageList = logService.page(pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent());
    }

    /**
     * 搜索日志信息
     *
     * @param searchDto 搜索信息
     * @param pageQuery 分页信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return {@code Y9Page<Y9logAccessLog>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "搜索日志信息", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/pageSearch")
    public Y9Page<Y9LogFlowableAccessLogDO> pageSearch(LogInfoModel searchDto, Y9PageQuery pageQuery,
        @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        Page<Y9LogFlowableAccessLogDO> resultPage =
            logService.pageSearchByCondition(searchDto, startTime, endTime, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), resultPage.getTotalPages(), resultPage.getTotalElements(),
            resultPage.getContent());
    }
}