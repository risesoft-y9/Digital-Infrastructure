package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.model.platform.Job;
import net.risesoft.repository.Y9JobRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * @author sdb
 * @author ls
 * @date 2022/9/22
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9JobServiceImpl implements Y9JobService {

    private final Y9JobRepository y9JobRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final Y9JobManager y9JobManager;
    private final Y9PositionManager y9PositionManager;

    private void checkIfRelatedPositionExists(String id) {
        Y9Assert.lessThanOrEqualTo(y9PositionRepository.countByJobId(id), 0,
            OrgUnitErrorCodeEnum.RELATED_POSITION_EXISTS);
    }

    private boolean isNameAvailable(String name, String id) {
        Optional<Y9Job> y9JobOptional = y9JobRepository.findByName(name);

        if (y9JobOptional.isEmpty()) {
            // 不存在同名的职位肯定可用
            return true;
        }

        // 编辑职位时没修改名称同样认为可用
        return y9JobOptional.get().getId().equals(id);
    }

    @Override
    public long count() {
        return y9JobRepository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> ids) {
        for (String id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        checkIfRelatedPositionExists(id);

        Y9Job y9Job = y9JobManager.getById(id);
        y9JobManager.delete(y9Job);
    }

    @Override
    public Optional<Y9Job> findById(String id) {
        return y9JobManager.findById(id);
    }

    @Override
    public List<Y9Job> findByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        List<String> positionIdList =
            y9PersonsToPositionsList.stream().map(Y9PersonsToPositions::getPositionId).collect(Collectors.toList());
        List<Y9Job> y9JobList = new ArrayList<>();
        for (String positionId : positionIdList) {
            Y9Position y9Position = y9PositionManager.getById(positionId);
            y9JobList.add(this.getById(y9Position.getJobId()));
        }
        return y9JobList;
    }

    @Override
    public Y9Job getById(String id) {
        return y9JobManager.getById(id);
    }

    private Integer getMaxTabIndex() {
        return y9JobRepository.findTopByOrderByTabIndexDesc().map(Y9Job::getTabIndex).orElse(0);
    }

    @Override
    public List<Y9Job> listAll() {
        return y9JobRepository.findAll(Sort.by(Sort.Direction.ASC, "tabIndex"));
    }

    @Override
    public List<Y9Job> listByName(String name) {
        return y9JobRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Transactional(readOnly = false)
    public Y9Job order(String jobId, int tabIndex) {
        Y9Job y9Job = this.getById(jobId);
        y9Job.setTabIndex(tabIndex);
        return saveOrUpdate(y9Job);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Job> order(List<String> jobIds) {
        List<Y9Job> jobList = new ArrayList<>();

        int tabIndex = 0;
        for (String jobId : jobIds) {
            jobList.add(order(jobId, tabIndex++));
        }

        return jobList;
    }

    @Override
    public Page<Y9Job> page(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by(Sort.Direction.DESC, "tabIndex"));
        return y9JobRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Job saveOrUpdate(Y9Job job) {
        // 检查名称是否可用
        Y9Assert.isTrue(isNameAvailable(job.getName(), job.getId()), OrgUnitErrorCodeEnum.JOB_EXISTS, job.getName());

        if (StringUtils.isNotBlank(job.getId())) {
            // 修改职位
            Optional<Y9Job> y9JobOptional = this.findById(job.getId());
            if (y9JobOptional.isPresent()) {
                Y9Job originY9Job = y9JobOptional.get();
                Y9Job updatedY9Job = new Y9Job();
                Y9BeanUtil.copyProperties(originY9Job, updatedY9Job);
                Y9BeanUtil.copyProperties(job, updatedY9Job);
                final Y9Job savedJob = y9JobManager.save(updatedY9Job);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Job, savedJob));

                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        Y9MessageOrg<Job> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedJob, Job.class),
                            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_JOB, Y9LoginUserHolder.getTenantId());
                        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新职位信息", "更新职位" + savedJob.getName());
                    }
                });

                return savedJob;
            }
        }

        // 新增职位
        if (StringUtils.isBlank(job.getId())) {
            job.setId(Y9IdGenerator.genId());
        }
        job.setCode(job.getCode());
        job.setName(job.getName());
        job.setTabIndex(getMaxTabIndex() + 1);
        final Y9Job savedJob = y9JobManager.save(job);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Job> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedJob, Job.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_JOB, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增职位信息", "新增职位" + savedJob.getName());
            }
        });

        return savedJob;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Job create(String name, String code) {
        Optional<Y9Job> y9JobOptional = y9JobRepository.findByName(name);
        if (y9JobOptional.isPresent()) {
            return y9JobOptional.get();
        }
        Y9Job y9Job = new Y9Job();
        y9Job.setName(name);
        y9Job.setCode(code);
        return this.saveOrUpdate(y9Job);
    }

}
