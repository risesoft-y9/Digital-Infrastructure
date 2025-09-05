package net.risesoft.log.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
public interface Y9logUserLoginInfoCustomRepository {

    long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success);

    long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId);

    long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success);

    List<Map<String, Object>> listUserHostIpByCip(String cip);

    Y9Page<Y9LogUserLoginInfoDO> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery);

    Y9Page<Y9LogUserLoginInfoDO> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success,
        int page, int rows);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page, int rows);

    Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows);

    Y9Page<Y9LogUserLoginInfoDO> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime,
        Date endTime, String success, int page, int rows);

    Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows);

    Integer countByUserId(String personId);

    Y9LogUserLoginInfoDO findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

    List<Y9LogUserLoginInfoDO> findAll();

    Set<Y9LogUserLoginInfoDO> findByUserIdAndSuccess(String userId, String success);

    Page<Y9LogUserLoginInfoDO> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable);

    Page<Y9LogUserLoginInfoDO> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9LogUserLoginInfoDO> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    void save(Y9LogUserLoginInfoDO y9LogUserLoginInfoDO);
}
