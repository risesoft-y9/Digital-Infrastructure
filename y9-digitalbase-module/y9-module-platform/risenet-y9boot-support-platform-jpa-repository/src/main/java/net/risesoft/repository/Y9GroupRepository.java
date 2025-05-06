package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
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
@JaversSpringDataAuditable
public interface Y9GroupRepository extends JpaRepository<Y9Group, String> {

    long countByDisabledAndGuidPathContaining(Boolean diabled, String guidPath);

    List<Y9Group> findByDn(String dn);

    List<Y9Group> findByDnAndDisabled(String dn, Boolean disabled);

    List<Y9Group> findByNameContainingAndDisabledOrderByTabIndexAsc(String name, Boolean disabled);

    List<Y9Group> findByNameContainingAndDnContainingAndDisabledOrderByTabIndexAsc(String name, String dnName,
        Boolean disabled);

    List<Y9Group> findByNameContainingAndDnContainingOrderByTabIndex(String name, String dnName);

    List<Y9Group> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Group> findByParentIdAndDisabledOrderByTabIndexAsc(String parentId, Boolean disabled);

    List<Y9Group> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Group> findTopByParentIdOrderByTabIndexDesc(String parentId);

    @Query("select o.id from Y9Group o where o.dn like %?1%")
    List<String> findIdByDnContaining(String name);
}
