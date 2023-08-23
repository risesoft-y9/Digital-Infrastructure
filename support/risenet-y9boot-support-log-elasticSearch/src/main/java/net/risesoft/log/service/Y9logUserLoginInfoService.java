package net.risesoft.log.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;

/**
 * 登录日志管理
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logUserLoginInfoService {
    long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success);

    long countByPersonId(String personId);

    long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId);

    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName);

    long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success);

    Iterable<Y9logUserLoginInfo> listAll();

    List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime);

    List<Map<String, Object>> listUserHostIpByCip(String cip);

    List<String> listUserHostIpByUserId(String userId, String success);

    Y9Page<Y9logUserLoginInfo> page(String userHostIp, String userId, String success, String startTime, String endTime,
        int page, int rows);

    Page<Y9logUserLoginInfo> pageBySuccessAndServerIpAndUserName(String success, String userHostIp, String userId,
        int page, int rows);

    Page<Y9logUserLoginInfo> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page, int rows);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page, int rows);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows);

    Y9Page<Y9logUserLoginInfo> pageByUserHostIpAndUserIdAndTenantIdAndLoginTime(String hostIp, String personId,
        String tenantId, String success, String startTime, String endTime, int page, int rows);

    void save(Y9logUserLoginInfo y9logUserLoginInfo);

    Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows);

    Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page,
        int rows);

    Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, int page,
        int rows);
}
