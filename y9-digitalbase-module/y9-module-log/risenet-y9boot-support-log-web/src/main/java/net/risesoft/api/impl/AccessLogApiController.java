package net.risesoft.api.impl;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.Y9logAccessLogService;

import cn.hutool.core.thread.ThreadFactoryBuilder;

/**
 * 访问日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@RestController
@RequestMapping(value = "/services/rest/v1/accessLog", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class AccessLogApiController implements AccessLogApi {

    private final Y9logAccessLogService accessLogService;

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @RiseLog(enable = false)
    @PostMapping("/asyncSaveLog")
    @Override
    public Y9Result<Object> asyncSaveLog(@RequestBody AccessLog accessLog) {
        ThreadFactory myThread = new ThreadFactoryBuilder().setNamePrefix("y9-asyncSaveLog").build();
        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(2, 2, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5), myThread);
        threadPool.execute(() -> saveLog(accessLog));
        threadPool.shutdown();
        return Y9Result.success();
    }

    /**
     * 根据操作类型分页查找访问日志
     *
     * @param operateType 操作类型
     * @param page 页码数
     * @param rows 每页条数
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @GetMapping("/pageByOperateType")
    @Override
    public Y9Page<AccessLog> pageByOperateType(@RequestParam("operateType") @NotBlank String operateType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) {
        return accessLogService.pageByOperateType(operateType, page, rows);
    }

    /**
     * 根据组织架构类型分页查找访问日志
     *
     * @param tenantId 组织id
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param operateType 操作类型
     * @param page 页码树
     * @param rows 每页条数
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageByOrgType")
    public Y9Page<AccessLog> pageByOrgType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgId") @NotBlank String orgId, @RequestParam("orgType") @NotBlank String orgType,
        @RequestParam("operateType") @NotBlank String operateType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return accessLogService.pageByOrgType(tenantId, orgId, orgType, operateType, page, rows);
    }

    /**
     * 保存日志 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @RiseLog(enable = false)
    @Override
    @PostMapping("/saveLog")
    public Y9Result<Object> saveLog(@RequestBody AccessLog accessLog) {
        String accessLogJson = Y9JsonUtil.writeValueAsString(accessLog);
        Y9LogAccessLogDO y9LogAccessLogDO = Y9JsonUtil.readValue(accessLogJson, Y9LogAccessLogDO.class);
        accessLog.setLogTime(new Date());
        accessLogService.save(y9LogAccessLogDO);
        return Y9Result.success();
    }

    /**
     * 多条件分页查询访问日志
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
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    public Y9Page<AccessLog> search(@RequestParam(value = "logLevel", required = false) String logLevel,
        @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "operateType", required = false) String operateType,
        @RequestParam(value = "operateName", required = false) String operateName,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "userHostIp", required = false) String userHostIp,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) {
        LogInfoModel search = new LogInfoModel();
        search.setLogLevel(logLevel);
        search.setOperateName(operateName);
        search.setSuccess(success);
        search.setOperateType(operateType);
        search.setUserName(userName);
        search.setUserHostIp(userHostIp);
        return accessLogService.pageByCondition(search, startTime, endTime, page, rows);
    }

}
