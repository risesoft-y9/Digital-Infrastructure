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
    void deleteByPersonId(String personId);

    List<Y9PersonsToPositions> addPositions(String personId, String[] positionIds);

    Y9PersonsToPositions save(String personId, String positionId);

    void delete(String positionId, String personId);

    void delete(Y9PersonsToPositions y9PersonsToPositions);

    Integer getMaxPositionOrderByPersonId(String personId);

    Integer getMaxPersonOrderByPositionId(String positionId);
}
