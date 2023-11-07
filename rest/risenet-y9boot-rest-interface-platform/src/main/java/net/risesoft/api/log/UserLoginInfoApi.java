package net.risesoft.api.log;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 个人登录日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@Validated
public interface UserLoginInfoApi {

    /**
     * 根据租户id和人员id，获取最新登录信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code LoginInfo} 通用请求返回对象 - data 是登录信息
     * @since 9.6.0
     */
    @GetMapping("/getTopByTenantIdAndUserId")
    LoginInfo getTopByTenantIdAndUserId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取登录日志列表
     *
     * @param userHostIp 用户IP
     * @param personId 用户id
     * @param tenantId 租户id
     * @param success 是否成功
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param rows 显示条数
     * @return {@code Y9Page<LoginInfo>} 通用分页请求返回对象 - data 是登录日志集合
     * @since 9.6.0
     */
    @GetMapping("/pageSearch")
    Y9Page<LoginInfo> pageSearch(@RequestParam(value = "userHostIp", required = false) String userHostIp, @RequestParam("personId") String personId, @RequestParam("tenantId") String tenantId, @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 保存登录信息
     *
     * @param info 用户登录信息
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/saveLoginInfo")
    Y9Result<Object> saveLoginInfo(@RequestBody LoginInfo info);

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/saveLoginInfoAsync")
    Y9Result<Object> saveLoginInfoAsync(@RequestBody LoginInfo info);

}
