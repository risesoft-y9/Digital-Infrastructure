package net.risesoft.api.log;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.AccessLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 访问日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@Validated
public interface AccessLogApi {

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/asyncSaveLog")
    Y9Result<Object> asyncSaveLog(@RequestBody AccessLog accessLog);

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
    Y9Page<AccessLog> pageByOperateType(@RequestParam("operateType") @NotBlank String operateType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    @GetMapping("/pageByOrgType")
    Y9Page<AccessLog> pageByOrgType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgId") @NotBlank String orgId, @RequestParam("orgType") @NotBlank String orgType,
        @RequestParam("operateType") @NotBlank String operateType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 保存日志 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/saveLog")
    Y9Result<Object> saveLog(@RequestBody AccessLog accessLog);

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
    @GetMapping("/search")
    Y9Page<AccessLog> search(@RequestParam(value = "logLevel", required = false) String logLevel,
        @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "operateType", required = false) String operateType,
        @RequestParam(value = "operateName", required = false) String operateName,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "userHostIp", required = false) String userHostIp,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

}