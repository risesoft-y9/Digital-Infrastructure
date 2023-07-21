package net.risesoft.log.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9logUserLoginInfo;

public interface Y9logUserLoginInfoRepository extends ElasticsearchRepository<Y9logUserLoginInfo, String> {
    public long countByUserHostIpAndSuccess(String userHostIp, String success);

    public long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName);

    public Page<Y9logUserLoginInfo> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId, Pageable pageable);

    public Page<Y9logUserLoginInfo> findByTenantIdAndManagerLevel(String tenantId, String managerLevel, Pageable pageable);

    public List<Y9logUserLoginInfo> findByUserId(String userId);

    public Set<Y9logUserLoginInfo> findByUserIdAndSuccess(String userId, String success);

}
