package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.org.PositionsGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionsToGroupsService {

    /**
     * 根据用户组id，获取虚拟岗位关联列表
     *
     * @param groupId 用户组id
     * @return {@code List<PositionsGroups>}
     */
    List<PositionsGroups> listByGroupId(String groupId);

    /**
     * 根据用户组id，返回岗位列表
     *
     * @param groupId 用户组id
     * @return {@code List<Y9Position>}
     */
    List<Position> listPositionsByGroupId(String groupId);

    /**
     * 保存排序结果
     *
     * @param groupId 用户组id
     * @param positionIds 岗位id数组
     * @return {@code List<PositionsGroups>}
     */
    List<PositionsGroups> orderPositions(String groupId, String[] positionIds);

    /**
     * 为用户组移除岗位
     *
     * @param groupId 用户组id
     * @param positionIds 岗位id数组
     */
    void removePositions(String groupId, String[] positionIds);

    /**
     * 保存虚拟岗位关联
     *
     * @param groupId 用户组id
     * @param positionIds 岗位id数组
     */
    void saveGroupPosition(String groupId, String[] positionIds);

}
