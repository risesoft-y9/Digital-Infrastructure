package net.risesoft.manager.authorization;

import java.util.List;

import net.risesoft.entity.Y9Person;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 人员角色关联 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PersonToRoleManager {
    void update(Y9Person y9Person, List<Y9Role> personRelatedY9RoleList);
}
