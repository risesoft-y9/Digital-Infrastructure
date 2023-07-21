package net.risesoft.log.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.log.entity.Y9CommonAppForPerson;

public interface Y9CommonAppForPersonRepository extends ElasticsearchRepository<Y9CommonAppForPerson, String> {

    public Y9CommonAppForPerson findByPersonId(String personId);
}
