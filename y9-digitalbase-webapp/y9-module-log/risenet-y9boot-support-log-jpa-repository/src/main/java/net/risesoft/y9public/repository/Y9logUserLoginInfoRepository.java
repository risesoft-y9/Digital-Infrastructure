package net.risesoft.y9public.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9logUserLoginInfo;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9logUserLoginInfoRepository
    extends JpaRepository<Y9logUserLoginInfo, String>, JpaSpecificationExecutor<Y9logUserLoginInfo> {
    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserNameContaining(String userHostIp, String success, String userName);

    Page<Y9logUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9logUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    Page<Y9logUserLoginInfo> findByUserHostIpAndSuccess(String userHostIp, String success, Pageable pageable);

    // 模糊查询终端IP下的人员
    @Query("select u.userId,u.userName,u.userHostIp,COUNT(*) from Y9logUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 and u.userName like %?3% group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success, String userName,
        Pageable pageable);

    List<Y9logUserLoginInfo> findByUserId(String userId);

    Set<Y9logUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

    // 获得每个被登陆成功的终端IP下所有人员名称列表
    @Query("select u.userId,u.userName,u.userHostIp,COUNT(u) from Y9logUserLoginInfo u where u.userHostIp = ?1 and u.success = ?2 group by u.userId,u.userName,u.userHostIp")
    Page<Object[]> findDistinctByUserHostIpAndSuccess(String userHostIp, String success, Pageable pageable);

    // 获得该人员登陆此终端ip的次数。
    @Query("select DISTINCT(u.userHostIp),count(*) from Y9logUserLoginInfo u where u.userHostIp like ?1% group by u.userHostIp")
    List<Object[]> findDistinctByUserHostIpLike(String userHostIp);

    // 根据人员id，获得该人员登陆的所有终端ip和登录次数。
    @Query("select DISTINCT(u.userHostIp),count(*) from Y9logUserLoginInfo u where u.userId=?1 and u.loginTime>=?2 and u.loginTime<=?3 group by u.userHostIp")
    List<Object[]> findDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime);

    Y9logUserLoginInfo findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

}
