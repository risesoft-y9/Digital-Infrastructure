package net.risesoft.repository.identity.person;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.person.Y9PersonToResourceAndAuthority;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonToResourceAndAuthorityRepository
    extends JpaRepository<Y9PersonToResourceAndAuthority, Integer> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAppId(String appId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAuthorizationId(String authorizationId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAuthorizationIdAndPersonId(String authorizationId, String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonId(String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByResourceId(String resourceId);

    List<Y9PersonToResourceAndAuthority> findByPersonId(String personId);

    Page<Y9PersonToResourceAndAuthority> findByPersonId(String personId, Pageable pageable);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(
        String personId, Integer authority, Integer resourceType);

    List<Y9PersonToResourceAndAuthority>
        findByPersonIdAndParentResourceIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(String personId,
            String parentResourceId, Integer authority, Integer resourceType);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndParentResourceIdAndAuthorityOrderByResourceTabIndex(
        String personId, String parentResourceId, Integer authority);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndResourceCustomIdAndAuthority(String personId,
        String resourceCustomId, Integer authority);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndResourceIdAndAuthority(String personId, String resourceId,
        Integer authority);

    Optional<Y9PersonToResourceAndAuthority> findByPersonIdAndResourceIdAndAuthorizationIdAndAuthority(String personId,
        String resourceId, String authorizationId, Integer authority);

    List<Y9PersonToResourceAndAuthority> findByResourceId(String resourceId);

}
