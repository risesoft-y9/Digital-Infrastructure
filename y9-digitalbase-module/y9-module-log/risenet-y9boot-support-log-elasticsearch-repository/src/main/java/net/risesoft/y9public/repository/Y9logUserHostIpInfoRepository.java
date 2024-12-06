package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9logUserHostIpInfo;

public interface Y9logUserHostIpInfoRepository extends ElasticsearchRepository<Y9logUserHostIpInfo, String> {
    List<Y9logUserHostIpInfo> findByClientIpSection(String clientIpSection);

    List<Y9logUserHostIpInfo> findByUserHostIp(String userHostIp);

    List<Y9logUserHostIpInfo> findByUserHostIpStartingWith(String userHostIp);

}
