package net.risesoft.service;

import org.springframework.data.domain.Page;

import net.risesoft.elastic.entity.User;

/**
 * 描述：人员 服务接口
 */
public interface UserService {

    User findById(String id);

    void deleteById(String id);

    User saveOrUpdate(User user);

    User save(User user);

    Iterable<User> findAll();

    User findTopByOrderByIdDesc();

    Page<User> search(String name, String mobile, Integer page, Integer rows);
}