package net.risesoft.manager.identity.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.identity.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;

/**
 * 人员权限缓存 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PositionToResourceAndAuthorityManagerImpl implements Y9PositionToResourceAndAuthorityManager {

    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;
    private final Y9AppRepository y9AppRepository;
    private final Y9SystemRepository y9SystemRepository;

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationId(String authorizationId) {
        y9PositionToResourceAndAuthorityRepository.deleteByAuthorizationId(authorizationId);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPositionIdAndAuthorizationIdNotIn(String positionId, List<String> authorizationIdList) {
        if (authorizationIdList.isEmpty()) {
            y9PositionToResourceAndAuthorityRepository.deleteByPositionId(positionId);
            return;
        }
        y9PositionToResourceAndAuthorityRepository.deleteByPositionIdAndAuthorizationIdNotIn(positionId,
            authorizationIdList);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPositionIdAndResourceId(String positionId, String resourceId) {
        y9PositionToResourceAndAuthorityRepository.deleteByPositionIdAndResourceId(positionId, resourceId);
    }

    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit) {
        Optional<Y9PositionToResourceAndAuthority> optionalY9PositionToResourceAndAuthority =
            y9PositionToResourceAndAuthorityRepository.findByPositionIdAndResourceIdAndAuthorizationIdAndAuthority(
                y9Position.getId(), y9ResourceBase.getId(), y9Authorization.getId(), y9Authorization.getAuthority());
        Y9PositionToResourceAndAuthority y9PositionToResourceAndAuthority;
        if (optionalY9PositionToResourceAndAuthority.isEmpty()) {
            y9PositionToResourceAndAuthority = new Y9PositionToResourceAndAuthority();
            y9PositionToResourceAndAuthority.setId(Y9IdGenerator.genId());
            y9PositionToResourceAndAuthority.setTenantId(y9Position.getTenantId());
            y9PositionToResourceAndAuthority.setPositionId(y9Position.getId());
            y9PositionToResourceAndAuthority.setResourceId(y9ResourceBase.getId());
            y9PositionToResourceAndAuthority.setAuthority(y9Authorization.getAuthority());
            y9PositionToResourceAndAuthority.setAuthorizationId(y9Authorization.getId());
        } else {
            y9PositionToResourceAndAuthority = optionalY9PositionToResourceAndAuthority.get();
        }
        y9PositionToResourceAndAuthority.setResourceType(y9ResourceBase.getResourceType());
        y9PositionToResourceAndAuthority.setParentResourceId(y9ResourceBase.getParentId());
        y9PositionToResourceAndAuthority.setInherit(inherit);
        y9PositionToResourceAndAuthority.setAppId(y9ResourceBase.getAppId());
        y9PositionToResourceAndAuthority.setSystemId(y9ResourceBase.getSystemId());

        y9PositionToResourceAndAuthorityRepository.save(y9PositionToResourceAndAuthority);
    }
}
