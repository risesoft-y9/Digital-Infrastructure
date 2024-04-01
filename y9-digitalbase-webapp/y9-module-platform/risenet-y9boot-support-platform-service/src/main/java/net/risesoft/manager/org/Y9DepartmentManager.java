package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.Y9Department;

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
     * @return
     */
    Optional<Y9Department> findByIdNotCache(String id);

    Y9Department getById(String id);

    Y9Department save(Y9Department y9Department);

    /**
     * 保存或者更新部门扩展信息
     *
     * @param id 部门唯一标识
     * @param properties 扩展属性
     * @return {@link Y9Department}
     */
    Y9Department saveProperties(String id, String properties);

    /**
     * 保存或者更新部门扩展信息
     *
     * @param id 部门唯一标识
     * @param tabIndex 排序
     * @return {@link Y9Department}
     */
    Y9Department updateTabIndex(String id, int tabIndex);
}
