package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9CustomGroupService {

    /**
     * 删除用户组
     *
     * @param idList id集合
     */
    void delete(List<String> idList);

    /**
     * 根据自定义id查找用户组
     *
     * @param customId 自定义id
     * @return {@code Optional<CustomGroup>}
     */
    Optional<CustomGroup> findByCustomId(String customId);

    /**
     * 根据id查找自定义用户组
     *
     * @param id 唯一标识
     * @return {@code Optional<CustomGroup>} 自定义用户组对象 或 null
     */
    Optional<CustomGroup> findById(String id);

    /**
     * 根据id获取自定义用户组
     *
     * @param id 唯一标识
     * @return {@link CustomGroup }自定义用户组对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    CustomGroup getById(String id);

    /**
     * 根据人员id获取所有自定义群组
     *
     * @param personId 人员id
     * @return {@code List<CustomGroup>}
     */
    List<CustomGroup> listByPersonId(String personId);

    /**
     * 获取自定用户组列表
     *
     * @param personId 人员id
     * @param pageQuery 分页信息
     * @return {@code Page<CustomGroup>}
     */
    Y9Page<CustomGroup> pageByPersonId(String personId, Y9PageQuery pageQuery);

    /**
     * 保存用户组
     *
     * @param y9CustomGroup 自定义用户组对象
     * @return {@link CustomGroup}
     */
    CustomGroup save(CustomGroup y9CustomGroup);

    /**
     * 保存用户组排序
     *
     * @param sortIdList 自定义用户组id集合
     * @return boolean
     */
    boolean saveCustomGroupOrder(List<String> sortIdList);

    /**
     * 保存用户组
     *
     * @param personId 人员id
     * @param personIdList 人员Ids
     * @param groupId 用户组Id,绑定人员时传入
     * @param groupName 用户组名称
     * @return {@link CustomGroup}
     */
    CustomGroup saveOrUpdate(String personId, List<String> personIdList, String groupId, String groupName);

    /**
     * 共享用户组
     *
     * @param personIds 人员Ids
     * @param groupIds 用户组Ids
     * @return boolean
     */
    boolean share(List<String> personIds, List<String> groupIds);

}
