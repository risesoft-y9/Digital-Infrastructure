package net.risesoft.repository.relation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.relation.Y9PersonsToPositions;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonsToPositionsRepository extends JpaRepository<Y9PersonsToPositions, Integer> {

    Integer countByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonId(String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionIdAndPersonId(String positionId, String personId);

    List<Y9PersonsToPositions> findByPersonId(String personId);

    List<Y9PersonsToPositions> findByPersonIdOrderByPositionOrderAsc(String personId);

    List<Y9PersonsToPositions> findByPositionId(String positionId);

    Y9PersonsToPositions findByPositionIdAndPersonId(String positionId, String personId);

    List<Y9PersonsToPositions> findByPositionIdOrderByPersonOrder(String positionId);

    Y9PersonsToPositions findTopByPersonIdOrderByPositionOrderDesc(String personId);

    Y9PersonsToPositions findTopByPositionIdOrderByPersonOrderDesc(String positionId);

    @Query("select t.positionId from Y9PersonsToPositions t where t.personId = ?1 order by t.positionOrder")
    List<String> listPositionIdsByPersonId(String personId);

    @Query("from Y9PersonsToPositions persontoposition where persontoposition.personId=?1")
    List<Y9PersonsToPositions> getByPersonId(String personId);
}
