package net.risesoft.manager.permission.cache;

import java.util.List;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.y9public.entity.Y9Role;

/**
 * 岗位角色 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PositionToRoleManager {

    void deleteByPositionIdAndRoleId(String positionId, String roleId);

    List<String> listRoleIdByPositionId(String positionId);

    void save(Y9Position y9Position, Y9Role role);
}
