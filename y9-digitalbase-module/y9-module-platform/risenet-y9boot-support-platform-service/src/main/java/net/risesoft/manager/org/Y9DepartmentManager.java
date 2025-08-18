package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 部门 Manager
 *
 * @author shidaobang
 * @date 2023/06/27
 * @since 9.6.2
 */
public interface Y9DepartmentManager {

    void delete(Y9Department y9Department);

    Optional<Y9Department> findById(String id);

    /**
     * 根据id，获取部门信息（直接读取数据库）
     *
     * @param id 部门id
     * @return {@code Optional<Y9Department>}
     */
    Optional<Y9Department> findByIdNotCache(String id);

    /**
     * 根据id，获取部门信息
     * 
     * @param id 部门id
     * @return {@link Y9Department }
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Department getById(String id);

    /**
     * 根据id，获取部门信息（直接读取数据库）
     *
     * @param id 部门id
     * @return {@link Y9Department }
     */
    Y9Department getByIdNotCache(String id);

    Y9Department insert(Y9Department dept);

    Y9Department update(Y9Department dept);

    /**
     * 保存或者更新部门扩展信息
     *
     * @param id 部门唯一标识
     * @param tabIndex 排序
     * @return {@link Y9Department}
     */
    Y9Department updateTabIndex(String id, int tabIndex);
}
