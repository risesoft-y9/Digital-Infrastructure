package net.risesoft.repository.permission.cache.position;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.cache.position.Y9PositionToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9PositionToRoleRepository extends JpaRepository<Y9PositionToRole, String> {

    int countByPositionIdAndRoleId(String positionId, String roleId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionIdAndRoleId(String positionId, String roleId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    List<Y9PositionToRole> findByPositionId(String positionId);

    Page<Y9PositionToRole> findByPositionId(String positionId, Pageable pageable);

    Optional<Y9PositionToRole> findByPositionIdAndRoleId(String positionId, String roleId);

    List<Y9PositionToRole> findByRoleId(String roleId);

    @Query("select distinct p.roleId from Y9PositionToRole p where p.positionId = ?1")
    List<String> listRoleIdsByPositionId(String positionId);

    @Query("select p.positionId from Y9PositionToRole p where p.roleId = ?1")
    List<String> findPositionIdByRoleId(String roleId);

    @Query("select p.roleId from Y9PositionToRole p where p.positionId = ?1")
    List<String> findRoleIdByPositionId(String positionId);
}
