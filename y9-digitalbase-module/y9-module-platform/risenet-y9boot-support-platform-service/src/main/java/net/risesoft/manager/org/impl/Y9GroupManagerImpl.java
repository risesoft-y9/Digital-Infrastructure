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
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_GROUP)
@RequiredArgsConstructor
public class Y9GroupManagerImpl implements Y9GroupManager {

    private final Y9GroupRepository y9GroupRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @CacheEvict(key = "#y9Group.id")
    @Transactional(readOnly = false)
    public void delete(Y9Group y9Group) {
        y9GroupRepository.delete(y9Group);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Group));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null")
    public Optional<Y9Group> findById(String id) {
        return y9GroupRepository.findById(id);
    }

    @Override
    public Optional<Y9Group> findByIdNotCache(String id) {
        return y9GroupRepository.findById(id);
    }

    @Override
    public Y9Group getByIdNotCache(String id) {
        return y9GroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.GROUP_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group getById(String id) {
        return y9GroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.GROUP_NOT_FOUND, id));
    }

    @CacheEvict(key = "#y9Group.id")
    @Transactional(readOnly = false)
    public Y9Group save(Y9Group y9Group) {
        return y9GroupRepository.save(y9Group);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Group insert(Y9Group group) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(group.getParentId());

        if (StringUtils.isBlank(group.getId())) {
            group.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        group.setTenantId(Y9LoginUserHolder.getTenantId());
        group.setDisabled(false);
        group.setParentId(parent.getId());
        group.setTabIndex((null == group.getTabIndex() || DefaultConsts.TAB_INDEX.equals(group.getTabIndex()))
            ? compositeOrgBaseManager.getNextSubTabIndex(parent.getId()) : group.getTabIndex());
        group.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, group.getName(), parent.getDn()));
        group.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), group.getId()));

        Y9Group savedGroup = this.save(group);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedGroup));

        return savedGroup;
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Group update(Y9Group group) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(group.getParentId());
        Y9Group currentGroup = this.getById(group.getId());
        Y9Group originalGroup = Y9ModelConvertUtil.convert(currentGroup, Y9Group.class);

        Y9BeanUtil.copyProperties(group, currentGroup, "tenantId");
        currentGroup.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, group.getName(), parent.getDn()));
        currentGroup.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), group.getId()));
        Y9Group savedGroup = this.save(currentGroup);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalGroup, savedGroup));
        return savedGroup;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Group updateTabIndex(String id, int tabIndex) {
        Y9Group group = this.getById(id);

        Y9Group updatedGroup = Y9ModelConvertUtil.convert(group, Y9Group.class);
        updatedGroup.setTabIndex(tabIndex);
        return this.update(updatedGroup);
    }
}
