package net.risesoft.manager.relation;

import java.util.List;

import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 人员岗位关联 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PersonsToPositionsManager {
    /**
     * 为人员添加岗位
     *
     * @param personId 人员id
     * @param positionIds 岗位id列表
     * @return 人员岗位关联列表
     * @throws Y9BusinessException 岗位人数已达到容量上限的情况
     * @throws Y9NotFoundException personId 或 positionIds 中的id对应的记录不存在的情况
     */
    List<Y9PersonsToPositions> addPositions(String personId, List<String> positionIds);

    /**
     * 根据岗位id和人员id删除人员岗位关联
     *
     * @param positionId 岗位id
     * @param personId 人员id
     * @throws Y9NotFoundException personId 或 positionId 对应的记录不存在的情况
     */
    void delete(String positionId, String personId);

    /**
     * 删除人员岗位关联
     *
     * @param y9PersonsToPositions 人员岗位关联信息
     */
    void delete(Y9PersonsToPositions y9PersonsToPositions);

    /**
     * 根据人员id删除人员岗位关联
     *
     * @param personId 人员id
     */
    void deleteByPersonId(String personId);

    /**
     * 根据岗位id删除人员岗位关联
     *
     * @param positionId 岗位id
     */
    void deleteByPositionId(String positionId);

    /**
     * 获取岗位下人员的下一个排序号
     *
     * @param positionId 岗位id
     * @return 下一个排序号
     */
    Integer getNextPersonOrderByPositionId(String positionId);

    /**
     * 获取人员所任岗位的下一个排序号
     *
     * @param personId 人员id
     * @return 下一个排序号
     */
    Integer getNextPositionOrderByPersonId(String personId);

    /**
     * 保存人员岗位关联
     *
     * @param personId 人员id
     * @param positionId 岗位id
     * @return {@link Y9PersonsToPositions}
     * @throws Y9BusinessException 岗位人数已达到容量上限的情况
     * @throws Y9NotFoundException personId 或 positionId 对应的记录不存在的情况
     */
    Y9PersonsToPositions save(String personId, String positionId);

    /**
     * 保存或更新人员岗位关联
     *
     * @param y9PersonsToPositions 人员岗位关联信息
     * @return {@link Y9PersonsToPositions}
     * @throws Y9NotFoundException 人员岗位关联中的人员或岗位不存在的情况
     */
    Y9PersonsToPositions saveOrUpdate(Y9PersonsToPositions y9PersonsToPositions);
}
