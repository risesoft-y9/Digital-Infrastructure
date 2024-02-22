package net.risesoft.repository.permission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;

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
    void deleteByPrincipalIdAndPrincipalType(String principalId, AuthorizationPrincipalTypeEnum principalType);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByResourceId(String id);

    Page<Y9Authorization> findByPrincipalId(String roleId, Pageable pageable);

    List<Y9Authorization> findByPrincipalIdAndPrincipalType(String principalId,
        AuthorizationPrincipalTypeEnum principalType);

    List<Y9Authorization> findByPrincipalIdAndResourceId(String roleId, String resourceId, Sort sort);

    Optional<Y9Authorization> findByPrincipalIdAndResourceIdAndAuthority(String roleId, String resourceId,
        AuthorityEnum authority);

    List<Y9Authorization> findByPrincipalIdOrderByCreateTime(String principalId);

    List<Y9Authorization> findByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId);

    List<Y9Authorization> findByPrincipalTypeNotAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId);

    List<Y9Authorization> findByResourceId(String resourceId);

    List<Y9Authorization> findByResourceIdAndAuthorityAndPrincipalIdIn(String resourceId, AuthorityEnum authority,
        List<String> principalIds);

    List<Y9Authorization> getByPrincipalIdIn(List<String> principalIds);

    Page<Y9Authorization> findByResourceIdAndPrincipalType(String resourceId,
        AuthorizationPrincipalTypeEnum principalType, Pageable pageRequest);
}
