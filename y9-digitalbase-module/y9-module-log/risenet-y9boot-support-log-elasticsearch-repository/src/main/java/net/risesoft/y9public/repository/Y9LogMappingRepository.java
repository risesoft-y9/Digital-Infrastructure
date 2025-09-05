package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9LogMapping;

public interface Y9LogMappingRepository extends ElasticsearchRepository<Y9LogMapping, String> {

    List<Y9LogMapping> findByModularName(String modularName);
}
