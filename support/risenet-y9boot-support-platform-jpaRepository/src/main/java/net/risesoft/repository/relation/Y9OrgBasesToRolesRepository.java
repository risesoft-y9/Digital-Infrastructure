package net.risesoft.repository.relation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.relation.Y9OrgBasesToRoles;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface Y9OrgBasesToRolesRepository extends JpaRepository<Y9OrgBasesToRoles, Integer> {

    long countByRoleIdAndOrgIdIn(String roleId, List<String> orgIds);

    long countByRoleIdAndOrgIdInAndNegative(String roleId, Set<String> orgIds, Boolean negative);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByOrgId(String orgId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    List<Y9OrgBasesToRoles> findByOrgIdAndNegativeOrderByOrgOrderDesc(String orgId, Boolean negative);

    List<Y9OrgBasesToRoles> findByParentId(String parentId);

    List<Y9OrgBasesToRoles> findByRoleId(String roleId);

    List<Y9OrgBasesToRoles> findByRoleId(String roleId, Sort sort);

    List<Y9OrgBasesToRoles> findByRoleIdAndNegativeOrderByOrgOrderDesc(String roleId, Boolean negative);

    List<Y9OrgBasesToRoles> findByRoleIdAndOrgId(String roleId, String orgId);

    Optional<Y9OrgBasesToRoles> findByRoleIdAndOrgIdAndNegative(String roleId, String orgId, Boolean negative);

    @Query("select distinct t.roleId from Y9OrgBasesToRoles t where t.orgId = ?1")
    List<String> findDistinctRoleIdByOrgId(String orgId);

    @Query("select distinct t.roleId from Y9OrgBasesToRoles t where t.parentId = ?1")
    List<String> findDistinctRoleIdByParentId(String parentId);

    @Query("select distinct t.roleId from Y9OrgBasesToRoles t where t.orgId = ?1 and t.negative = ?2")
    List<String> findRoleIdsByOrgIdAndNegative(String orgId, Boolean negative);

    @Query("select max(orgOrder) from Y9OrgBasesToRoles where roleId = ?1")
    Integer getMaxOrgOrderByRoleId(String roleId);

    @Query("select orgId from Y9OrgBasesToRoles where roleId = ?1 order by orgOrder desc")
    List<String> listOrgIdsByRoleId(String roleId);
}
