package net.risesoft.manager.relation;

import java.util.List;

/**
 * 自定义用户组 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9CustomGroupMembersManager {
    void save(List<String> orgUnitList, String groupId);

    void share(String sourceGroupId, String targetGroupId);
}
