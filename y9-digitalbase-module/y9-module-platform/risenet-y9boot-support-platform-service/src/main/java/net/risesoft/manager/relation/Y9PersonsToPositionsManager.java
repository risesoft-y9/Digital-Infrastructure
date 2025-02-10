package net.risesoft.manager.relation;

import java.util.List;

import net.risesoft.entity.relation.Y9PersonsToPositions;

/**
 * 人员岗位关联 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PersonsToPositionsManager {
    List<Y9PersonsToPositions> addPositions(String personId, List<String> positionIds);

    void delete(String positionId, String personId);

    void delete(Y9PersonsToPositions y9PersonsToPositions);

    void deleteByPersonId(String personId);

    void deleteByPositionId(String positionId);

    Integer getNextPersonOrderByPositionId(String positionId);

    Integer getNextPositionOrderByPersonId(String personId);

    Y9PersonsToPositions save(String personId, String positionId);

    Y9PersonsToPositions saveOrUpdate(Y9PersonsToPositions y9PersonsToPositions);
}
