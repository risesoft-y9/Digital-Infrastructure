package net.risesoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Organization;

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
public interface Y9OrganizationRepository extends JpaRepository<Y9Organization, String> {

    List<Y9Organization> findByOrderByTabIndexAsc();

    List<Y9Organization> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Organization> findByNameContainingAndDnContainingOrderByTabIndexAsc(String name, String dnName);

    List<Y9Organization> findByTenantIdOrderByTabIndexAsc(String tenantId);

    List<Y9Organization> findByVirtualOrderByTabIndexAsc(Boolean virtual);

    Y9Organization findTopByOrderByTabIndexDesc();

    List<Y9Organization> findByDn(String dn);

    @Query("select id from Y9Organization where guidPath like ?1")
    List<String> listByGuidPathLike(String guidPath);

}
