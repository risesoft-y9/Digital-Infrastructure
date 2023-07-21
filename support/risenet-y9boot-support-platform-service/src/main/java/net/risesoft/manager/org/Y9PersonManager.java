package net.risesoft.manager.org;

import java.util.List;

import net.risesoft.entity.Y9Person;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PersonManager {

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getById(String id);

    List<Y9Person> listByPositionId(String positionId);

    List<Y9Person> listByGroupId(String groupId);
}
