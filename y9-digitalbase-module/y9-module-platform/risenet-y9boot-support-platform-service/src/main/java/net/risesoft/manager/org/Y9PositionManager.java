package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PositionManager {

    String buildName(Y9Job y9Job, List<Y9PersonsToPositions> personsToPositionsList);

    void delete(Y9Position y9Position);

    Optional<Y9Position> findById(String id);

    /**
     * 根据id，获取岗位信息（直接读取数据库）
     *
     * @param id 岗位id
     * @return {@code Optional<Y9Position>}
     */
    Optional<Y9Position> findByIdNotCache(String id);

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return {@link Y9Position} 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Position getById(String id);

    /**
     * 根据主键id获取岗位实例（直接读取数据库）
     *
     * @param id 唯一标识
     * @return {@link Y9Position} 岗位对象
     */
    Y9Position getByIdNotCache(String id);

    Y9Position save(Y9Position position);

    Y9Position saveOrUpdate(Y9Position position);

    /**
     * 保存或者更新岗位扩展信息
     *
     * @param id 岗位id
     * @param properties 扩展属性
     * @return {@link Y9Position}
     */
    Y9Position saveProperties(String id, String properties);

    Y9Position updateTabIndex(String id, int tabIndex);
}
