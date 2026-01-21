package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.repository.org.Y9OrganizationRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * 组织 manager 实现类
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.ORG_ORGANIZATION)
@RequiredArgsConstructor
public class Y9OrganizationManagerImpl implements Y9OrganizationManager {

    private final Y9OrganizationRepository y9OrganizationRepository;

    @Override
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public void delete(Y9Organization y9Organization) {
        y9OrganizationRepository.delete(y9Organization);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Organization> findByIdFromCache(String id) {
        return y9OrganizationRepository.findById(id);
    }

    @Override
    public Optional<Y9Organization> findById(String id) {
        return y9OrganizationRepository.findById(id);
    }

    @Override
    public Y9Organization getById(String id) {
        return y9OrganizationRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization getByIdFromCache(String id) {
        return this.getById(id);
    }

    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public Y9Organization save(Y9Organization y9Organization) {
        return y9OrganizationRepository.save(y9Organization);
    }

    @Override
    public Y9Organization insert(Y9Organization organization) {
        final Y9Organization savedOrganization = this.save(organization);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedOrganization));

        return savedOrganization;
    }

    @Override
    public Y9Organization update(Y9Organization organization, Y9Organization originalOrganization) {
        final Y9Organization savedOrganization = this.save(organization);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalOrganization, savedOrganization));

        return savedOrganization;
    }

}
