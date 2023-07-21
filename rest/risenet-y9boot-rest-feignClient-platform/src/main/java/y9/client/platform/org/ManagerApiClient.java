package y9.client.platform.org;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.ManagerApi;
import net.risesoft.model.Manager;

/**
 * 三员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "ManagerApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/manager")
public interface ManagerApiClient extends ManagerApi {

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param userId 人员唯一标识
     * @return Manager 人员对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getManager")
    Manager getManager(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 判断是否为该部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 人员唯一标识
     * @param deptId 三员唯一标识
     * @return boolean 是否为该部门的三员
     * @since 9.6.0
     */
    @Override
    @GetMapping("/isDeptManager")
    boolean isDeptManager(@RequestParam("tenantId") String tenantId, @RequestParam("managerId") String managerId, @RequestParam("deptId") String deptId);

}
