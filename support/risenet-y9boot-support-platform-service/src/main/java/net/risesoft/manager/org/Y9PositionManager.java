package net.risesoft.manager.org;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Position;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PositionManager {

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Position getById(String id);

    Y9Position findById(String id);

    Y9Position saveOrUpdate(Y9Position position, Y9OrgBase parent);

    Y9Position save(Y9Position position);

    void delete(Y9Position y9Position);

    Y9Position updateTabIndex(String id, int tabIndex);
}
