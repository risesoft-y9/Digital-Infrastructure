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
     * @param personId 人员id
     * @param groupIds 用户组id数组
     */
    void addGroups(String personId, String[] groupIds);

    /**
     * add persons to the group 为用户组添加人员
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     */
    void addPersons(String groupId, String[] personIds);

    /**
     * 根据人员ID,删除用户组和人员的映射关系
     *
     * @param personId 人员id
     */
    void deleteByPersonId(String personId);

    List<Y9PersonsToGroups> findByGroupId(String groupId);

    /**
     * 根据人员id查询最大的用户组排序号
     *
     * @param personId 人员id
     * @return Integer
     */
    Integer getNextGroupOrderByPersonId(String personId);

    /**
     * 根据组id获取最大的人员排列序号
     *
     * @param groupId 用户组id
     * @return Integer
     */
    Integer getNextPersonOrderByGroupId(String groupId);

    /**
     * 根据用户组id获取
     *
     * @param groupId 用户组id
     * @return {@code List<Y9PersonsToGroups>}
     */
    List<Y9PersonsToGroups> listByGroupId(String groupId);

    /**
     * 根据人员id获取组ID
     *
     * @param personId 人员id
     * @return {@code List<String>}
     */
    List<String> listGroupIdsByPersonId(String personId);

    /**
     * 保存排序结果 save the orders of the groups for this person
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     * @return {@code List<Y9PersonsToGroups>}
     */
    List<Y9PersonsToGroups> orderGroups(String personId, String[] groupIds);

    /**
     * 保存排序结果 save the orders of the persons in this group
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     * @return {@code List<Y9PersonsToGroups>}
     */
    List<Y9PersonsToGroups> orderPersons(String groupId, String[] personIds);

    /**
     * remove groups from the person 为人员移除用户组
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     */
    void removeGroups(String personId, String[] groupIds);

    /**
     * remove persons from the group 为用户组移除人员
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     */
    void removePersons(String groupId, String[] personIds);

    List<Y9PersonsToGroups> listByPersonId(String personId);

    Y9PersonsToGroups saveOrUpdate(Y9PersonsToGroups y9PersonsToGroups);
}
