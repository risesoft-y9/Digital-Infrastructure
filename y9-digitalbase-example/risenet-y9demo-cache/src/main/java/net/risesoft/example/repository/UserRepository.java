package net.risesoft.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.example.entity.User;

/**
 * 描述：人员 Repository接口
 */
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    @Query("from User p where p.name like ?1 and p.sex like ?2")
    Page<User> findAll(String name, String sex, Pageable page);

    User findTopByOrderByIdDesc();

}
