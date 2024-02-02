package net.risesoft.log.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9logUserHostIpInfo;

public interface Y9logUserHostIpInfoRepository extends ElasticsearchRepository<Y9logUserHostIpInfo, String> {
    public List<Y9logUserHostIpInfo> findByClientIpSection(String clientIpSection);

    public List<Y9logUserHostIpInfo> findByUserHostIp(String userHostIp);

    public List<Y9logUserHostIpInfo> findByUserHostIpStartingWith(String userHostIp);

}
