package net.risesoft.repository.relation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.relation.Y9PositionsToGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PositionsToGroupsRepository extends JpaRepository<Y9PositionsToGroups, Integer> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    List<Y9PositionsToGroups> findByGroupId(String groupId);

    Optional<Y9PositionsToGroups> findByGroupIdAndPositionId(String groupId, String positionId);

    List<Y9PositionsToGroups> findByGroupIdOrderByPositionOrder(String groupId);

    List<Y9PositionsToGroups> findByPositionId(String positionId);

    Optional<Y9PositionsToGroups> findTopByGroupIdOrderByPositionOrderDesc(String groupId);

    Optional<Y9PositionsToGroups> findTopByPositionIdOrderByGroupOrderDesc(String positionId);

}
