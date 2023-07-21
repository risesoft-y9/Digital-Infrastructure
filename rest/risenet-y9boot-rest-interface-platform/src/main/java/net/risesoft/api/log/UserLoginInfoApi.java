package net.risesoft.api.log;

import java.util.Date;
import java.util.List;

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
    List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String personId, Date startTime, Date endTime);

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
     * @return Y9Page&lt;LoginInfo&gt; LoginInfo分页列表
     * @since 9.6.0
     */
    Y9Page<LoginInfo> pageByUserIdAndLoginTime(String userHostIp, String personId, String tenantId, String success, String startTime, String endTime, int page, int rows);

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
     * @return Y9Page&lt;LoginInfo&gt; LoginInfo分页列表
     * @since 9.6.0
     */
    Y9Page<LoginInfo> pageSearch(String userHostIp, String personId, String tenantId, String success, String startTime, String endTime, int page, int rows);

    /**
     * 保存登录信息
     *
     * @param info 用户登录信息
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    boolean saveLoginInfo(LoginInfo info);

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @since 9.6.0
     */
    void saveLoginInfoAsync(LoginInfo info);

}
