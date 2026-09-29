package net.risesoft.manager.permission.cache;

import java.util.List;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.y9public.entity.Y9Role;

/**
 * 岗位角色缓存 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PositionToRoleManager {

    /**
     * 删除岗位角色缓存
     *
     * @param positionId 岗位id
     * @param roleId 角色id
     */
    void deleteByPositionIdAndRoleId(String positionId, String roleId);

    /**
     * 根据岗位id获取角色id列表
     *
     * @param positionId 岗位id
     * @return 角色id列表
     */
    List<String> listRoleIdByPositionId(String positionId);

    /**
     * 保存岗位角色缓存
     *
     * @param y9Position 岗位信息
     * @param role 角色信息
     */
    void save(Y9Position y9Position, Y9Role role);
}
