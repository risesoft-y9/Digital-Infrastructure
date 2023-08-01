package y9.client.platform.dictionary;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.dictionary.OptionValueApi;

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

}
