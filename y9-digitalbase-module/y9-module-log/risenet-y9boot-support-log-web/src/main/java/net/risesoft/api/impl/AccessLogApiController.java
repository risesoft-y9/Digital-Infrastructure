package net.risesoft.api.impl;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.AccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
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
        Y9LogAccessLogDO y9LogAccessLogDO = Y9ModelConvertUtil.convert(accessLog, Y9LogAccessLogDO.class);
        accessLog.setLogTime(new Date());
        accessLogService.save(y9LogAccessLogDO);
        return Y9Result.success();
    }

    /**
     * 多条件分页查询访问日志
     *
     * @param accessLogQuery 查询条件
     * @param pageQuery 分页条件
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    public Y9Page<AccessLog> search(AccessLogQuery accessLogQuery, Y9PageQuery pageQuery) {
        return accessLogService.pageSearchByCondition(accessLogQuery, pageQuery);
    }

}
