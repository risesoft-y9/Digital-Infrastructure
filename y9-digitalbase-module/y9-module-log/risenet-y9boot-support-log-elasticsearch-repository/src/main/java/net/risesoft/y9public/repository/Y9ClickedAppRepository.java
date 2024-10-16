package net.risesoft.y9public.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9ClickedApp;

public interface Y9ClickedAppRepository extends ElasticsearchRepository<Y9ClickedApp, String> {

}
