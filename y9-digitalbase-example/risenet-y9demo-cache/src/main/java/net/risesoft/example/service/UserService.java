package net.risesoft.example.service;

import java.util.List;

import net.risesoft.example.entity.User;

/**
 * 描述：人员 服务接口
 */
public interface UserService {

    User findById(String id);

    void deleteById(String id);

    User saveOrUpdate(User user);

    User save(User user);

    List<User> findAll();

    User findTopByOrderByIdDesc();
}