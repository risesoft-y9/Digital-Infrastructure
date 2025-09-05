package net.risesoft.y9public.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogUserLoginInfo;

public interface Y9LogUserLoginInfoRepository extends ElasticsearchRepository<Y9LogUserLoginInfo, String> {
    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserNameContaining(String userHostIp, String success, String userName);

    Page<Y9LogUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9LogUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    Page<Y9LogUserLoginInfo> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable);

    List<Y9LogUserLoginInfo> findByUserId(String userId);

    Set<Y9LogUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

    Y9LogUserLoginInfo findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

    Integer countByUserId(String personId);
}
