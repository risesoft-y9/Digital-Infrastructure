package net.risesoft.manager.authorization.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.manager.authorization.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
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
public class Y9PersonToResourceAndAuthorityManagerImpl implements Y9PersonToResourceAndAuthorityManager {

    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9AppRepository y9AppRepository;
    private final Y9SystemRepository y9SystemRepository;

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationId(String authorizationId) {
        y9PersonToResourceAndAuthorityRepository.deleteByAuthorizationId(authorizationId);
    }

    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Person person, Y9Authorization y9Authorization,
        Boolean inherit) {
        Optional<Y9PersonToResourceAndAuthority> optionalY9PersonToResourceAndAuthority =
            y9PersonToResourceAndAuthorityRepository.findByPersonIdAndResourceIdAndAuthorizationIdAndAuthority(
                person.getId(), y9ResourceBase.getId(), y9Authorization.getId(), y9Authorization.getAuthority());
        Y9PersonToResourceAndAuthority y9PersonToResourceAndAuthority;
        if (optionalY9PersonToResourceAndAuthority.isEmpty()) {
            y9PersonToResourceAndAuthority = new Y9PersonToResourceAndAuthority();
            y9PersonToResourceAndAuthority.setTenantId(person.getTenantId());
            y9PersonToResourceAndAuthority.setPersonId(person.getId());
            y9PersonToResourceAndAuthority.setResourceId(y9ResourceBase.getId());
            y9PersonToResourceAndAuthority.setAuthority(y9Authorization.getAuthority());
            y9PersonToResourceAndAuthority.setAuthorizationId(y9Authorization.getId());
        } else {
            y9PersonToResourceAndAuthority = optionalY9PersonToResourceAndAuthority.get();
        }
        y9PersonToResourceAndAuthority.setResourceName(y9ResourceBase.getName());
        y9PersonToResourceAndAuthority.setResourceType(y9ResourceBase.getResourceType());
        y9PersonToResourceAndAuthority.setResourceUrl(y9ResourceBase.getUrl());
        y9PersonToResourceAndAuthority.setResourceDescription(y9ResourceBase.getDescription());
        y9PersonToResourceAndAuthority.setResourceTabIndex(y9ResourceBase.getTabIndex());
        y9PersonToResourceAndAuthority.setParentResourceId(y9ResourceBase.getParentId());
        y9PersonToResourceAndAuthority.setInherit(inherit);

        Optional<Y9App> y9AppOptional = y9AppRepository.findById(y9ResourceBase.getAppId());
        if (y9AppOptional.isPresent()) {
            Y9App app = y9AppOptional.get();
            y9PersonToResourceAndAuthority.setAppId(app.getAppId());
            y9PersonToResourceAndAuthority.setAppName(app.getName());
        }

        Optional<Y9System> y9SystemOptional = y9SystemRepository.findById(y9ResourceBase.getSystemId());
        if (y9SystemOptional.isPresent()) {
            Y9System y9System = y9SystemOptional.get();
            y9PersonToResourceAndAuthority.setSystemId(y9System.getId());
            y9PersonToResourceAndAuthority.setSystemName(y9System.getName());
            y9PersonToResourceAndAuthority.setSystemCnName(y9System.getCnName());
        }
        y9PersonToResourceAndAuthorityRepository.save(y9PersonToResourceAndAuthority);
    }
}
