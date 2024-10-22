package y9.client.rest.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.resource.DataCatalogApi;

/**
 * 数据目录
 *
 * @author shidaobang
 * @date 2024/10/23
 * @since 9.6.8
 */
@FeignClient(contextId = "DataCatalogApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/dataCatalog",
    primary = false)
public interface DataCatalogApiClient extends DataCatalogApi {}
