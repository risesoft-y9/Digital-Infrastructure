package y9.client.platform.system;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.resource.SystemApi;
import net.risesoft.model.System;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "SystemApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/system")
public interface SystemApiClient extends SystemApi {

    /**
     * 根据系统唯一标识获取系统
     *
     * @param id 系统名称
     * @return System 系统信息
     * @since 9.6.2
     */
    @Override
    @GetMapping("/getById")
    System getById(@RequestParam("id") String id);

    /**
     * 根据系统名称获取系统
     *
     * @param name 系统名称
     * @return System 系统信息
     */
    @Override
    @GetMapping("/getByName")
    System getByName(@RequestParam("name") String name);

    /**
     * 根据系统id，获取系统名称列表
     *
     * @param systemIds 系统id集合（List&lt;String&lt;）
     * @return List&lt;String&gt; 系统名称列表
     */
    @Override
    @GetMapping("/listSystemNameByIds")
    List<String> listSystemNameByIds(@RequestParam("systemIds") List<String> systemIds);
}
