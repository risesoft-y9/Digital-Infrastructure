package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogIpDeptMapping;

public interface Y9LogIpDeptMappingRepository extends ElasticsearchRepository<Y9LogIpDeptMapping, String> {
    List<Y9LogIpDeptMapping> findByClientIpSection(String clientIpSection);

    List<Y9LogIpDeptMapping> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection);

    List<Y9LogIpDeptMapping> findByTenantId(String tenantId, Sort sort);
}
