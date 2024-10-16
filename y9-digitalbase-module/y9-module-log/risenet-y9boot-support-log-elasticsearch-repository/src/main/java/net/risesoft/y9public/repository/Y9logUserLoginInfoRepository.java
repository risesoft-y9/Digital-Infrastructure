package net.risesoft.y9public.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9logUserLoginInfo;

public interface Y9logUserLoginInfoRepository extends ElasticsearchRepository<Y9logUserLoginInfo, String> {
    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserNameContaining(String userHostIp, String success, String userName);

    Page<Y9logUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9logUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    Page<Y9logUserLoginInfo> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable);

    List<Y9logUserLoginInfo> findByUserId(String userId);

    Set<Y9logUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

    Y9logUserLoginInfo findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

}
