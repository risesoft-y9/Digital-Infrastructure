package net.risesoft.y9public.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9logAccessLog;

public interface Y9logAccessLogRepository extends ElasticsearchRepository<Y9logAccessLog, String> {

}