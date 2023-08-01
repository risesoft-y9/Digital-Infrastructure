package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.model.userlogininfo.LoginInfo;

/**
 * 个人登陆日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "UserLoginInfoApiClient", name = "UserLoginInfo", url = "${y9.common.logBaseUrl}", path = "/services/rest/log")
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
    boolean saveLoginInfo(@SpringQueryMap LoginInfo info);

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLoginInfoAsync")
    void saveLoginInfoAsync(@SpringQueryMap LoginInfo info);

}
