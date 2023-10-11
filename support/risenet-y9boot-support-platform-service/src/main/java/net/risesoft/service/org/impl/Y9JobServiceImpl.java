package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Job;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.model.Job;
import net.risesoft.repository.Y9JobRepository;
import net.risesoft.repository.Y9PositionRepository;
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

    private final Y9JobManager y9JobManager;

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
                updatedY9Job = y9JobManager.save(updatedY9Job);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Job, updatedY9Job));

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(updatedY9Job, Job.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_JOB, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新职位信息", "更新职位" + updatedY9Job.getName());

                return updatedY9Job;
            }
        }

        // 新增职位
        Y9Job y9Job = new Y9Job();
        if (StringUtils.isNotBlank(job.getId())) {
            // 使用指定的id
            y9Job.setId(job.getId());
        } else {
            y9Job.setId(Y9IdGenerator.genId());
        }
        y9Job.setCode(job.getCode());
        y9Job.setName(job.getName());
        y9Job.setTabIndex(getMaxTabIndex() + 1);
        y9Job = y9JobManager.save(y9Job);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(y9Job, Job.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_JOB, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增职位信息", "新增职位" + job.getName());

        return y9Job;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Job create(String name, String code) {
        Optional<Y9Job> y9JobOptional = y9JobRepository.findByName(name);
        Y9Job y9Job = y9JobOptional.orElse(new Y9Job());
        y9Job.setName(name);
        y9Job.setCode(code);
        return this.saveOrUpdate(y9Job);
    }

}
