package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.CustomGroupMemberQuery;

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
     * 根据自定义用户组id获取组下所有人
     *
     * @param groupId 用户组id
     * @return {@code List<Y9Person>}
     */
    List<Person> listAllPersonsByGroupId(String groupId);

    /**
     * 保存自定义用户组成员
     *
     * @param orgUnitList 成员id列表
     * @param groupId 用户组id
     */
    void save(List<String> orgUnitList, String groupId);

    /**
     * 保存人员排序
     *
     * @param memberIdList 成员id列表
     * @return boolean
     */
    boolean saveOrder(List<String> memberIdList);

    List<CustomGroupMember> list(CustomGroupMemberQuery customGroupMemberQuery);

    Y9Page<CustomGroupMember> page(CustomGroupMemberQuery customGroupMemberQuery, Y9PageQuery pageQuery);
}
