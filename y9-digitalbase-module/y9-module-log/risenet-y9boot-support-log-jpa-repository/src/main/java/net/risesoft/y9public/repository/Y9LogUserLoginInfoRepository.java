package net.risesoft.y9public.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9LogUserLoginInfo;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9LogUserLoginInfoRepository
    extends JpaRepository<Y9LogUserLoginInfo, String>, JpaSpecificationExecutor<Y9LogUserLoginInfo> {

    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserNameContaining(String userHostIp, String success, String userName);

    Page<Y9LogUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9LogUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    Page<Y9LogUserLoginInfo> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable);

    Page<Y9LogUserLoginInfo> findByUserHostIpAndSuccess(String userHostIp, String success, Pageable pageable);

    // 模糊查询终端IP下的人员
    @Query("select u.userId,u.userName,u.userHostIp,COUNT(*) from Y9LogUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 and u.userName like %?3% group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success, String userName,
        Pageable pageable);

    @Query("select u.userId,u.userName,u.userHostIp,COUNT(*) from Y9LogUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 and u.userName like %?3% AND tenantId =?4 group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findByUserHostIpAndSuccessAndUserNameLikeAndTenantId(String userHostIp, String success,
        String userName, String tenantId, Pageable pageable);

    List<Y9LogUserLoginInfo> findByUserId(String userId);

    Set<Y9LogUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

    // 获得每个被登陆成功的终端IP下所有人员名称列表
    @Query("select u.userId,u.userName,u.userHostIp,COUNT(u) from Y9LogUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findDistinctByUserHostIpAndSuccess(String userHostIp, String success, Pageable pageable);

    @Query("select u.userId,u.userName,u.userHostIp,COUNT(u) from Y9LogUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 AND tenantId =?3 group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findDistinctByUserHostIpAndSuccessAndTenantId(String userHostIp, String success, String tenantId,
        Pageable pageable);

    @Query("select DISTINCT(u.userHostIp) as userHostIp ,count(*) as count from Y9LogUserLoginInfo u where u.userHostIp LIKE ?1% group by u.userHostIp")
    List<Map<String, Object>> findDistinctByUserHostIpLike(String cip);

    @Query("select DISTINCT(u.userHostIp) as userHostIp ,count(*) as count from Y9LogUserLoginInfo u where u.userHostIp LIKE ?1% AND tenantId =?2 group by u.userHostIp")
    List<Map<String, Object>> findDistinctByUserHostIpLikeAndTenantId(String cip, String tenantId);

    // 根据人员id，获得该人员登陆的所有终端ip和登录次数。
    @Query("select DISTINCT(u.userHostIp) as userHostIp ,count(*) as count from Y9LogUserLoginInfo u where u.userId=?1 and u.loginTime>=?2 and u.loginTime<=?3 group by u.userHostIp")
    List<Map<String, Object>> findDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime);

    /** 根据ip的前三位，获得终端ip的登陆次数。 */
    @Query("select userHostIp as userHostIp , count(userHostIp) as count from Y9LogUserLoginInfo where userHostIp LIKE ?1% group by userHostIp")
    List<Map<String, Object>> findDistinctUserHostIpCountByUserHostIpLike(String cip);

    @Query("select userHostIp as userHostIp , count(userHostIp) as count from Y9LogUserLoginInfo where userHostIp LIKE ?1% AND tenantId =?2 group by userHostIp")
    List<Map<String, Object>> findDistinctUserHostIpCountByUserHostIpLikeAndTenantId(String cip, String tenantId);

    Y9LogUserLoginInfo findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

    Integer countByUserId(String personId);
}
