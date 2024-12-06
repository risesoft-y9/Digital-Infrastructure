package net.risesoft.y9public.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.y9public.entity.Y9CommonAppForPerson;

public interface Y9CommonAppForPersonRepository extends ElasticsearchRepository<Y9CommonAppForPerson, String> {

    Y9CommonAppForPerson findByPersonId(String personId);
}
