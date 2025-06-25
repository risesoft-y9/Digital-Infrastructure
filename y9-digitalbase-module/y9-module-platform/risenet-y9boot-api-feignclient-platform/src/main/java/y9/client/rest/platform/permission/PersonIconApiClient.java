package y9.client.rest.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.permission.PersonIconApi;
import net.risesoft.model.platform.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 人员图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonIconApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/personIcon")
public interface PersonIconApiClient extends PersonIconApi {

    @Override
    @GetMapping("/pageByOrgUnitId")
    Y9Page<PersonIconItem> pageByOrgUnitId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @SpringQueryMap Y9PageQuery pageQuery);

}
