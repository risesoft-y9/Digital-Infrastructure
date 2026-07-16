package net.risesoft.manager.relation;

/**
 * 岗位用户组关联 Manager
 *
 * @author shidaobang
 * @date 2024/03/14
 */
public interface Y9PositionsToGroupsManager {

    /**
     * 根据岗位id删除岗位与用户组的关联
     *
     * @param positionId 岗位id
     */
    void deleteByPositionId(String positionId);

}
