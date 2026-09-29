package net.risesoft.manager.relation;

import java.util.List;

import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 自定义用户组 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9CustomGroupMembersManager {
    /**
     * 保存自定义用户组成员
     *
     * @param orgUnitList 组织节点id列表
     * @param groupId 用户组id
     * @throws Y9NotFoundException orgUnitList 中的组织节点不存在的情况
     */
    void save(List<String> orgUnitList, String groupId);

    /**
     * 将源用户组成员共享到目标用户组
     *
     * @param sourceGroupId 源用户组id
     * @param targetGroupId 目标用户组id
     */
    void share(String sourceGroupId, String targetGroupId);
}
