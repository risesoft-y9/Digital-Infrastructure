package net.risesoft.y9public.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;

/**
 * 登录日志管理
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logUserLoginInfoService {

    /**
     * 获取一定时间段内登录的人数
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return
     */
    long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success);

    /**
     * 根据人员id，获取该人员登录次数
     *
     * @param personId 人员唯一标识
     * @return
     */
    Integer countByPersonId(String personId);

    /**
     * 根据userId和userHostIP查询人员登录成功的次数
     *
     * @param success 登录是否成功
     * @param userHostIp 登录人员机器IP
     * @param userId 人员唯一标识
     * @return
     */
    long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId);

    /**
     * 根据userHostIP,查询人员登录成功的次数
     *
     * @param userHostIp 登录人员机器IP
     * @param success 登录是否成功
     * @return
     */
    long countByUserHostIpAndSuccess(String userHostIp, String success);

    /**
     * 根据userHostIP和人员名称,模糊查询人员登录成功的次数
     *
     * @param userHostIp 登录人员机器IP
     * @param success 登录是否成功
     * @param userName 人员名称
     * @return
     */
    long countByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success, String userName);

    /**
     * 获取一定时间段内的特点IP段登录人数数据
     *
     * @param userHostIp 登录人员机器IP
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @return
     */
    long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success);

    /**
     * 根据租户id和人员id，获取最新登录信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return
     */
    Y9logUserLoginInfo getTopByTenantIdAndUserId(String tenantId, String userId);

    /**
     * 获取全部登录信息
     *
     * @return
     */
    Iterable<Y9logUserLoginInfo> listAll();

    /**
     * 根据人员id，获得特定时间段内该人员登陆的所有终端ip和登录次数。
     *
     * @param userId 人员id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime);

    /**
     * 根据C类IP段，获取属于该IP段的IP地址和登录次数
     *
     * @param cip C类IP段
     * @return
     */
    List<Map<String, Object>> listUserHostIpByCip(String cip);

    /**
     * 根据人员id，获取所有登陆成功的终端ip
     *
     * @param userId 人员id
     * @param success 登录是否成功
     * @return
     */
    List<String> listUserHostIpByUserId(String userId, String success);

    /**
     * 模糊搜索
     *
     * @param tenantId 租户id
     * @param userHostIp 登录人员机器IP
     * @param userId 人员id
     * @param success 登录是否成功
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageQuery
     * @return
     */
    Y9Page<Y9logUserLoginInfo> page(String tenantId, String userHostIp, String userId, String success, String startTime,
        String endTime, Y9PageQuery pageQuery);

    /**
     * 获取特定时间段内的登录成功信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Y9Page<Y9logUserLoginInfo> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success, int page,
        int rows);

    /**
     * 根据终端IP和人员获取人员的详细分页信息
     *
     * @param success 登录是否成功
     * @param userHostIp 登录人员机器IP
     * @param userId 人员id
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Page<Y9logUserLoginInfo> pageBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        int page, int rows);

    /**
     * 根据租户id和三元级别，获取登录详情
     *
     * @param tenantId 租户id
     * @param managerLevel 三员级别
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Page<Y9logUserLoginInfo> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page, int rows);

    /**
     * 根据IP，获取登录分页详情
     *
     * @param userHostIp 登录人员机器IP
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page, int rows);

    /**
     * 模糊查询终端IP下的人员
     *
     * @param userHostIp 登录人员机器IP
     * @param success 登录是否成功
     * @param userName 用户名称
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows);

    /**
     * 根据终端C段IP和时间段查询出该时间段终端IP的登录详情
     *
     * @param userHostIp
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Y9Page<Y9logUserLoginInfo> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime,
        Date endTime, String success, int page, int rows);

    /**
     *
     * @param y9logUserLoginInfo
     */
    void save(Y9logUserLoginInfo y9logUserLoginInfo);

    /**
     * 根据条件查询，返回相应的管理员登录日志
     *
     * @param tenantId 租户id
     * @param managerLevel 三员级别
     * @param loginInfoModel 搜索详情
     * @param page 页数
     * @param rows 条数
     * @return
     */
    Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, int page,
        int rows);
}
