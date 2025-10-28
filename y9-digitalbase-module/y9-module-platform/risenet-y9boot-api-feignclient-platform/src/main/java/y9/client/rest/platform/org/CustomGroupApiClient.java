package y9.client.rest.platform.org;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.CustomGroupApi;
import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.query.platform.CustomGroupMemberQuery;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "CustomGroupApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/customGroup",
    primary = false)
public interface CustomGroupApiClient extends CustomGroupApi {

    @Override
    @GetMapping("/listCustomGroupMember")
    Y9Result<List<CustomGroupMember>> listCustomGroupMember(@RequestParam("tenantId") String tenantId,
        @SpringQueryMap CustomGroupMemberQuery customGroupMemberQuery);

    @Override
    @GetMapping("/pageCustomGroupByPersonId")
    Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") String personId, @SpringQueryMap Y9PageQuery pageQuery);

    @GetMapping("/pageCustomGroupMember")
    Y9Page<CustomGroupMember> pageCustomGroupMember(@RequestParam("tenantId") String tenantId,
        @SpringQueryMap CustomGroupMemberQuery customGroupMemberQuery, @SpringQueryMap Y9PageQuery pageQuery);
}