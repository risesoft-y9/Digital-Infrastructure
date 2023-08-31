package net.risesoft.repository.permission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.Y9Authorization;

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
public interface Y9AuthorizationRepository extends JpaRepository<Y9Authorization, String> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPrincipalIdAndPrincipalType(String principalId, Integer principalType);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByResourceId(String id);

    Page<Y9Authorization> findByPrincipalId(String roleId, Pageable pageable);

    List<Y9Authorization> findByPrincipalIdAndPrincipalType(String principalId, Integer principalType);

    List<Y9Authorization> findByPrincipalIdAndResourceId(String roleId, String resourceId, Sort sort);

    Optional<Y9Authorization> findByPrincipalIdAndResourceIdAndAuthority(String roleId, String resourceId,
        Integer authority);

    List<Y9Authorization> findByPrincipalIdAndResourceIdAndAuthorityIsNot(String roleId, String resourceId,
        Integer value, Sort sort);

    List<Y9Authorization> findByPrincipalIdOrderByCreateTime(String principalId);

    List<Y9Authorization> findByPrincipalTypeAndResourceId(Integer principalType, String resourceId);

    List<Y9Authorization> findByPrincipalTypeNotAndResourceId(Integer principalType, String resourceId);

    List<Y9Authorization> findByResourceId(String resourceId);

    List<Y9Authorization> findByResourceIdAndAuthorityAndPrincipalId(String resourceId, Integer authority,
        String principalId);

    List<Y9Authorization> findByResourceIdAndAuthorityAndPrincipalIdIn(String resourceId, Integer authority,
        List<String> principalIds);

    List<Y9Authorization> getByPrincipalIdIn(List<String> principalIds);

    @Query("select distinct t.resourceId from Y9Authorization t where t.authority >= ?1 and t.principalId in ?2")
    List<String> getResourceIdList(Integer authority, List<String> principalIds);

    Page<Y9Authorization> findByResourceIdAndPrincipalType(String resourceId, Integer principalType,
        PageRequest pageRequest);
}
