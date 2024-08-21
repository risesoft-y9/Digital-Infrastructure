package net.risesoft.service.relation;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.pojo.Y9PageQuery;

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
     * @param memberIdList 用户组成员id数组
     */
    void delete(List<String> memberIdList);

    /**
     * 删除用户组成员
     *
     * @param id 用户组成员id
     */
    void delete(String id);

    /**
     * 获取最大的序号
     *
     * @param groupId 用户在id
     * @return Integer
     */
    Integer getMaxTabIndex(String groupId);

    /**
     * 根据自定义用户组id获取组下所有人
     *
     * @param groupId 用户组id
     * @return {@code List<Y9Person>}
     */
    List<Y9Person> listAllPersonsByGroupId(String groupId);

    /**
     * 根据群组id获取群组下所有的群成员
     *
     * @param groupId 用户组id
     * @return {@code List<Y9CustomGroupMember>}
     */
    List<Y9CustomGroupMember> listByGroupId(String groupId);

    /**
     * 根据自定义用户组id和成员类型获取自定义用户组成员
     *
     * @param groupId 用户组id
     * @param memberType 成员类型{@link OrgTypeEnum}
     * @return {@code List<Y9CustomGroupMember>}
     */
    List<Y9CustomGroupMember> listByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType);

    /**
     * 根据自定义用户组id获取用户组成员
     *
     * @param groupId 自定义用户组id
     * @param pageQuery 分页查询参数
     * @return {@code Page<Y9CustomGroupMember>}
     */
    Page<Y9CustomGroupMember> pageByGroupId(String groupId, Y9PageQuery pageQuery);

    /**
     * 根据自定义用户组id和自定义用户组成员类型分页查询成员
     *
     * @param groupId 用户组id
     * @param memberType 成员类型{@link OrgTypeEnum}
     * @param pageQuery 分页查询参数
     * @return {@code Page<Y9CustomGroupMember>}
     */
    Page<Y9CustomGroupMember> pageByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType, Y9PageQuery pageQuery);

    /**
     * 保存自定义用户组成员
     *
     * @param orgUnitList 成员id列表
     * @param groupId 用户组id
     */
    void save(List<String> orgUnitList, String groupId);

    /**
     * 保存一个群组成员
     *
     * @param member 群组成员
     * @return {@link Y9CustomGroupMember}
     */
    Y9CustomGroupMember save(Y9CustomGroupMember member);

    /**
     * 保存人员排序
     *
     * @param memberIdList 成员id列表
     * @return boolean
     */
    boolean saveOrder(List<String> memberIdList);

    /**
     * 分享的自定义用户组
     *
     * @param sourceGroupId 分享的用户组id
     * @param targetGroupId 目标用户组id
     */
    void share(String sourceGroupId, String targetGroupId);
}
