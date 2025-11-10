package net.risesoft.repository.permission.cache.person;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.cache.person.Y9PersonToResource;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9PersonToResourceAndAuthorityRepository extends JpaRepository<Y9PersonToResource, String> {

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

    List<Y9PersonToResource> findByPersonId(String personId);

    Page<Y9PersonToResource> findByPersonId(String personId, Pageable pageable);

    List<Y9PersonToResource> findByPersonIdAndAuthorityAndResourceType(String personId, AuthorityEnum authority,
        ResourceTypeEnum resourceType);

    List<Y9PersonToResource> findByPersonIdAndParentResourceIdAndAuthority(String personId, String parentResourceId,
        AuthorityEnum authority);

    List<Y9PersonToResource> findByPersonIdAndParentResourceIdAndAuthorityAndResourceType(String personId,
        String parentResourceId, AuthorityEnum authority, ResourceTypeEnum resourceType);

    List<Y9PersonToResource> findByPersonIdAndResourceIdAndAuthority(String personId, String resourceId,
        AuthorityEnum authority);

    Optional<Y9PersonToResource> findByPersonIdAndResourceIdAndAuthorizationIdAndAuthority(String personId,
        String resourceId, String authorizationId, AuthorityEnum authority);

    @Query("select distinct p.resourceId from Y9PersonToResource p where p.personId = ?1 and p.authority = ?2 and p.resourceType = ?3")
    Page<String> findResourceIdByPersonIdAndAuthorityAndResourceType(String personId, AuthorityEnum authority,
        ResourceTypeEnum resourceTypeEnum, Pageable pageable);

}
