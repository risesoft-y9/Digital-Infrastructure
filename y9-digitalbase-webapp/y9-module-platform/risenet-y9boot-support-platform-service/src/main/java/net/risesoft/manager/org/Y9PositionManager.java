package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PositionManager {

    String buildName(Y9Job y9Job, List<Y9PersonsToPositions> personsToPositionsList);

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Position getById(String id);

    Optional<Y9Position> findById(String id);

    Y9Position saveOrUpdate(Y9Position position);

    Y9Position save(Y9Position position);

    void delete(Y9Position y9Position);

    Y9Position updateTabIndex(String id, int tabIndex);
}
