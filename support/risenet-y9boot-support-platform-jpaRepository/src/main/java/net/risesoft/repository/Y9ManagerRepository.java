package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Manager;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9ManagerRepository extends JpaRepository<Y9Manager, String> {

    List<Y9Manager> findByLoginName(String loginName);

    List<Y9Manager> findByNameContainingAndGlobalManagerFalse(String name);

    List<Y9Manager> findByParentIdOrderByTabIndex(String parentId);

    Optional<Y9Manager> findTopByParentIdOrderByTabIndexDesc(String parentId);

    List<Y9Manager> findByGlobalManager(boolean globalManager);

    List<Y9Manager> findByNameContainingAndDnContaining(String name, String dnName);
}
