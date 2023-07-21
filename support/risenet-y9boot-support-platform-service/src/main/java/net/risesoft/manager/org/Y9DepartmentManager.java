package net.risesoft.manager.org;

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
}
