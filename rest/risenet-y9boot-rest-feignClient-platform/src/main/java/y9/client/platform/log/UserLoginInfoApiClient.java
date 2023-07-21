package y9.client.platform.log;

import java.util.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.pojo.Y9Page;

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
     * 获取个人使用的ip和登录次数列表
     *
     * @param personId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List&lt;Object[]&gt; ip和登录次数列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listDistinctUserHostIpByUserIdAndLoginTime")
    List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(@RequestParam("personId") String personId, @RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime);

    /**
     * 获取个人日志列表
     *
     * @param userHostIp 用户IP
     * @param personId 用户id
     * @param tenantId 租户id
     * @param success 是否成功
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param rows 显示条数
     * @return Y9Page&lt;LoginInfo&gt; 返回值包含：currpage、total、totalpages、rows（LoginInfo列表）
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageByUserIdAndLoginTime")
    Y9Page<LoginInfo> pageByUserIdAndLoginTime(@RequestParam(value = "userHostIp", required = false) String userHostIp, @RequestParam("personId") String personId, @RequestParam("tenantId") String tenantId, @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取个人日志列表
     *
     * @param userHostIp 用户IP
     * @param personId 用户id
     * @param tenantId 租户id
     * @param success 是否成功
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param rows 显示条数
     * @return Y9Page&lt;LoginInfo&gt; 返回值包含：currpage、total、totalpages、rows（LoginInfo列表）
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageSearch")
    Y9Page<LoginInfo> pageSearch(@RequestParam(value = "userHostIp", required = false) String userHostIp, @RequestParam("personId") String personId, @RequestParam("tenantId") String tenantId, @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") int page, @RequestParam("rows") int rows);

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
