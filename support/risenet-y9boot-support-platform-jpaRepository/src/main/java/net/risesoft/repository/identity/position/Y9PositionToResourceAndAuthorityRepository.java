package net.risesoft.repository.identity.position;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
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
public interface Y9PositionToResourceAndAuthorityRepository
    extends JpaRepository<Y9PositionToResourceAndAuthority, String> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAppId(String appId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAuthorizationId(String authorizationId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAuthorizationIdAndPositionId(String authorizationId, String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionId(String positionId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionIdAndAuthorizationIdNotIn(String positionId, List<String> authorizationIdList);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPositionIdAndResourceId(String positionId, String resourceId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByResourceId(String resourceId);

    List<Y9PositionToResourceAndAuthority> findByPositionId(String positionId);

    Page<Y9PositionToResourceAndAuthority> findByPositionId(String positionId, Pageable pageable);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(
        String positionId, AuthorityEnum authority, ResourceTypeEnum resourceType);

    List<Y9PositionToResourceAndAuthority>
        findByPositionIdAndParentResourceIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(String positionId,
            String parentResourceId, AuthorityEnum authority, ResourceTypeEnum resourceType);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndParentResourceIdAndAuthorityOrderByResourceTabIndex(
        String positionId, String parentResourceId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndParentResourceIdIsNullAndAuthorityOrderByResourceTabIndex(
        String positionId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndResourceCustomIdAndAuthority(String positionId,
        String customId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndResourceIdAndAuthority(String positionId,
        String resourceId, AuthorityEnum authority);

    Optional<Y9PositionToResourceAndAuthority> findByPositionIdAndResourceIdAndAuthorizationIdAndAuthority(
        String positionId, String resourceId, String authorizationId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> findByResourceId(String resourceId);
}
