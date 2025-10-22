package net.risesoft.repository.permission;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@JaversSpringDataAuditable
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

    List<Y9Authorization> findByResourceIdAndPrincipalType(String resourceId,
        AuthorizationPrincipalTypeEnum principalType);

    List<Y9Authorization> findByResourceIdAndPrincipalTypeNot(String resourceId,
        AuthorizationPrincipalTypeEnum principalType);

    List<Y9Authorization> findByResourceId(String resourceId);

    List<Y9Authorization> findByResourceIdAndAuthorityAndPrincipalIdIn(String resourceId, AuthorityEnum authority,
        List<String> principalIds);

    Page<Y9Authorization> findByResourceIdAndPrincipalType(String resourceId,
        AuthorizationPrincipalTypeEnum principalType, Pageable pageRequest);

    List<Y9Authorization> getByPrincipalIdIn(List<String> principalIds);

    List<Y9Authorization> findByResourceIdAndAuthority(String resourceId, AuthorityEnum authority);

    @Query("select resourceId from Y9Authorization where principalId = ?1 and authority = ?2")
    List<String> listResourceIdByPrincipleId(String principalId, AuthorityEnum authority);

    @Query("select principalId from Y9Authorization where resourceId = ?1 and authority = ?2")
    List<String> listRoleIdByResourceId(String resourceId, AuthorityEnum authority);
}
