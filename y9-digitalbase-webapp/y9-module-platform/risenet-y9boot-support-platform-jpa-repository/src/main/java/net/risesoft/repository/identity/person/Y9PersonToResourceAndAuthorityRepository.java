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
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;

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
    extends JpaRepository<Y9PersonToResourceAndAuthority, String> {

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
    void deleteByPersonIdAndAuthorizationIdNotIn(String personId, List<String> authorizationIdList);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonIdAndResourceId(String personId, String resourceId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByResourceId(String resourceId);

    List<Y9PersonToResourceAndAuthority> findByPersonId(String personId);

    Page<Y9PersonToResourceAndAuthority> findByPersonId(String personId, Pageable pageable);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndAuthorityAndResourceType(String personId,
        AuthorityEnum authority, ResourceTypeEnum resourceType);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndParentResourceIdAndAuthority(String personId,
        String parentResourceId, AuthorityEnum authority);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndParentResourceIdAndAuthorityAndResourceType(String personId,
        String parentResourceId, AuthorityEnum authority, ResourceTypeEnum resourceType);

    List<Y9PersonToResourceAndAuthority> findByPersonIdAndResourceIdAndAuthority(String personId, String resourceId,
        AuthorityEnum authority);

    Optional<Y9PersonToResourceAndAuthority> findByPersonIdAndResourceIdAndAuthorizationIdAndAuthority(String personId,
        String resourceId, String authorizationId, AuthorityEnum authority);

}
