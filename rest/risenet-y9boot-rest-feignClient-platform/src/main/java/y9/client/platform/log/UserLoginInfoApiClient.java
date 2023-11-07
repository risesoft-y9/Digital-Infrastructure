package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.pojo.Y9Result;

/**
 * 个人登陆日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "UserLoginInfoApiClient", name = "UserLoginInfo", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/v1/log")
public interface UserLoginInfoApiClient extends UserLoginInfoApi {

    /**
     * 保存登录信息
     *
     * @param info 用户登录信息
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLoginInfo")
    Y9Result<Object> saveLoginInfo(@SpringQueryMap LoginInfo info);

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLoginInfoAsync")
    Y9Result<Object> saveLoginInfoAsync(@SpringQueryMap LoginInfo info);

}
