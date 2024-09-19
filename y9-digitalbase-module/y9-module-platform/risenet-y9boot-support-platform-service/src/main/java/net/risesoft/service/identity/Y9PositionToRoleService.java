package net.risesoft.service.identity;

import java.util.List;

import net.risesoft.entity.identity.position.Y9PositionToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionToRoleService {

    Boolean hasPublicRole(String positionId, String roleName);

    boolean hasRole(String positionId, String roleId);

    Boolean hasRole(String positionId, String systemName, String roleName, String properties);

    /**
     * 查看岗位是否拥有 customId 对应的角色
     *
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return {@link Boolean}
     */
    Boolean hasRoleByCustomId(String positionId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param positionId 岗位id
     * @return {@code List<Y9PositionToRole>}
     */
    List<Y9PositionToRole> listByPositionId(String positionId);

    /**
     * 根据岗位id移除
     *
     * @param positionId 岗位id
     */
    void removeByPositionId(String positionId);

    /**
     * 根据角色id移除
     *
     * @param roleId 角色id
     */
    void removeByRoleId(String roleId);

}