package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Position;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
// @JaversSpringDataAuditable
public interface Y9PositionRepository extends JpaRepository<Y9Position, String> {

    long countByDisabledAndGuidPathContaining(boolean disabled, String guidPath);

    int countByJobId(String jobId);

    List<Y9Position> findByJobId(String jobId);

    List<Y9Position> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Position> findByNameContainingAndDnContainingOrderByTabIndexAsc(String name, String dnName);

    @Query("from Y9Position t where t.name like %?1% and (t.dn like %?2% and t.dn like %?3%)")
    List<Y9Position> findByNameContainingAndDnContainingOrDnContaining(String name, String dn, String dn2);

    List<Y9Position> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Position> findTopByParentIdOrderByTabIndexDesc(String parentId);

    List<Y9Position> findByDn(String dn);

    @Query("select id from Y9Position where guidPath like ?1%")
    List<String> findIdByGuidPathStartingWith(String guidPath);
}
