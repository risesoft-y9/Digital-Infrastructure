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
     * @return
     */
    Optional<Y9Group> findById(String id);

    /**
     * 根据id，获取用户组信息（直接读取数据库）
     *
     * @param id 用户组id
     * @return
     */
    Optional<Y9Group> findByIdNotCache(String id);

    /**
     * 根据主键id获取用户组实例
     *
     * @param id 唯一标识
     * @return 用户组对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Group getById(String id);

    /**
     * 保存用户组信息
     *
     * @param y9Group
     * @return
     */
    Y9Group save(Y9Group y9Group);

    /**
     * 保存或者更新用户组扩展信息
     *
     * @param groupId 用户组id
     * @param properties 扩展信息
     * @return {@link Y9Group}
     */
    Y9Group saveProperties(String id, String properties);

    Y9Group updateTabIndex(String id, int tabIndex);
}
