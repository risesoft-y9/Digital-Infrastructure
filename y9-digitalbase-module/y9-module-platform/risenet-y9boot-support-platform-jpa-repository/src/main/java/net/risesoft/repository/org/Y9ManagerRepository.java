package net.risesoft.repository.org;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.org.Y9Manager;

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

    boolean existsByLoginName(String loginName);

    List<Y9Manager> findByGlobalManager(Boolean globalManager);

    Optional<Y9Manager> findByLoginName(String loginName);

    List<Y9Manager> findByNameContainingAndDnContaining(String name, String dnName);

    List<Y9Manager> findByNameContainingAndDnContainingAndDisabled(String name, String dnName, Boolean disabled);

    List<Y9Manager> findByNameContainingAndGlobalManagerFalse(String name);

    List<Y9Manager> findByNameContainingAndGlobalManagerFalseAndDisabled(String name, Boolean disabled);

    List<Y9Manager> findByParentIdOrderByTabIndex(String parentId);

    Optional<Y9Manager> findTopByParentIdOrderByTabIndexDesc(String parentId);
}
