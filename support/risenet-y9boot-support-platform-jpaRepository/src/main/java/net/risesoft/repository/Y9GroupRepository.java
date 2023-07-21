package net.risesoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Group;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
//@JaversSpringDataAuditable
public interface Y9GroupRepository extends JpaRepository<Y9Group, String> {

    List<Y9Group> findByIdIn(List<String> groupIdList);

    List<Y9Group> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Group> findByNameContainingAndDnContainingOrderByTabIndex(String name, String dnName);

    List<Y9Group> findByParentIdOrderByTabIndexAsc(String parentId);

    Y9Group findTopByOrderByTabIndexDesc();

    Y9Group findTopByParentIdOrderByTabIndexDesc(String parentId);

    List<Y9Group> getByDn(String dn);

    @Query("select id from Y9Group where guidPath like ?1")
    List<String> listByGuidPathLike(String guidPath);
}
