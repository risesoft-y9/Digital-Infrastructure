package net.risesoft.controller;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.service.Y9logAccessLogService;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 日志管理
 *
 * @author mengjuhua
 * @author guoweijun
 * @author shidaobang
 *
 */
@RestController
@RequestMapping(value = "/admin/accessLog")
@Slf4j
@RequiredArgsConstructor
public class AccessLogController {

    private final Y9logAccessLogService logService;

    /**
     * 统计APP被点击的排行情况
     *
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return
     */
    @RequestMapping(value = "/getAppClickCount")
    public Y9Result<Map<String, Object>> getAppClickCount(String orgId, String orgType, String startDay,
        String endDay) {
        Map<String, Object> map = new HashMap<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            map = logService.getAppClickCount(orgId, orgType, tenantId, startDay, endDay);
        } catch (UnknownHostException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Result.success(map);
    }

    /**
     * 统计模块访问情况
     *
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return
     */
    @RequestMapping(value = "/getModuleAccessData")
    public Y9Result<Map<String, Object>> getModuleAccessData(String orgId, String orgType, String startDay,
        String endDay) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = logService.getModuleNameCount(orgId, orgType, tenantId, startDay, endDay);
        return Y9Result.success(map);
    }

    /**
     * 获取操作状态的统计数据
     *
     * @param selectedDate 选择日期
     * @return
     */
    @RequestMapping(value = "/getOperateStatusData")
    public Y9Result<Map<String, Object>> getOperateStatusData(@RequestParam(required = false) String selectedDate) {
        Map<String, Object> map = logService.getOperateStatusCount(selectedDate);
        return Y9Result.success(map);
    }

    /**
     * 获取操作用时的柱状图数据
     *
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return
     */
    @RequestMapping(value = "/listEplasedTimeData")
    public Y9Result<List<Long>> listEplasedTimeData(String startDay, String endDay) {
        List<Long> list = logService.listOperateTimeCount(startDay, endDay);
        return Y9Result.success(list);
    }

    /**
     * 查看安全审计员日志
     *
     * @param userId 人员id
     * @param pageQuery 分页信息
     * @param sort 排序字段
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看安全审计员日志")
    @RequestMapping(value = "/pageByAuditManagers")
    public Y9Page<Y9logAccessLog> pageByAuditManagers(String userId, Y9PageQuery pageQuery, String sort) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.AUDIT_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_AUDIT_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.pageByTenantIdAndManagerLevelAndUserId(tenantId,
            String.valueOf(managerLevel), userId, pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取安全审计员分页列表成功");
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
     * @return
     */
    @RequestMapping(value = "/pageByElapsedTime")
    public Y9Page<Y9logAccessLog> pageByElapsedTime(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, Y9PageQuery pageQuery) {
        try {
            Page<Y9logAccessLog> pageResult = logService.pageElapsedTimeByCondition(searchDto, startDay, endDay, sTime,
                lTime, pageQuery.getPage(), pageQuery.getSize());
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
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/pageByOperateStatus")
    public Y9Page<Y9logAccessLog> pageByOperateStatus(LogInfoModel searchDto, String date, String hour,
        String operateStatus, Y9PageQuery pageQuery) throws ParseException {
        Page<Y9logAccessLog> pageResult = logService.pageOperateStatusByOperateStatus(searchDto, operateStatus, date,
            hour, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageResult.getTotalPages(), pageResult.getTotalElements(),
            pageResult.getContent());
    }

    /**
     * 查看安全保密员日志
     *
     * @param userId 用户id
     * @param pageQuery 分页信息
     * @param sort 排序字段
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看安全保密员日志")
    @RequestMapping(value = "/pageBySecurityManagers")
    public Y9Page<Y9logAccessLog> pageBySecurityManagers(String userId, Y9PageQuery pageQuery, String sort) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SECURITY_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SECURITY_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.pageByTenantIdAndManagerLevelAndUserId(tenantId,
            String.valueOf(managerLevel), userId, pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取安全保密员日志分页列表成功");
    }

    /**
     * 查看系统管理员日志
     *
     * @param userId 人员id
     * @param pageQuery 分页信息
     * @param sort 排序字段
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看系统管理员日志")
    @RequestMapping(value = "/pageBySystemManagers")
    public Y9Page<Y9logAccessLog> pageBySystemManagers(String userId, Y9PageQuery pageQuery, String sort) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SYSTEM_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.pageByTenantIdAndManagerLevelAndUserId(tenantId,
            String.valueOf(managerLevel), userId, pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取系统管理员日志分页列表成功");
    }

    /**
     * 查看用户日志
     *
     * @param userId 人员id
     * @param pageQuery 分页信息
     * @param sort 排序字段
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看用户日志")
    @RequestMapping(value = "/pageByUsers")
    public Y9Page<Y9logAccessLog> pageByUsers(String userId, Y9PageQuery pageQuery, String sort) {
        Page<Y9logAccessLog> pageList = logService.pageByTenantIdAndManagerLevelAndUserId(
            Y9LoginUserHolder.getTenantId(), String.valueOf(ManagerLevelEnum.GENERAL_USER.getValue()), userId,
            pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取用户日志分页列表成功");
    }

    /**
     * 获取日志分页列表
     *
     * @param pageQuery 搜索信息
     * @param sort 排序字段
     * @return
     */
    @RequestMapping(value = "/pageLogInfo")
    public Y9Page<Y9logAccessLog> pageLogInfo(Y9PageQuery pageQuery, String sort) {
        Page<Y9logAccessLog> pageList = logService.page(pageQuery.getPage(), pageQuery.getSize(), sort);
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
     * @return
     */
    @RequestMapping(value = "/pageSreachList")
    public Y9Page<Y9logAccessLog> pageSreachList(LogInfoModel searchDto, Y9PageQuery pageQuery,
        @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        Page<Y9logAccessLog> resultPage =
            logService.pageSearchByCondition(searchDto, startTime, endTime, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), resultPage.getTotalPages(), resultPage.getTotalElements(),
            resultPage.getContent());
    }

    /**
     * 查询安全审计员日志
     *
     * @param loginInfoModel 搜索信息
     * @param pageQuery 分页信息
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询安全审计员日志")
    @RequestMapping(value = "/searchAuditManagers")
    public Y9Page<Y9logAccessLog> searchAuditManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.AUDIT_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_AUDIT_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.searchQuery(tenantId, String.valueOf(managerLevel), loginInfoModel,
            pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取安全审计员搜索分页列表成功");
    }

    /**
     * 查询安全保密员日志
     *
     * @param loginInfoModel 搜索信息
     * @param pageQuery 分页信息
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询安全保密员日志")
    @RequestMapping(value = "/searchSecurityManagers")
    public Y9Page<Y9logAccessLog> searchSecurityManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SECURITY_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SECURITY_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.searchQuery(tenantId, String.valueOf(managerLevel), loginInfoModel,
            pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取安全保密员日志搜索分页列表成功");
    }

    /**
     * 查询系统管理员日志
     *
     * @param loginInfoModel 搜索信息
     * @param pageQuery 分页信息
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询系统管理员日志")
    @RequestMapping(value = "/searchSystemManagers")
    public Y9Page<Y9logAccessLog> searchSystemManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SYSTEM_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.getValue();
        }
        Page<Y9logAccessLog> pageList = logService.searchQuery(tenantId, String.valueOf(managerLevel), loginInfoModel,
            pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取系统管理员日志搜索分页列表成功");
    }

    /**
     * 查询用户日志
     *
     * @param loginInfoModel 搜索信息
     * @param pageQuery 分页信息
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询用户日志")
    @RequestMapping(value = "/searchUsers")
    public Y9Page<Y9logAccessLog> searchUsers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        Page<Y9logAccessLog> pageList = logService.searchQuery(Y9LoginUserHolder.getTenantId(),
            String.valueOf(ManagerLevelEnum.GENERAL_USER.getValue()), loginInfoModel, pageQuery.getPage(),
            pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取用户日志搜索分页列表成功");
    }

}