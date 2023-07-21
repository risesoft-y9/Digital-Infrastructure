package net.risesoft.repository.identity.position;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.position.Y9PositionToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PositionToRoleRepository extends JpaRepository<Y9PositionToRole, Integer> {
    
    long countByPositionId(String positionId);

    long countByPositionIdAndRoleCustomId(String positionId, String customId);

    long countByPositionIdAndSystemName(String positionId, String systemName);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    List<Y9PositionToRole> findByPositionId(String positionId);

    Page<Y9PositionToRole> findByPositionId(String positionId, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndAppName(String positionId, String appName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndAppNameAndSystemCnName(String positionId, String appName, String systemCnName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndAppNameAndSystemCnNameAndRoleName(String positionId, String appName, String systemCnName, String roleName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndAppNameNotIn(String positionId, List<String> appNames, Pageable pageable);

    Y9PositionToRole findByPositionIdAndRoleId(String positionId, String roleId);

    Page<Y9PositionToRole> findByPositionIdAndRoleName(String positionId, String roleName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndRoleNameAndAppName(String positionId, String roleName, String appName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndRoleNameAndAppNameNotIn(String positionId, String roleName, List<String> appNames, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndRoleNameAndSystemCnNameAndAppNameNotIn(String positionId, String roleName, String systemCnName, List<String> appNames, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndSystemCnName(String positionId, String systemCnName, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndSystemCnNameAndAppNameNotIn(String positionId, String systemCnName, List<String> appNames, Pageable pageable);

    Page<Y9PositionToRole> findByPositionIdAndSystemCnNameAndRoleName(String positionId, String systemCnName, String roleName, Pageable pageable);

    List<Y9PositionToRole> findByPositionIdAndSystemNameOrderByAppName(String positionId, String systemName);

    List<Y9PositionToRole> findByRoleId(String roleId);

    @Query("select distinct p.roleId from Y9PositionToRole p where p.positionId = ?1")
    List<String> listRoleIdsByPositionId(String positionId);
}
