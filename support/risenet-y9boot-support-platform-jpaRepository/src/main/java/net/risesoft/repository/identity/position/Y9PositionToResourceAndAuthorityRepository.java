package net.risesoft.repository.identity.position;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;

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
    extends JpaRepository<Y9PositionToResourceAndAuthority, Integer> {

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
    void deleteByResourceId(String resourceId);

    List<Y9PositionToResourceAndAuthority> findByPositionId(String positionId);

    Page<Y9PositionToResourceAndAuthority> findByPositionId(String positionId, Pageable pageable);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(
        String positionId, Integer authority, Integer resourceType);

    List<Y9PositionToResourceAndAuthority>
        findByPositionIdAndParentResourceIdAndAuthorityAndResourceTypeOrderByResourceTabIndex(String positionId,
            String parentResourceId, Integer authority, Integer resourceType);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndParentResourceIdAndAuthorityOrderByResourceTabIndex(
        String positionId, String parentResourceId, Integer authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndParentResourceIdIsNullAndAuthorityOrderByResourceTabIndex(
        String positionId, Integer authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndResourceCustomIdAndAuthority(String positionId,
        String customId, Integer authority);

    List<Y9PositionToResourceAndAuthority> findByPositionIdAndResourceIdAndAuthority(String positionId,
        String resourceId, Integer authority);

    Y9PositionToResourceAndAuthority findByPositionIdAndResourceIdAndAuthorizationIdAndAuthority(String positionId,
        String resourceId, String authorizationId, Integer authority);

    List<Y9PositionToResourceAndAuthority> findByResourceId(String resourceId);
}
