package net.risesoft.log.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9logUserLoginInfo;

public interface Y9logUserLoginInfoRepository extends ElasticsearchRepository<Y9logUserLoginInfo, String> {
    long countByUserHostIpAndSuccess(String userHostIp, String success);

    long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName);

    Page<Y9logUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId,
        Pageable pageable);

    Page<Y9logUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    List<Y9logUserLoginInfo> findByUserId(String userId);

    Set<Y9logUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

    Y9logUserLoginInfo findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId);

}
