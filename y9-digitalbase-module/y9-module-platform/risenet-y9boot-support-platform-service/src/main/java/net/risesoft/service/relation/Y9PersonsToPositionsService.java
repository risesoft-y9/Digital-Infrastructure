package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.y9.exception.Y9NotFoundException;

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
     * @param positionId 岗位id
     * @param personIds 人员id数组
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> addPersons(String positionId, String[] personIds);

    /**
     * 为人员添加多个岗位
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> addPositions(String personId, String[] positionIds);

    /**
     * 从岗位移除人员
     *
     * @param positionId 岗位id
     * @param personIds 人员id数组
     */
    void deletePersons(String positionId, String[] personIds);

    /**
     * 为人员移除岗位
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     */
    void deletePositions(String personId, String[] positionIds);

    /**
     * 根据人员id获取所拥有的岗位id（,分隔）
     *
     * @param personId 人员id
     * @param disabled 是否禁用
     * @return {@link String}
     */
    String getPositionIdsByPersonId(String personId, Boolean disabled);

    /**
     * 根据人员id获取人员与岗位的关联关系
     *
     * @param personId 人员id
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> listByPersonId(String personId);

    /**
     * 根据岗位id获取对应关系
     *
     * @param positionId 岗位id
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> listByPositionId(String positionId);

    /**
     * 保存排序结果
     *
     * @param positionId 岗位id
     * @param personIds 人员id数组
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> orderPersons(String positionId, String[] personIds);

    /**
     * 保存排序结果
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> orderPositions(String personId, String[] positionIds);

    /**
     * 保存或更新人员与岗位的关联关系
     *
     * @param y9PersonsToPositions 人员与岗位的关联关系
     * @throws Y9NotFoundException 人员或岗位不存在
     */
    void saveOrUpdate(PersonsPositions y9PersonsToPositions);

    /**
     * 根据岗位id获取人员列表
     *
     * @param positionId 岗位id
     * @return {@code List<Person>}
     * @throws Y9NotFoundException 关联关系对应的人员不存在
     */
    List<Person> listPersonByPositionId(String positionId);
}
