package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionService {

    /**
     * 更改禁用状态
     *
     * @param id ID
     * @return 岗位实体
     */
    Position changeDisabled(String id);

    /**
     * 创建岗位
     *
     * @param parentId 父ID
     * @param jobId 职位id
     * @return 岗位实体
     */
    Position create(String parentId, String jobId);

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
     * 根据id查找岗位
     *
     * @param id 唯一标识
     * @return {@code Optional<Position>} 岗位对象 或 null
     */
    Optional<Position> findById(String id);

    /**
     * 根据职位id查找岗位
     *
     * @param jobId 职位id
     * @return {@code List<Position>}
     */
    List<Position> findByJobId(String jobId);

    /**
     * 查找 guidPath 包含传入参数的对应岗的 id
     *
     * @param guidPath guid path
     * @return {@code List<String>}
     */
    List<String> findIdByGuidPathStartingWith(String guidPath);

    /**
     * 根据主键id获取岗位实例
     *
     * @param id 唯一标识
     * @return {@link Position} 岗位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Position getById(String id);

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
     * @return {@code List<Position>}
     */
    List<Position> listAll();

    /**
     * 根据父节点id,获取本层级的岗位列表
     *
     * @param parentId 父节点id
     * @param disabled 是否包含禁用的岗位
     * @return {@code List<Position>}
     */
    List<Position> listByParentId(String parentId, Boolean disabled);

    /**
     * 根据人员id,获取本层级的岗位列
     *
     * @param personId 人员id
     * @param disabled 是否包含禁用的岗位
     * @return {@code List<Position>}
     */
    List<Position> listByPersonId(String personId, Boolean disabled);

    /**
     * 移动
     *
     * @param id 岗位id
     * @param parentId 父节点id
     * @return {@link Position}
     */
    Position move(String id, String parentId);

    /**
     * 保存岗位
     *
     * @param position 岗位
     * @return {@link Position}
     */
    Position save(Position position);

    /**
     * 保存或者修改此岗位的信息
     *
     * @param position 岗位对象
     * @return {@link Position}
     */
    Position saveOrUpdate(Position position);

    /**
     * 保存新的序号
     *
     * @param positionIds 岗位id数组
     * @return {@code List<Position>}
     */
    List<Position> saveOrder(List<String> positionIds);

    /**
     * 保存或者更新岗位扩展信息
     *
     * @param id 岗位id
     * @param properties 扩展属性
     * @return {@link Position}
     */
    Position saveProperties(String id, String properties);

}
