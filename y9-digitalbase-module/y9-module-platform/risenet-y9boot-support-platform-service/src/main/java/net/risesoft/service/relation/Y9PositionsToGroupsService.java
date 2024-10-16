package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PositionsToGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionsToGroupsService {

    /**
     * 根据用户组Id,删除用户组和岗位的映射关系
     *
     * @param groupId 用户组id
     */
    void deleteByGroupId(String groupId);

    /**
     * 根据岗位ID,删除用户组和岗位的映射关系
     *
     * @param positionId 岗位id
     */
    void deleteByPositionId(String positionId);

    /**
     * 根据岗位id，获取最大的用户组排序
     *
     * @param positionId 岗位id
     * @return Integer
     */
    Integer getMaxGroupIdOrderByPositionId(String positionId);

    /**
     * 根据用户组id， 获取最大的岗位排序
     *
     * @param groupId 用户组id
     * @return Integer
     */
    Integer getMaxPositionOrderByGroupId(String groupId);

    /**
     * 根据用户组id，获取虚拟岗位关联列表
     *
     * @param groupId 用户组id
     * @return {@code List<Y9PositionsToGroups>}
     */
    List<Y9PositionsToGroups> listByGroupId(String groupId);

    /**
     * 根据用户组id，返回岗位列表
     *
     * @param groupId 用户组id
     * @return {@code List<Y9Position>}
     */
    List<Y9Position> listPositionsByGroupId(String groupId);

    /**
     * 保存排序结果
     *
     * @param groupId 用户组id
     * @param positionIds 岗位id数组
     * @return {@code List<Y9PositionsToGroups>}
     */
    List<Y9PositionsToGroups> orderPositions(String groupId, String[] positionIds);

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
