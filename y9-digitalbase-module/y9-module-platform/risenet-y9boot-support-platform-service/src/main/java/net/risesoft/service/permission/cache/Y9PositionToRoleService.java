package net.risesoft.service.permission.cache;

import java.util.List;

import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.permission.cache.PositionToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionToRoleService {

    boolean hasPublicRole(String positionId, String roleName);

    boolean hasRole(String positionId, String roleId);

    boolean hasRole(String positionId, String systemName, String roleName, String properties);

    /**
     * 查看岗位是否拥有 customId 对应的角色
     *
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return {@link Boolean}
     */
    boolean hasRoleByCustomId(String positionId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param positionId 岗位id
     * @return {@code List<PositionToRole>}
     */
    List<PositionToRole> listByPositionId(String positionId);

    /**
     * 根据角色id查询拥有角色的所有岗位
     *
     * @param roleId 角色id
     * @param disabled 岗位是否禁用
     * @return {@code List<Position> }
     */
    List<Position> listPositionsByRoleId(String roleId, Boolean disabled);

    /**
     * 根据岗位id查询其拥有的所有角色
     *
     * @param positionId 岗位id
     * @return {@code List<Role>}
     */
    List<Role> listRolesByPositionId(String positionId);
}
