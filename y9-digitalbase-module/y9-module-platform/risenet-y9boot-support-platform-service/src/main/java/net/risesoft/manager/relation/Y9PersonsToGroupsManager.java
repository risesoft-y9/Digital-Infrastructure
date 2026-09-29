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

    /**
     * 删除人员与用户组的关联
     *
     * @param y9PersonsToGroups 人员与用户组关联信息
     */
    void delete(Y9PersonsToGroups y9PersonsToGroups);

    /**
     * 根据用户组id删除人员与用户组的关联
     *
     * @param groupId 用户组id
     */
    void deleteByGroupId(String groupId);

    /**
     * 根据人员id删除人员与用户组的关联
     *
     * @param personId 人员id
     */
    void deleteByPersonId(String personId);
}
