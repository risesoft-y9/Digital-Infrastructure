package net.risesoft.api.impl;

import jakarta.validation.constraints.NotBlank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.service.Y9logAccessLogService;
import net.risesoft.model.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

import cn.hutool.core.thread.ThreadFactoryBuilder;

/**
 * 访问日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@RestController
@RequestMapping("/services/rest/v1/accessLog")
@Slf4j
@RequiredArgsConstructor
public class AccessLogApiController implements AccessLogApi {

    private final Y9logAccessLogService accessLogService;

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志实体对象
     */
    @PostMapping("/asyncSaveLog")
    @Override
    public void asyncSaveLog(AccessLog accessLog) {
        ThreadFactory myThread = new ThreadFactoryBuilder().setNamePrefix("y9-asyncSaveLog").build();
        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(2, 2, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5), myThread);
        threadPool.execute(() -> saveLog(accessLog));
        threadPool.shutdown();
    }

    /**
     * 异步保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     */
    @PostMapping("/asyncSaveLogByJson")
    @Override
    public void asyncSaveLogByJson(@RequestParam("accessLogJson") @NotBlank String accessLogJson) {
        ThreadFactory myThread = new ThreadFactoryBuilder().setNamePrefix("y9-asyncSaveLogByJson").build();
        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(2, 2, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5), myThread);
        threadPool.execute(() -> saveLogByJson(accessLogJson));
        threadPool.shutdown();
    }

    /**
     * 根据操作类型分页查找日志
     *
     * @param operateType 操作类型
     * @param page 页码数
     * @param rows 每页条数
     * @return
     */
    @GetMapping("/pageByOperateType")
    @Override
    public Y9Page<AccessLog> pageByOperateType(@RequestParam("operateType") @NotBlank String operateType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) {
        Y9Page<AccessLog> map = accessLogService.pageByOperateType(operateType, page, rows);
        return map;
    }

    /**
     * 根据组织架构类型分页查找日志
     *
     * @param tenantId 组织id
     * @param orgId 组织id
     * @param orgType 组织类型
     * @param operateType 操作类型
     * @param page 页码树
     * @param rows 每页条数
     * @return
     */
    @Override
    @GetMapping("/pageByOrgType")
    public Y9Page<AccessLog> pageByOrgType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgId") @NotBlank String orgId, @RequestParam("orgType") @NotBlank String orgType,
        @RequestParam("operateType") @NotBlank String operateType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Page<AccessLog> map = accessLogService.pageByOrgType(tenantId, orgId, orgType, operateType, page, rows);
        return map;
    }

    /**
     * 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveLog")
    public boolean saveLog(AccessLog accessLog) {
        boolean ret = true;
        try {
            String accessLogJson = Y9JsonUtil.writeValueAsString(accessLog);
            Y9logAccessLog y9AccessLog = Y9JsonUtil.readValue(accessLogJson, Y9logAccessLog.class);
            accessLog.setLogTime(new Date());
            accessLogService.save(y9AccessLog);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            ret = false;
        }
        return ret;
    }

    /**
     * 保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveLogByJson")
    public boolean saveLogByJson(@RequestParam("accessLogJson") @NotBlank String accessLogJson) {
        boolean ret = true;
        try {
            Y9logAccessLog accessLog = Y9JsonUtil.readValue(accessLogJson, Y9logAccessLog.class);
            accessLog.setLogTime(new Date());
            accessLogService.save(accessLog);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            ret = false;
        }
        return ret;
    }

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
     * @return
     * @throws ParseException
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
        @RequestParam("rows") Integer rows) throws ParseException {
        LogInfoModel search = new LogInfoModel();
        search.setLogLevel(logLevel);
        search.setOperateName(operateName);
        search.setSuccess(success);
        search.setOperateType(operateType);
        search.setUserName(userName);
        search.setUserHostIp(userHostIp);
        Y9Page<AccessLog> map = accessLogService.pageByCondition(search, startTime, endTime, page, rows);
        return map;
    }

    /**
     * 获取日志
     *
     * @param loginName 登录名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tenantId 租户id
     * @return List&lt;String&gt; 返回日志数据
     */
    @Override
    @GetMapping("/searchLog")
    public List<String> searchLog(@RequestParam("loginName") @NotBlank String loginName,
        @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime,
        @RequestParam("tenantId") String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sTime = "";
        String eTime = "";
        if (startTime != null) {
            Date start = new Date(startTime);
            sTime = sdf.format(start);
        }
        if (endTime != null) {
            Date end = new Date(endTime);
            eTime = sdf.format(end);
        }
        return accessLogService.listAccessLog(sTime, eTime, loginName, tenantId);
    }

}
