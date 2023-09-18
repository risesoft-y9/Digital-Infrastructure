package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Position;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionService {

    /**
     * 创建岗位
     *
     * @param y9Position 岗位对象
     * @return {@link Y9Position}
     */
    Y9Position createPosition(Y9Position y9Position);

    /**
     * 根据id数组删除岗位
     * 
     * @param ids id数组
     */
    void delete(List<String> ids);

    /**
     * 根据主键id删除岗位实例
     *
     * @param id 唯一标识
     */
    void deleteById(String id);

    /**
     * 根据父节点id删除岗位实例(并且移除岗位内的人员)
     *
     * @param parentId 父节点id
     */
    void deleteByParentId(String parentId);

    /**
     * 根据id判断岗位是否存在
     *
     * @param id 唯一标识
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据id查找岗位
     *
     * @param id 唯一标识
     * @return 岗位对象 或 null
     */
    Optional<Y9Position> findById(String id);

    /**
     * 根据职位id查找岗位
     *
     * @param jobId 职位id
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> findByJobId(String jobId);

    /**
     * 查找 guidPath 包含传入参数的对应岗的 id
     *
     * @param guidPath guid path
     * @return {@link List}<{@link String}>
     */
    List<String> findIdByGuidPathStartingWith(String guidPath);

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Position getById(String id);

    /**
     * 查询某人是否有某岗位
     *
     * @param positionName 岗位名
     * @param personId 人员id
     * @return {@link Boolean}
     */
    Boolean hasPosition(String positionName, String personId);

    /**
     * 获取所有岗位
     *
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> listAll();

    /**
     * 根据dn查询岗位
     *
     * @param dn dn
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> listByDn(String dn);

    /**
     * 根据名称查询
     *
     * @param name 岗位名
     * @return List<ORGPosition>
     */
    List<Y9Position> listByNameLike(String name);

    /**
     * 根据名称查询
     *
     * @param name 岗位名
     * @param dn dn
     * @return List<ORGPosition>
     */
    List<Y9Position> listByNameLike(String name, String dn);

    /**
     * 根据父节点id,获取本层级的岗位列表
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> listByParentId(String parentId);

    /**
     * 根据人员id,获取本层级的岗位列
     *
     * @param personId 人员id
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> listByPersonId(String personId);

    /**
     * 保存岗位
     *
     * @param position 岗位
     * @return {@link Y9Position}
     */
    Y9Position save(Y9Position position);

    /**
     * 保存新的序号
     *
     * @param positionIds 岗位id数组
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> saveOrder(List<String> positionIds);

    /**
     * 保存或者修改此岗位的信息
     *
     * @param position 岗位对象
     * @return ORGRole
     */
    Y9Position saveOrUpdate(Y9Position position);

    /**
     * 保存或者更新岗位扩展信息
     *
     * @param positionId 岗位id
     * @param properties 扩展属性
     * @return {@link Y9Position}
     */
    Y9Position saveProperties(String positionId, String properties);

    /**
     * 根据whereClause查询岗位
     *
     * @param whereClause 查询子句
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> search(String whereClause);

    /**
     * 针对岗位树，根据岗位名字查找
     *
     * @param name 岗位名
     * @return {@link List}<{@link Y9Position}>
     */
    List<Y9Position> treeSearch(String name);

    /**
     * 更新排序序列号
     *
     * @param id 唯一标识
     * @param tabIndex 排序序列号
     * @return {@link Y9Position}
     */
    Y9Position updateTabIndex(String id, int tabIndex);

    /**
     * 移动
     *
     * @param positionId 岗位id
     * @param parentId 父节点id
     * @return {@link Y9Position}
     */
    Y9Position move(String positionId, String parentId);
}
