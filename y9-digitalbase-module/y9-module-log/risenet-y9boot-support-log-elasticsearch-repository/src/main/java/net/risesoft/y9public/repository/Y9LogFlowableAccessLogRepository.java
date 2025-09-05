package net.risesoft.y9public.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogFlowableAccessLog;

/**
 * @author qinman
 * @date 2025/05/22
 */
public interface Y9LogFlowableAccessLogRepository extends ElasticsearchRepository<Y9LogFlowableAccessLog, String> {

}