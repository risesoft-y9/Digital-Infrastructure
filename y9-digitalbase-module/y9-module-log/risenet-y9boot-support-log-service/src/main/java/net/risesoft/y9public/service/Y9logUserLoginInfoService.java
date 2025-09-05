package net.risesoft.y9public.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

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
     * @return long
     */
    long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success);

    /**
     * 根据人员id，获取该人员登录次数
     *
     * @param personId 人员唯一标识
     * @return Integer
     */
    Integer countByPersonId(String personId);

    /**
     * 根据userId和userHostIP查询人员登录成功的次数
     *
     * @param success 登录是否成功
     * @param userHostIp 登录人员机器IP
     * @param userId 人员唯一标识
     * @return long
     */
    long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId);

    /**
     * 获取一定时间段内的特点IP段登录人数数据
     *
     * @param userHostIp 登录人员机器IP
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @return long
     */
    long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success);

    /**
     * 根据租户id和人员id，获取最新登录信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9logUserLoginInfo
     */
    Y9LogUserLoginInfoDO getTopByTenantIdAndUserId(String tenantId, String userId);

    /**
     * 获取全部登录信息
     *
     * @return {@code Iterable<Y9logUserLoginInfo>}
     */
    List<Y9LogUserLoginInfoDO> listAll();

    /**
     * 根据C类IP段，获取属于该IP段的IP地址和登录次数
     *
     * @param cip C类IP段
     * @return {@code List<Map<String, Object>>}
     */
    List<Map<String, Object>> listUserHostIpByCip(String cip);

    /**
     * 根据人员id，获取所有登陆成功的终端ip
     *
     * @param userId 人员id
     * @param success 登录是否成功
     * @return {@code List<String>}
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
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    Y9Page<Y9LogUserLoginInfoDO> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery);

    /**
     * 获取特定时间段内的登录成功信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    Y9Page<Y9LogUserLoginInfoDO> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success,
        int page, int rows);

    /**
     * 根据终端IP和人员获取人员的详细分页信息
     *
     * @param success 登录是否成功
     * @param userHostIp 登录人员机器IP
     * @param userId 人员id
     * @param page 页数
     * @param rows 条数
     * @return {@code  Page<Y9logUserLoginInfo>}
     */
    Page<Y9LogUserLoginInfoDO> pageBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        int page, int rows);

    /**
     * 根据租户id和三元级别，获取登录详情
     *
     * @param tenantId 租户id
     * @param managerLevel 三员级别
     * @param page 页数
     * @param rows 条数
     * @return {@code  Page<Y9logUserLoginInfo>}
     */
    Page<Y9LogUserLoginInfoDO> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page, int rows);

    /**
     * 根据IP，获取登录分页详情
     *
     * @param userHostIp 登录人员机器IP
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<Map<String, Object>>}
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
     * @return {@code Y9Page<Map<String, Object>>}
     */
    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows);

    /**
     * 根据终端C段IP和时间段查询出该时间段终端IP的登录详情
     *
     * @param userHostIp 登录人员机器IP
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 登录是否成功
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    Y9Page<Y9LogUserLoginInfoDO> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime,
        Date endTime, String success, int page, int rows);

    /**
     * 保存用户登录信息
     * 
     * @param y9LogUserLoginInfoDO 用户登录详情
     */
    void save(Y9LogUserLoginInfoDO y9LogUserLoginInfoDO);

    /**
     * 根据条件查询，返回相应的管理员登录日志
     *
     * @param tenantId 租户id
     * @param managerLevel 三员级别
     * @param loginInfoModel 搜索详情
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows);
}
