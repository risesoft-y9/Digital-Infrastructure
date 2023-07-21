package y9.client.platform.log;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.model.AccessLog;
import net.risesoft.pojo.Y9Page;

/**
 * 访问日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "AccessLogApiClient", name = "log", url = "${y9.common.logBaseUrl}", path = "/services/rest/accessLog")
public interface AccessLogApiClient extends AccessLogApi {

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @since 9.6.0
     */
    @PostMapping("/asyncSaveLog")
    @Override
    void asyncSaveLog(@SpringQueryMap AccessLog accessLog);

    /**
     * 异步保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     * @since 9.6.0
     */
    @PostMapping("/asyncSaveLogByJson")
    @Override
    void asyncSaveLogByJson(@RequestParam("accessLogJson") String accessLogJson);

    /**
     * 获取模块访问次数
     *
     * @param orgId 机构id
     * @param orgType 机构类型
     * @param tenantId 租户id
     * @param startDay 开始时间
     * @param endDay 结束时间
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getModuleCount")
    Map<String, Object> getModuleCount(@RequestParam("orgId") String orgId, @RequestParam("orgType") String orgType, @RequestParam("tenantId") String tenantId, @RequestParam("startDay") String startDay, @RequestParam("endDay") String endDay);

    /**
     * 根据操作类型分页查找日志
     *
     * @param operateType 操作类型
     * @param page 页码数
     * @param rows 每页条数
     * @return
     * @since 9.6.0
     */
    @GetMapping("/pageByOperateType")
    @Override
    Y9Page<AccessLog> pageByOperateType(@RequestParam("operateType") String operateType, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageByOrgType")
    Y9Page<AccessLog> pageByOrgType(@RequestParam("tenantId") String tenantId, @RequestParam("orgId") String orgId, @RequestParam("orgType") String orgType, @RequestParam("operateType") String operateType, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLog")
    boolean saveLog(@SpringQueryMap AccessLog accessLog);

    /**
     * 保存访问日志
     *
     * @param accessLogJson 访问日志实体Json字符串
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLogByJson")
    boolean saveLogByJson(@RequestParam("accessLogJson") String accessLogJson);

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
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    Y9Page<AccessLog> search(@RequestParam(value = "logLevel", required = false) String logLevel, @RequestParam(value = "success", required = false) String success, @RequestParam(value = "operateType", required = false) String operateType,
        @RequestParam(value = "operateName", required = false) String operateName, @RequestParam(value = "userName", required = false) String userName, @RequestParam(value = "userHostIp", required = false) String userHostIp, @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws ParseException;

    /**
     * 获取日志
     *
     * @param loginName 登录名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tenantId 租户id
     * @return List&lt;String&gt; 返回日志数据
     * @since 9.6.0
     */
    @Override
    @GetMapping("/searchLog")
    List<String> searchLog(@RequestParam("loginName") String loginName, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime, @RequestParam("tenantId") String tenantId);
}