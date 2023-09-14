package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.entity.relation.Y9PersonsToGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonsToGroupsService {

    /**
     * add groups to the person 为人员添加多个用户组
     *
     * @param personId
     * @param groupIds
     */
    void addGroups(String personId, String[] groupIds);

    /**
     * add persons to the group 为用户组添加人员
     *
     * @param groupId
     * @param personIds
     */
    void addPersons(String groupId, String[] personIds);

    /**
     * 根据用户组ID,删除用户组和人员的映射关系 delete the group-person mapping relationship by groupID
     *
     * @param groupId
     */
    void deleteByGroupId(String groupId);

    /**
     * 根据人员ID,删除用户组和人员的映射关系 delete the group-person mapping relationship by personID
     *
     * @param personId
     */
    void deleteByPersonId(String personId);

    /**
     * 根据人员id查询最大的用户组排序号
     *
     * @param personId
     * @return
     */
    Integer getMaxGroupOrderByPersonId(String personId);

    /**
     * 根据组id获取最大的人员排列序号
     *
     * @param groupId
     * @return
     */
    Integer getMaxPersonOrderByGroupId(String groupId);

    /**
     * 根据用户组id获取
     *
     * @param groupId
     * @return
     */
    List<Y9PersonsToGroups> listByGroupId(String groupId);

    /**
     * 根据人员id获取组ID
     *
     * @param personId
     * @return
     */
    List<String> listGroupIdsByPersonId(String personId);

    /**
     * 保存排序结果 save the orders of the groups for this person
     *
     * @param personId
     * @param groupIds
     * @return
     */
    List<Y9PersonsToGroups> orderGroups(String personId, String[] groupIds);

    /**
     * 保存排序结果 save the orders of the persons in this group
     *
     * @param groupId
     * @param personIds
     * @return
     */
    List<Y9PersonsToGroups> orderPersons(String groupId, String[] personIds);

    /**
     * remove groups from the person 为人员移除用户组
     *
     * @param personId
     * @param groupIds
     */
    void removeGroups(String personId, String[] groupIds);

    /**
     * remove persons from the group 为用户组移除人员
     *
     * @param groupId
     * @param personIds
     */
    void removePersons(String groupId, String[] personIds);
}
