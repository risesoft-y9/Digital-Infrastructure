package net.risesoft.api.log;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.pojo.Y9Page;

/**
 * 个人登录日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
public interface UserLoginInfoApi {
    /**
     * 获取个人使用的ip和登录次数列表
     *
     * @param personId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List&lt;Object[]&gt; ip和登录次数列表
     * @since 9.6.0
     */
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
    @PostMapping("/saveLoginInfo")
    boolean saveLoginInfo(LoginInfo info);

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @PostMapping("/saveLoginInfoAsync")
    void saveLoginInfoAsync(LoginInfo info);

}
