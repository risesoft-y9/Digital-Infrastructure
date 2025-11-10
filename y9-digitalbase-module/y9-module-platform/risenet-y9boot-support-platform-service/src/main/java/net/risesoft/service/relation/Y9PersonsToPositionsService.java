package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonsPositions;

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
     * @return {@link String}
     */
    String getPositionIdsByPersonId(String personId);

    List<PersonsPositions> listByPersonId(String personId);

    /**
     * 根据岗位id获取对应关系
     *
     * @param positionId 岗位id
     * @return {@code List<PersonsPositions>}
     */
    List<PersonsPositions> listByPositionId(String positionId);

    /**
     * 根据人员id获取岗位ID
     *
     * @param personId 人员id
     * @return {@code List<String>}
     */
    List<String> listPositionIdsByPersonId(String personId);

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

    void saveOrUpdate(PersonsPositions y9PersonsToPositions);

    List<Person> listPersonByPositionId(String positionId);
}
