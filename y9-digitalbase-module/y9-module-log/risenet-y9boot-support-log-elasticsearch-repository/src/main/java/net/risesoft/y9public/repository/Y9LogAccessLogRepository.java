package net.risesoft.y9public.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogAccessLog;

public interface Y9LogAccessLogRepository extends ElasticsearchRepository<Y9LogAccessLog, String> {

}