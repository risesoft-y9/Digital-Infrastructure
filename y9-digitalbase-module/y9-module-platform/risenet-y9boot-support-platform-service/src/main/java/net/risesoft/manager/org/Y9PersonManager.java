package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PersonManager {

    void delete(Y9Person y9Person);

    Optional<Y9Person> findByIdFromCache(String id);

    /**
     * 根据id，获取人员信息（直接读取数据库）
     *
     * @param id 人员id
     * @return {@code Optional<Y9Person>}
     */
    Optional<Y9Person> findById(String id);

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return {@link Y9Person } 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getByIdFromCache(String id);

    Y9Person getById(String id);

    List<Y9Person> listByGroupId(String groupId, Boolean disabled);

    List<Y9Person> listByParentId(String parentId, Boolean disabled);

    List<Y9Person> listByPositionId(String positionId, Boolean disabled);

    Y9Person insert(Y9Person person);

    Y9Person update(Y9Person person, Y9Person originalPerson);
    
    void updatePersonByOriginalId(Y9Person originalPerson, Y9PersonExt originalExt);
}
