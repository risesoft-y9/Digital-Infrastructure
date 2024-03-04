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

    List<Y9Department> findByBureauAndDnContainingOrderByTabIndexAsc(boolean bureau, String dn);

    List<Y9Department> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Department> findByNameContainingAndDnContainingOrderByTabIndexAsc(String name, String dnName);

    List<Y9Department> findByParentId(String parentId);

    List<Y9Department> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Department> findTopByParentIdOrderByTabIndexDesc(String parentId);

    List<Y9Department> getByDn(String dn);

    List<Y9Department> findByBureauAndGuidPathContainingOrderByTabIndexAsc(boolean isBureau, String guidPath);
}
