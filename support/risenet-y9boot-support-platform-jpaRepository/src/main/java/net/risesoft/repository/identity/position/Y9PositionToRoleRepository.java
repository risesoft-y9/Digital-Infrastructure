package net.risesoft.repository.identity.position;

import java.util.List;
import java.util.Optional;

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

    long countByPositionIdAndRoleCustomId(String positionId, String customId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionIdAndRoleId(String positionId, String roleId);

    List<Y9PositionToRole> findByPositionId(String positionId);

    Page<Y9PositionToRole> findByPositionId(String positionId, Pageable pageable);

    Optional<Y9PositionToRole> findByPositionIdAndRoleId(String positionId, String roleId);

    List<Y9PositionToRole> findByPositionIdAndSystemNameOrderByAppName(String positionId, String systemName);

    List<Y9PositionToRole> findByRoleId(String roleId);

    @Query("select distinct p.roleId from Y9PositionToRole p where p.positionId = ?1")
    List<String> listRoleIdsByPositionId(String positionId);

}
