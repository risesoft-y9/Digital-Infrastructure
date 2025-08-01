package y9.client.rest.platform.dictionary;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.dictionary.OptionValueApi;

/**
 * 字典表管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "OptionValueApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/optionValue",
    primary = false)
public interface OptionValueApiClient extends OptionValueApi {

}
