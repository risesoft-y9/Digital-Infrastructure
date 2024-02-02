package y9.client.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 个人登陆日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "UserLoginInfoApiClient", name = "UserLoginInfo", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/v1/userLoginInfo")
public interface UserLoginInfoApiClient extends UserLoginInfoApi {

    @Override
    @GetMapping("/pageSearch")
    Y9Page<LoginInfo> pageSearch(@RequestParam(value = "userHostIp", required = false) String userHostIp,
        @RequestParam("personId") String personId, @RequestParam("tenantId") String tenantId,
        @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, @SpringQueryMap Y9PageQuery pageQuery);
}
