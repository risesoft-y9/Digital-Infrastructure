package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9logIpDeptMapping;

public interface Y9logIpDeptMappingRepository extends ElasticsearchRepository<Y9logIpDeptMapping, String> {
    public List<Y9logIpDeptMapping> findByClientIpSection(String clientIpSection);

}
