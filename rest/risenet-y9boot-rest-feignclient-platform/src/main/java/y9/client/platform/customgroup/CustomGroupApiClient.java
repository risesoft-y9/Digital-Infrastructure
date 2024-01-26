package y9.client.platform.customgroup;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.customgroup.CustomGroupApi;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "CustomGroupApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/v1/customGroup", primary = false)
public interface CustomGroupApiClient extends CustomGroupApi {

    @Override
    @GetMapping("/pageCustomGroupByPersonId")
    Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") String personId, @SpringQueryMap Y9PageQuery pageQuery);

    @Override
    @GetMapping("/pageCustomGroupMemberByGroupId")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") String tenantId,
        @RequestParam("groupId") String groupId, @SpringQueryMap Y9PageQuery pageQuery);

    @Override
    @GetMapping("/pageCustomGroupMemberByGroupIdAndMemberType")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(@RequestParam("tenantId") String tenantId,
        @RequestParam("groupId") String groupId, @RequestParam("memberType") OrgTypeEnum memberType,
        @SpringQueryMap Y9PageQuery pageQuery);
}