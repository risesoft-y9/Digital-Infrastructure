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
    List<String> findRoleIdByPersonId(String personId);

    void removeByPersonIdAndRoleId(String personId, String roleId);

    void save(Y9Person person, Y9Role role);
}
