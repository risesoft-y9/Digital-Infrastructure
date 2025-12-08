package net.risesoft.api.log;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.AccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
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
     * @param accessLogQuery 查询条件
     * @param pageQuery 分页查询
     * @return {@code Y9Page<AccessLog>} 通用分页请求返回对象 - data 是访问日志集合
     * @since 9.6.0
     */
    @GetMapping("/search")
    Y9Page<AccessLog> search(AccessLogQuery accessLogQuery, Y9PageQuery pageQuery);

}