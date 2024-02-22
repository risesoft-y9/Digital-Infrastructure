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
    Y9Department getById(String id);

    Y9Department save(Y9Department y9Department);

    void delete(Y9Department y9Department);

    Optional<Y9Department> findById(String id);

    Y9Department updateTabIndex(String id, int tabIndex);
}
