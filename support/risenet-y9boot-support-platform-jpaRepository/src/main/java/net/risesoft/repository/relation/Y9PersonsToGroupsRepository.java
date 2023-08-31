package net.risesoft.repository.relation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.relation.Y9PersonsToGroups;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonsToGroupsRepository extends JpaRepository<Y9PersonsToGroups, Integer> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByGroupId(String groupId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByGroupIdAndPersonId(String groupId, String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonId(String personId);

    List<Y9PersonsToGroups> findByGroupId(String groupId);

    Optional<Y9PersonsToGroups> findByGroupIdAndPersonId(String groupId, String personId);

    List<Y9PersonsToGroups> findByGroupIdOrderByPersonOrder(String groupId);

    List<Y9PersonsToGroups> findByPersonIdOrderByGroupOrder(String personId);

    Optional<Y9PersonsToGroups> findTopByGroupIdOrderByPersonOrderDesc(String groupId);

    Optional<Y9PersonsToGroups> findTopByPersonIdOrderByGroupOrderDesc(String personId);

    @Query("select distinct t.groupId from Y9PersonsToGroups t where t.personId = ?1")
    List<String> listGroupIdsByPersonId(String personId);
}
