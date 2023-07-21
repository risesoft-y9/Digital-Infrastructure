package net.risesoft.log.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9ClickedApp;

public interface Y9ClickedAppRepository extends ElasticsearchRepository<Y9ClickedApp, String> {

}
