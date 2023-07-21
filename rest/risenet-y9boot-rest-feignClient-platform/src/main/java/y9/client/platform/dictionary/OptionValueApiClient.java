package y9.client.platform.dictionary;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.dictionary.OptionValueApi;
import net.risesoft.model.OptionValue;

/**
 * 字典表管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "OptionValueApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/optionValue")
public interface OptionValueApiClient extends OptionValueApi {

    /**
     * 根据类型查找
     *
     * @param tenantId 租户id
     * @param type 类型
     * @return List&lt;OptionValue&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByType")
    List<OptionValue> listByType(@RequestParam("tenantId") String tenantId, @RequestParam("type") String type);
}
