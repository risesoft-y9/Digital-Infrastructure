package net.risesoft.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.example.entity.User;

/**
 * 描述：人员 Repository接口
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    User findTopByOrderByIdDesc();
}
