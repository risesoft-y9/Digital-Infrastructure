package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Department;

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
public interface Y9DepartmentRepository
    extends JpaRepository<Y9Department, String>, JpaSpecificationExecutor<Y9Department> {

    long countByDisabledAndGuidPathContaining(Boolean disabled, String guidPath);

    List<Y9Department> findByBureauAndDnContainingOrderByTabIndexAsc(Boolean bureau, String dn);

    List<Y9Department> findByBureauAndGuidPathContainingAndDisabledOrderByTabIndexAsc(Boolean bureau,
        String organizationId, Boolean disabled);

    List<Y9Department> findByBureauAndGuidPathContainingOrderByTabIndexAsc(Boolean isBureau, String guidPath);

    List<Y9Department> findByDn(String dn);

    List<Y9Department> findByDnAndDisabled(String dn, Boolean disabled);

    List<Y9Department> findByNameContainingAndDisabledOrderByTabIndexAsc(String name, Boolean disabled);

    List<Y9Department> findByNameContainingAndDnContainingAndDisabledOrderByTabIndexAsc(String name, String dnName,
        Boolean disabled);

    List<Y9Department> findByNameContainingAndDnContainingOrderByTabIndexAsc(String name, String dnName);

    List<Y9Department> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Department> findByParentId(String parentId);

    List<Y9Department> findByParentIdAndDisabled(String orgBaseId, Boolean disabled);

    List<Y9Department> findByParentIdAndDisabledOrderByTabIndexAsc(String parentId, Boolean disabled);

    List<Y9Department> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Department> findTopByParentIdOrderByTabIndexDesc(String parentId);
}
