package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.Y9Group;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9GroupManager {

    void delete(Y9Group y9Group);

    /**
     * 根据id，获取用户组信息
     *
     * @param id 用户组id
     * @return {@code Optional<Y9Group>}
     */
    Optional<Y9Group> findById(String id);

    /**
     * 根据id，获取用户组信息（直接读取数据库）
     *
     * @param id 用户组id
     * @return {@code Optional<Y9Group>}
     */
    Optional<Y9Group> findByIdNotCache(String id);

    /**
     * 根据主键id获取用户组实例
     *
     * @param id 唯一标识
     * @return {@link Y9Group} 用户组对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Group getById(String id);

    Y9Group getByIdNotCache(String id);

    Y9Group insert(Y9Group group);

    Y9Group update(Y9Group group);

    Y9Group updateTabIndex(String id, int tabIndex);
}
