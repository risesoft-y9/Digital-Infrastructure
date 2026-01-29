package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.repository.org.Y9JobRepository;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 职位 manager 实现类
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.ORG_JOB)
@RequiredArgsConstructor
public class Y9JobManagerImpl implements Y9JobManager {

    private final Y9JobRepository y9JobRepository;

    @Override
    @CacheEvict(key = "#y9Job.id")
    public void delete(Y9Job y9Job) {
        y9JobRepository.delete(y9Job);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Job> findByIdFromCache(String id) {
        return y9JobRepository.findById(id);
    }

    @Override
    public Optional<Y9Job> findById(String id) {
        return y9JobRepository.findById(id);
    }

    @Override
    public Y9Job getById(String id) {
        return y9JobRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.JOB_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Job getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    @CacheEvict(key = "#y9Job.id", condition = "#y9Job.id!=null")
    public Y9Job save(Y9Job y9Job) {
        return y9JobRepository.save(y9Job);
    }

    @Override
    @CacheEvict(key = "#id")
    public Y9Job updateTabIndex(String id, int tabIndex) {
        Y9Job y9Job = this.getById(id);
        y9Job.setTabIndex(tabIndex);
        return save(y9Job);
    }

    @Override
    @CacheEvict(key = "#y9Job.id", condition = "#y9Job.id!=null")
    public Y9Job update(Y9Job y9Job) {
        Y9Job currentJob = this.getById(y9Job.getId());
        Y9Job originY9Job = PlatformModelConvertUtil.convert(currentJob, Y9Job.class);

        Y9BeanUtil.copyProperties(y9Job, currentJob);
        Y9Job savedJob = y9JobRepository.save(currentJob);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Job, savedJob));

        return savedJob;
    }

    @Override
    @CacheEvict(key = "#y9Job.id", condition = "#y9Job.id!=null")
    public Y9Job insert(Y9Job y9Job) {
        if (StringUtils.isBlank(y9Job.getId())) {
            y9Job.setId(Y9IdGenerator.genId());
        }
        y9Job.setTabIndex(getMaxTabIndex() + 1);
        final Y9Job savedJob = y9JobRepository.save(y9Job);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedJob));

        return savedJob;
    }

    @Override
    public Integer getMaxTabIndex() {
        return y9JobRepository.findTopByOrderByTabIndexDesc().map(Y9Job::getTabIndex).orElse(0);
    }

    @Override
    public boolean isNameAvailable(String name, String id) {
        Optional<Y9Job> y9JobOptional = y9JobRepository.findByName(name);

        if (y9JobOptional.isEmpty()) {
            // 不存在同名的职位肯定可用
            return true;
        }

        // 编辑职位时没修改名称同样认为可用
        return y9JobOptional.get().getId().equals(id);
    }
}
