package net.risesoft.repository.identity.person;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.person.Y9PersonToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonToRoleRepository extends JpaRepository<Y9PersonToRole, Integer> {

    long countByPersonId(String personId);

    int countByPersonIdAndRoleCustomId(String personId, String customId);

    long countByPersonIdAndSystemName(String personId, String systemName);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonId(String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    List<Y9PersonToRole> findByPersonId(String personId);

    Page<Y9PersonToRole> findByPersonId(String personId, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndAppName(String personId, String appName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndAppNameAndSystemCnName(String personId, String appName, String systemCnName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndAppNameAndSystemCnNameAndRoleName(String personId, String appName, String systemCnName, String roleName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndAppNameNotIn(String personId, List<String> appNames, Pageable pageable);

    Y9PersonToRole findByPersonIdAndRoleId(String personId, String roleId);

    Page<Y9PersonToRole> findByPersonIdAndRoleName(String personId, String roleName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndRoleNameAndAppName(String personId, String roleName, String appName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndRoleNameAndAppNameNotIn(String personId, String roleName, List<String> appNames, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndRoleNameAndSystemCnNameAndAppNameNotIn(String personId, String roleName, String systemCnName, List<String> appNames, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndSystemCnName(String personId, String systemCnName, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndSystemCnNameAndAppNameNotIn(String personId, String systemCnName, List<String> appNames, Pageable pageable);

    Page<Y9PersonToRole> findByPersonIdAndSystemCnNameAndRoleName(String personId, String systemCnName, String roleName, Pageable pageable);

    List<Y9PersonToRole> findByPersonIdAndSystemNameOrderByAppName(String personId, String systemName);

    List<Y9PersonToRole> findByRoleId(String roleId);

    @Query("select distinct p.roleId from Y9PersonToRole p where p.personId = ?1")
    List<String> listRoleIdsByPersonId(String personId);

    @Query("select p.roleId from Y9PersonToRole p where p.personId = ?1")
    List<String> findRoleIdByPersonId(String personId);
}
