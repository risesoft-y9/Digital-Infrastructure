package net.risesoft.y9public.repository.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
public interface Y9logUserLoginInfoCustomRepository {

    long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success);

    long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId);

    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success);

    List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime);

    List<Map<String, Object>> listUserHostIpByCip(String cip);

    Y9Page<Y9logUserLoginInfo> page(String tenantId, String userHostIp, String userId, String success, String startTime,
        String endTime, Y9PageQuery pageQuery);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page, int rows);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows);

    Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows);

    Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page,
        int rows);

    Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, int page,
        int rows);
}
