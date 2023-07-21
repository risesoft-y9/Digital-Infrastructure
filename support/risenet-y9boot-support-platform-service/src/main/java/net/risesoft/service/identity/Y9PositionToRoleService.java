package net.risesoft.service.identity;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.identity.position.Y9PositionToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionToRoleService {

    /**
     * 查看岗位是否拥有 customId 对应的角色
     *
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return {@link Boolean}
     */
    Boolean hasRole(String positionId, String customId);

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
     * 根据人员id，分页查询个人授权
     *
     * @param positionId
     * @param page
     * @param rows
     * @param sort
     * @return
     */
    Page<Y9PositionToRole> pageByPositionId(String positionId, int page, int rows, String sort);

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
