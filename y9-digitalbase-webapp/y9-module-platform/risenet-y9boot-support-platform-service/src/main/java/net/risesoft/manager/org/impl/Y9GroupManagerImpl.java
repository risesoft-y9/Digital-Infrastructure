package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Group;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.model.platform.Group;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9ModelConvertUtil;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_GROUP)
@RequiredArgsConstructor
public class Y9GroupManagerImpl implements Y9GroupManager {

    private final Y9GroupRepository y9GroupRepository;

    @Override
    @CacheEvict(key = "#y9Group.id")
    @Transactional(readOnly = false)
    public void delete(Y9Group y9Group) {
        y9GroupRepository.delete(y9Group);
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
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group getById(String id) {
        return y9GroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.GROUP_NOT_FOUND, id));
    }

    @Override
    @CacheEvict(key = "#y9Group.id")
    @Transactional(readOnly = false)
    public Y9Group save(Y9Group y9Group) {
        return y9GroupRepository.save(y9Group);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Group saveProperties(String id, String properties) {
        Y9Group group = this.getById(id);
        group.setProperties(properties);
        group = save(group);

        Y9MessageOrg<Group> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventTypeConst.GROUP_UPDATE, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return group;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Group updateTabIndex(String id, int tabIndex) {
        Y9Group group = this.getById(id);
        group.setTabIndex(tabIndex);
        group = this.save(group);

        Y9MessageOrg<Group> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventTypeConst.GROUP_UPDATE, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新用户组排序号", group.getName() + "的排序号更新为" + tabIndex);

        return group;
    }
}
