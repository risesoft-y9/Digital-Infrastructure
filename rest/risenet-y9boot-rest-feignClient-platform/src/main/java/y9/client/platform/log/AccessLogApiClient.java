package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.model.AccessLog;

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
     * 保存访问日志
     *
     * @param accessLog 访问日志实体对象
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLog")
    boolean saveLog(@SpringQueryMap AccessLog accessLog);
}