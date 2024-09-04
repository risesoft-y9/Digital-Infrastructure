package net.risesoft.elastic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.elastic.entity.User;

/**
 * 描述：人员 Repository接口
 */
public interface UserRepository extends ElasticsearchRepository<User, String> {

    Page<User> findByNameContainingAndSexContaining(String name, String sex, Pageable page);

    User findTopByOrderByIdDesc();
}
