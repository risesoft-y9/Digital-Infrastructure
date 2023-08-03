package net.risesoft.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Person;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
// @JaversSpringDataAuditable
public interface Y9PersonRepository extends JpaRepository<Y9Person, String> {

    long countByDisabledAndGuidPathContaining(boolean disabled, String guidPath);

    long countByParentId(String parentId);

    Page<Y9Person> findByDisabledAndNameContaining(boolean disabled, String userName, Pageable pageable);

    Page<Y9Person> findByDisabledAndParentId(boolean disabled, String parentId, Pageable pageable);

    List<Y9Person> findByDisabledAndParentIdOrderByTabIndex(boolean disabled, String parentId);

    Y9Person findByDisabledFalseAndMobileAndOriginal(String mobile, Boolean original);

    List<Y9Person> findByDisabledAndGuidPathContaining(boolean disabled, String guidPath);

    List<Y9Person> findByEmailAndOriginal(String email, Boolean original);

    List<Y9Person> findByLoginName(String loginName);

    Y9Person findByLoginNameAndOriginalTrue(String loginName);

    Y9Person findByLoginNameAndParentId(String loginName, String parentId);

    List<Y9Person> findByLoginNameAndTenantIdAndOriginal(String loginName, String tenantId, Boolean original);

    List<Y9Person> findByMobileAndOriginal(String mobile, Boolean original);

    List<Y9Person> findByNameContainingAndDnContaining(String name, String dnName);

    List<Y9Person> findByNameContaining(String name);

    List<Y9Person> findByNameContainingAndDisabled(String name, boolean disable);

    List<Y9Person> findByOriginalId(String originalId);

    List<Y9Person> findByOriginalIdAndDisabled(String originalId, boolean disable);

    Y9Person findByOriginalIdAndParentId(String originalId, String parentId);

    Page<Y9Person> findByParentIdAndDisabledAndNameContaining(String parentId, boolean disabled, String userName,
        Pageable pageable);

    List<Y9Person> findByParentIdAndDisabledAndNameContainingOrderByTabIndex(String parentId, boolean disabled,
        String name);

    List<Y9Person> findByParentIdOrderByTabIndex(String parentId);

    Y9Person findTopByOrderByTabIndexDesc();

    Y9Person findTopByParentIdOrderByTabIndexDesc(String parentId);

    @Query("select id from Y9Person where guidPath like ?1 and disabled = false")
    List<String> getPersonIdByGuidPathLike(String dn);

    @Query("select id from Y9Person where parentId = ?1 and disabled = false")
    List<String> getPersonIdByParentId(String parentId);

}
