package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.entity.relation.Y9PersonsToPositions;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonsToPositionsService {

    /**
     * 为岗位添加人员
     *
     * @param positionId
     * @param personIds
     * @return List<ORGPerson>
     */
    List<Y9PersonsToPositions> addPersons(String positionId, String[] personIds);

    /**
     * 为人员添加多个岗位
     *
     * @param personId
     * @param positionIds
     * @return List<ORGPosition>
     */
    List<Y9PersonsToPositions> addPositions(String personId, String[] positionIds);

    /**
     * 根据人员ID,删除岗位和人员的映射关系
     *
     * @param personId
     */
    void deleteByPersonId(String personId);

    /**
     * 根据岗位ID，删除岗位和人员的映射关系
     *
     * @param positionId
     */
    void deleteByPositionId(String positionId);

    /**
     * 从岗位移除人员
     *
     * @param positionId
     * @param personIds
     */
    void deletePersons(String positionId, String[] personIds);

    /**
     * 为人员移除岗位
     *
     * @param personId
     * @param positionIds
     */
    void deletePositions(String personId, String[] positionIds);

    /**
     * 根据岗位id查找 人员和岗位的关联关系
     *
     * @param positionId 岗位id
     * @return {@link List}<{@link Y9PersonsToPositions}>
     */
    List<Y9PersonsToPositions> findByPositionId(String positionId);

    /**
     * 根据人员id获取所拥有的岗位id（,分隔）
     *
     * @param personId 人员id
     * @return {@link String}
     */
    String getPositionIdsByPersonId(String personId);

    List<Y9PersonsToPositions> listByPersonId(String personId);

    /**
     * 根据岗位id获取对应关系
     *
     * @param positionId
     * @return
     */
    List<Y9PersonsToPositions> listByPositionId(String positionId);

    /**
     * 根据人员id获取岗位ID
     *
     * @param personId
     * @return
     */
    List<String> listPositionIdsByPersonId(String personId);

    /**
     * 保存排序结果
     *
     * @param positionId
     * @param personIds
     * @return
     */
    List<Y9PersonsToPositions> orderPersons(String positionId, String[] personIds);

    /**
     * 保存排序结果
     *
     * @param personId
     * @param positionIds
     * @return
     */
    List<Y9PersonsToPositions> orderPositions(String personId, String[] positionIds);
}
