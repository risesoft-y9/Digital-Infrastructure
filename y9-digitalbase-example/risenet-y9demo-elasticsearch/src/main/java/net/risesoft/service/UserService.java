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

    /**
     * Criteria查询
     * 
     * @param name
     * @param mobile
     * @param page
     * @param rows
     * @return
     */
    Page<User> search(String name, String mobile, Integer page, Integer rows);

    /**
     * NativeSearchQueryBuilder查询
     * 
     * @param name
     * @param mobile
     * @param page
     * @param rows
     * @return
     */
    Page<User> search2(String name, String mobile, Integer page, Integer rows);
}