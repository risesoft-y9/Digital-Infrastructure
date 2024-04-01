package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Person;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PersonManager {

    void delete(Y9Person y9Person);

    Optional<Y9Person> findById(String id);

    /**
     * 根据id，获取人员信息（直接读取数据库）
     *
     * @param id 人员id
     * @return
     */
    Optional<Y9Person> findByIdNotCache(String id);

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getById(String id);

    List<Y9Person> listByGroupId(String groupId, Boolean disabled);

    List<Y9Person> listByPositionId(String positionId, Boolean disabled);

    Y9Person save(Y9Person y9Person);

    /**
     * 保存或者更新人员扩展信息
     *
     * @param id 人员id
     * @param properties 扩展属性
     * @return {@link Y9Person}
     */
    Y9Person saveProperties(String id, String properties);

    Y9Person updateTabIndex(String id, int tabIndex);
}
