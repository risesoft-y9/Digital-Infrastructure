package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.model.platform.org.Person;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PositionManager {

    String buildName(String jobName, List<Person> personList);

    void delete(Y9Position y9Position);

    Optional<Y9Position> findByIdFromCache(String id);

    /**
     * 根据id，获取岗位信息（直接读取数据库）
     *
     * @param id 岗位id
     * @return {@code Optional<Y9Position>}
     */
    Optional<Y9Position> findById(String id);

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return {@link Y9Position} 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Position getByIdFromCache(String id);

    /**
     * 根据主键id获取岗位实例（直接读取数据库）
     *
     * @param id 唯一标识
     * @return {@link Y9Position} 岗位对象
     */
    Y9Position getById(String id);

    Y9Position save(Y9Position position);

    Y9Position insert(Y9Position position);

    Y9Position update(Y9Position position);

    Y9Position updateTabIndex(String id, int tabIndex);
}
