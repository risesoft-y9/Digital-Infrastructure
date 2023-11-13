package net.risesoft.service.relation;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.OrgTypeEnum;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9CustomGroupMembersService {

    /**
     * 删除用户组成员
     *
     * @param memberIdList
     */
    void delete(List<String> memberIdList);

    /**
     * 删除用户组成员
     *
     * @param id id
     */
    void delete(String id);

    /**
     * 获取最大的序号
     *
     * @param groupId
     * @return
     */
    Integer getMaxTabIndex(String groupId);

    /**
     * 根据自定义用户组id获取组下所有人
     *
     * @param groupId
     * @return
     */
    List<Y9Person> listAllPersonsByGroupId(String groupId);

    /**
     * 根据群组id获取群组下所有的群成员
     *
     * @param groupId
     * @return
     */
    List<Y9CustomGroupMember> listByGroupId(String groupId);

    /**
     * 根据自定义用户组id和成员类型获取自定义用户组成员
     *
     * @param groupId
     * @param memberType
     * @return
     */
    List<Y9CustomGroupMember> listByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType);

    /**
     * 根据自定义用户组id获取用户组成员
     *
     * @param groupId
     * @param page
     * @param rows
     * @return
     */
    Page<Y9CustomGroupMember> pageByGroupId(String groupId, int page, int rows);

    /**
     * 根据自定义用户组id和自定义用户组成员类型分页查询成员
     *
     * @param groupId
     * @param memberType
     * @param page
     * @param rows
     * @return
     */
    Page<Y9CustomGroupMember> pageByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType, int page, int rows);

    /**
     * 保存自定义用户组成员
     *
     * @param orgUnitList
     * @param groupId
     */
    void save(List<String> orgUnitList, String groupId);

    /**
     * 保存一个群组成员
     *
     * @param member
     * @return
     */
    Y9CustomGroupMember save(Y9CustomGroupMember member);

    /**
     * 保存人员排序
     *
     * @param memberIdList
     * @return
     */
    boolean saveOrder(List<String> memberIdList);

    /**
     * 分享的自定义用户组
     *
     * @param sourceGroupId
     * @param targetGroupId
     */
    void share(String sourceGroupId, String targetGroupId);
}
