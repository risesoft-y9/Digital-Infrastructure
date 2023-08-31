package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9CustomGroup;
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
     * @return {@link Y9CustomGroup}
     */
    Optional<Y9CustomGroup> findByCustomId(String customId);

    /**
     * 根据id查找自定义用户组
     *
     * @param id 唯一标识
     * @return 自定义用户组对象 或 null
     */
    Y9CustomGroup findById(String id);

    /**
     * 根据id获取自定义用户组
     *
     * @param id 唯一标识
     * @return 自定义用户组对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9CustomGroup getById(String id);

    /**
     * 根据人员id获取所有自定义群组
     *
     * @param personId 人员id
     * @return {@link List}<{@link Y9CustomGroup}>
     */
    List<Y9CustomGroup> listByPersonId(String personId);

    /**
     * 获取自定用户组列表
     *
     * @param page 页数
     * @param rows 每页行数
     * @param personId 人员id
     * @return {@link Page}<{@link Y9CustomGroup}>
     */
    Page<Y9CustomGroup> pageByPersonId(int page, int rows, String personId);

    /**
     * 保存用户组
     *
     * @param y9CustomGroup 自定义用户组对象
     * @return {@link Y9CustomGroup}
     */
    Y9CustomGroup save(Y9CustomGroup y9CustomGroup);

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
     * @return {@link Y9CustomGroup}
     */
    Y9CustomGroup saveOrUpdate(String personId, List<String> personIdList, String groupId, String groupName);

    /**
     * 共享用户组
     *
     * @param personIds 人员Ids
     * @param groupIds 用户组Ids
     * @return boolean
     */
    boolean share(List<String> personIds, List<String> groupIds);

}
