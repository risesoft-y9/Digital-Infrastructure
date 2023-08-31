package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9DepartmentProp;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9DepartmentPropRepository
    extends JpaRepository<Y9DepartmentProp, String>, JpaSpecificationExecutor<Y9DepartmentProp> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByDeptId(String deptId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByOrgBaseId(String orgBaseId);

    List<Y9DepartmentProp> findByCategoryOrderByTabIndex(Integer category);

    List<Y9DepartmentProp> findByDeptId(String deptId);

    List<Y9DepartmentProp> findByDeptIdAndCategoryOrderByTabIndex(String deptId, Integer category);

    Optional<Y9DepartmentProp> findByDeptIdAndOrgBaseIdAndCategory(String deptId, String orgBaseId, Integer category);

    List<Y9DepartmentProp> findByOrgBaseIdAndCategoryOrderByTabIndex(String orgBaseId, Integer category);

    @Query("select max(tabIndex) from Y9DepartmentProp where deptId = ?1 and category = ?2")
    Integer getMaxTabIndex(String deptId, Integer category);
}
