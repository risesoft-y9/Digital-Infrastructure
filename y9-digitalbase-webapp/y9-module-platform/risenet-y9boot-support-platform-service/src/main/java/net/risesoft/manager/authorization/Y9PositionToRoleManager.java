package net.risesoft.manager.authorization;

import java.util.List;

import net.risesoft.entity.Y9Position;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 岗位角色 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PositionToRoleManager {

    void update(Y9Position y9Position, List<Y9Role> positionRelatedY9RoleList);
}
