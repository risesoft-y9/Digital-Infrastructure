package net.risesoft.api.impl;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.FlowableAccessLogApi;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.FlowableAccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;

import cn.hutool.core.thread.ThreadFactoryBuilder;

/**
 * 工作流访问日志组件
 *
 * @author qinman
 * @date 2025/05/21
 * @since 9.6.8
 */
@RestController
@RequestMapping(value = "/services/rest/v1/flowable/accessLog", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class FlowableAccessLogApiController implements FlowableAccessLogApi {

    private final Y9logFlowableAccessLogService y9logFlowableAccessLogService;

    /**
     * 异步保存访问日志
     *
     * @param flowableAccessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.8
     */
    @RiseLog(enable = false)
    @PostMapping("/asyncSaveLog")
    @Override
    public Y9Result<Object> asyncSaveLog(@RequestBody FlowableAccessLog flowableAccessLog) {
        ThreadFactory myThread = new ThreadFactoryBuilder().setNamePrefix("y9-asyncSaveLog-flowable").build();
        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(2, 2, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5), myThread);
        threadPool.execute(() -> saveLog(flowableAccessLog));
        threadPool.shutdown();
        return Y9Result.success();
    }

    /**
     * 保存日志 保存访问日志
     *
     * @param flowableAccessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @RiseLog(enable = false)
    @Override
    @PostMapping("/saveLog")
    public Y9Result<Object> saveLog(@RequestBody FlowableAccessLog flowableAccessLog) {
        String accessLogJson = Y9JsonUtil.writeValueAsString(flowableAccessLog);
        Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO =
            Y9JsonUtil.readValue(accessLogJson, Y9LogFlowableAccessLogDO.class);
        y9logFlowableAccessLogService.save(y9LogFlowableAccessLogDO);
        return Y9Result.success();
    }

    /**
     * 多条件分页查询访问日志
     *
     * @param query 查询条件
     * @param page 页码数
     * @param rows 每页条数
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    public Y9Page<FlowableAccessLog> search(FlowableAccessLogQuery query, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) {
        return y9logFlowableAccessLogService.pageByCondition(query, page, rows);
    }

}
