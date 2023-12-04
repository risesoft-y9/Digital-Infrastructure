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
     * @param positionId
     * @return
     */
    List<Y9PositionToRole> listByPositionId(String positionId);

    /**
     * 根据人员id及系统名，查询个人授权列表
     *
     * @param positionId
     * @param systemName
     * @return
     */
    List<Y9PositionToRole> listByPositionIdAndSystemName(String positionId, String systemName);

    /**
     * 重新计算岗位角色
     *
     * @param positionId 岗位id
     */
    void recalculate(String positionId);

    /**
     * 根据岗位id移除
     *
     * @param positionId
     */
    void removeByPositionId(String positionId);

    /**
     * 根据角色id移除
     *
     * @param roleId
     */
    void removeByRoleId(String roleId);

    /**
     * 更新人员授权信息
     *
     * @param roleId
     * @param roleName
     * @param systemName
     * @param systemCnName
     * @param description
     */
    void update(String roleId, String roleName, String systemName, String systemCnName, String description);

}
