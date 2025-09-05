package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogUserHostIpInfo;

public interface Y9LogUserHostIpInfoRepository extends ElasticsearchRepository<Y9LogUserHostIpInfo, String> {
    List<Y9LogUserHostIpInfo> findByClientIpSection(String clientIpSection);

    List<Y9LogUserHostIpInfo> findByUserHostIp(String userHostIp);

    List<Y9LogUserHostIpInfo> findByUserHostIpStartingWith(String userHostIp);

}
