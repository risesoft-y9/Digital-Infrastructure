package net.risesoft.manager.authorization.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.manager.authorization.Y9PositionToResourceAndAuthorityManager;
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
    public void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit) {
        Optional<Y9PositionToResourceAndAuthority> optionalY9PositionToResourceAndAuthority =
            y9PositionToResourceAndAuthorityRepository.findByPositionIdAndResourceIdAndAuthorizationIdAndAuthority(
                y9Position.getId(), y9ResourceBase.getId(), y9Authorization.getId(), y9Authorization.getAuthority());
        Y9PositionToResourceAndAuthority y9PositionToResourceAndAuthority;
        if (optionalY9PositionToResourceAndAuthority.isEmpty()) {
            y9PositionToResourceAndAuthority = new Y9PositionToResourceAndAuthority();
            y9PositionToResourceAndAuthority.setTenantId(y9Position.getTenantId());
            y9PositionToResourceAndAuthority.setPositionId(y9Position.getId());
            y9PositionToResourceAndAuthority.setResourceId(y9ResourceBase.getId());
            y9PositionToResourceAndAuthority.setAuthority(y9Authorization.getAuthority());
            y9PositionToResourceAndAuthority.setAuthorizationId(y9Authorization.getId());
        } else {
            y9PositionToResourceAndAuthority = optionalY9PositionToResourceAndAuthority.get();
        }
        y9PositionToResourceAndAuthority.setResourceName(y9ResourceBase.getName());
        y9PositionToResourceAndAuthority.setResourceType(y9ResourceBase.getResourceType());
        y9PositionToResourceAndAuthority.setResourceUrl(y9ResourceBase.getUrl());
        y9PositionToResourceAndAuthority.setResourceDescription(y9ResourceBase.getDescription());
        y9PositionToResourceAndAuthority.setResourceTabIndex(y9ResourceBase.getTabIndex());
        y9PositionToResourceAndAuthority.setParentResourceId(y9ResourceBase.getParentId());
        y9PositionToResourceAndAuthority.setInherit(inherit);

        Optional<Y9App> y9AppOptional = y9AppRepository.findById(y9ResourceBase.getAppId());
        if (y9AppOptional.isPresent()) {
            Y9App app = y9AppOptional.get();
            y9PositionToResourceAndAuthority.setAppId(app.getAppId());
            y9PositionToResourceAndAuthority.setAppName(app.getName());
        }

        Optional<Y9System> y9SystemOptional = y9SystemRepository.findById(y9ResourceBase.getSystemId());
        if (y9SystemOptional.isPresent()) {
            Y9System y9System = y9SystemOptional.get();
            y9PositionToResourceAndAuthority.setSystemId(y9System.getId());
            y9PositionToResourceAndAuthority.setSystemName(y9System.getName());
            y9PositionToResourceAndAuthority.setSystemCnName(y9System.getCnName());
        }
        y9PositionToResourceAndAuthorityRepository.save(y9PositionToResourceAndAuthority);
    }
}
