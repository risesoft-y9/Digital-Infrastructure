package net.risesoft.manager.permission.cache.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.entity.permission.cache.person.Y9PersonToResourceAndAuthority;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.permission.cache.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.repository.permission.cache.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

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

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationId(String authorizationId) {
        y9PersonToResourceAndAuthorityRepository.deleteByAuthorizationId(authorizationId);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPersonIdAndAuthorizationIdNotIn(String personId, List<String> authorizationIdList) {
        if (authorizationIdList.isEmpty()) {
            y9PersonToResourceAndAuthorityRepository.deleteByPersonId(personId);
            return;
        }
        y9PersonToResourceAndAuthorityRepository.deleteByPersonIdAndAuthorizationIdNotIn(personId, authorizationIdList);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPersonIdAndResourceId(String personId, String resourceId) {
        y9PersonToResourceAndAuthorityRepository.deleteByPersonIdAndResourceId(personId, resourceId);
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
            y9PersonToResourceAndAuthority.setId(Y9IdGenerator.genId());
            y9PersonToResourceAndAuthority.setTenantId(person.getTenantId());
            y9PersonToResourceAndAuthority.setPersonId(person.getId());
            y9PersonToResourceAndAuthority.setResourceId(y9ResourceBase.getId());
            y9PersonToResourceAndAuthority.setAuthority(y9Authorization.getAuthority());
            y9PersonToResourceAndAuthority.setAuthorizationId(y9Authorization.getId());
        } else {
            y9PersonToResourceAndAuthority = optionalY9PersonToResourceAndAuthority.get();
        }
        y9PersonToResourceAndAuthority.setResourceType(y9ResourceBase.getResourceType());
        y9PersonToResourceAndAuthority.setParentResourceId(y9ResourceBase.getParentId());
        y9PersonToResourceAndAuthority.setInherit(inherit);
        y9PersonToResourceAndAuthority.setAppId(y9ResourceBase.getAppId());
        y9PersonToResourceAndAuthority.setSystemId(y9ResourceBase.getSystemId());

        y9PersonToResourceAndAuthorityRepository.save(y9PersonToResourceAndAuthority);
    }
}
