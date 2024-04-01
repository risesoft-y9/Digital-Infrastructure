package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Organization;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.model.platform.Organization;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
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
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization getById(String id) {
        return y9OrganizationRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public Y9Organization save(Y9Organization y9Organization) {
        return y9OrganizationRepository.save(y9Organization);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional(readOnly = false)
    public Y9Organization saveProperties(String id, String properties) {
        Y9Organization org = this.getById(id);
        org.setProperties(properties);
        org = save(org);

        Y9MessageOrg<Organization> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);
        return org;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Organization updateTabIndex(String id, int tabIndex) {
        Y9Organization org = this.getById(id);
        org.setTabIndex(tabIndex);
        org = save(org);

        Y9MessageOrg<Organization> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新组织机构排序号", org.getName() + "的排序号更新为" + tabIndex);
        return org;
    }
}
