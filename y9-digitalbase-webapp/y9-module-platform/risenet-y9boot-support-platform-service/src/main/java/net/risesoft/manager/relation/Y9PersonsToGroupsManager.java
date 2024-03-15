package net.risesoft.manager.relation;

import net.risesoft.entity.relation.Y9PersonsToGroups;

/**
 * 人员-用户组关联 Manager
 * 
 * @author shidaobang
 * @date 2023/09/18
 * @since 9.6.3
 */
public interface Y9PersonsToGroupsManager {

    void delete(Y9PersonsToGroups y9PersonsToGroups);

    void deleteByGroupId(String groupId);

    void deleteByPersonId(String personId);
}
