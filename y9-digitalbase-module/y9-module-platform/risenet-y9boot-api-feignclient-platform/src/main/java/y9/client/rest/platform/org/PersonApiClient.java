package y9.client.rest.platform.org;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PersonQuery;

/**
 * 人员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/person",
    primary = false)
public interface PersonApiClient extends PersonApi {

    @Override
    @GetMapping("/page")
    Y9Page<Person> page(@RequestParam("tenantId") String tenantId, @SpringQueryMap PersonQuery personQuery,
        @SpringQueryMap Y9PageQuery pageQuery);

}
