package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.org.PersonsGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonsToGroupsService {

    /**
     * 为人员添加多个用户组
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     */
    void addGroups(String personId, String[] groupIds);

    /**
     * 为用户组添加人员
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     */
    void addPersons(String groupId, String[] personIds);

    /**
     * 根据用户组id获取
     *
     * @param groupId 用户组id
     * @return {@code List<PersonsGroups>}
     */
    List<PersonsGroups> listByGroupId(String groupId);

    /**
     * 保存排序结果
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     * @return {@code List<PersonsGroups>}
     */
    List<PersonsGroups> orderGroups(String personId, String[] groupIds);

    /**
     * 保存排序结果
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     * @return {@code List<PersonsGroups>}
     */
    List<PersonsGroups> orderPersons(String groupId, String[] personIds);

    /**
     * 为人员移除用户组
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     */
    void removeGroups(String personId, String[] groupIds);

    /**
     * 为用户组移除人员
     *
     * @param groupId 用户组id
     * @param personIds 人员id数组
     */
    void removePersons(String groupId, String[] personIds);

    List<PersonsGroups> listByPersonId(String personId);

    PersonsGroups saveOrUpdate(PersonsGroups y9PersonsToGroups);
}
