package net.risesoft.log.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9logAccessLog;

public interface Y9logAccessLogRepository extends ElasticsearchRepository<Y9logAccessLog, String> {

}