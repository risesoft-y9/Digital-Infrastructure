package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 组织 manager 实现类
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_ORGANIZATION)
@RequiredArgsConstructor
public class Y9OrganizationManagerImpl implements Y9OrganizationManager {

    private final Y9OrganizationRepository y9OrganizationRepository;

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public void delete(Y9Organization y9Organization) {
        y9OrganizationRepository.delete(y9Organization);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Organization> findById(String id) {
        return y9OrganizationRepository.findById(id);
    }

    @Override
    public Optional<Y9Organization> findByIdNotCache(String id) {
        return y9OrganizationRepository.findById(id);
    }

    @Override
    public Y9Organization getByIdNotCache(String id) {
        return y9OrganizationRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization getById(String id) {
        return y9OrganizationRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public Y9Organization save(Y9Organization y9Organization) {
        return y9OrganizationRepository.save(y9Organization);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Organization insert(Y9Organization organization) {
        if (StringUtils.isBlank(organization.getId())) {
            organization.setId(Y9IdGenerator.genId());
        }

        organization.setTenantId(Y9LoginUserHolder.getTenantId());
        organization.setVersion(InitDataConsts.Y9_VERSION);
        organization.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.ORGANIZATION, organization.getName(), null));
        organization.setTabIndex(
            (null == organization.getTabIndex() || DefaultConsts.TAB_INDEX.equals(organization.getTabIndex()))
                ? getNextTabIndex() : organization.getTabIndex());
        organization.setGuidPath(Y9OrgUtil.buildGuidPath(null, organization.getId()));
        final Y9Organization savedOrganization = this.save(organization);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedOrganization));

        return savedOrganization;
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Organization update(Y9Organization organization) {
        Y9Organization originY9Organization = new Y9Organization();
        Y9Organization updatedY9Organization = this.getById(organization.getId());
        Y9BeanUtil.copyProperties(updatedY9Organization, originY9Organization);

        Y9BeanUtil.copyProperties(organization, updatedY9Organization);

        updatedY9Organization.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.ORGANIZATION, updatedY9Organization.getName(), null));
        updatedY9Organization.setGuidPath(Y9OrgUtil.buildGuidPath(null, updatedY9Organization.getId()));
        final Y9Organization savedOrganization = this.save(updatedY9Organization);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Organization, savedOrganization));

        return savedOrganization;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Organization updateTabIndex(String id, int tabIndex) {
        Y9Organization organization = this.getById(id);

        Y9Organization updatedOrganization = Y9ModelConvertUtil.convert(organization, Y9Organization.class);
        updatedOrganization.setTabIndex(tabIndex);
        return this.update(updatedOrganization);
    }

    private Integer getNextTabIndex() {
        return y9OrganizationRepository.findTopByOrderByTabIndexDesc().map(Y9OrgBase::getTabIndex).orElse(-1) + 1;
    }
}
