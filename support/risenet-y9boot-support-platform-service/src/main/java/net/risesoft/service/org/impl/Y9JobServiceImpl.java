package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Job;
import net.risesoft.exception.JobErrorCodeEnum;
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

    private void checkIfPositionExists(String name) {
        Y9Assert.lessThanOrEqualTo(y9JobRepository.countByName(name), 0, JobErrorCodeEnum.JOB_EXISTS, name);
    }

    private void checkIfRelatedPositionExists(String id) {
        Y9Assert.lessThanOrEqualTo(y9PositionRepository.countByJobId(id), 0, JobErrorCodeEnum.RELATED_POSITION_EXISTS);
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
    public Y9Job findById(String id) {
        return y9JobManager.findById(id);
    }

    @Override
    public Y9Job getById(String id) {
        return y9JobManager.getById(id);
    }

    private Integer getMaxTabIndex() {
        Y9Job job = y9JobRepository.findTopByOrderByTabIndexDesc();
        if (job != null) {
            return job.getTabIndex();
        }
        return 0;
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
    public Y9Job order(String jobId, String tabIndexArray) {
        Y9Job y9Job = this.getById(jobId);
        y9Job.setTabIndex(Integer.parseInt(tabIndexArray));
        return saveOrUpdate(y9Job);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Job> order(String[] jobIds, String[] tabIndexs) {
        List<Y9Job> jobList = new ArrayList<>();
        for (int i = 0; i < jobIds.length; i++) {
            jobList.add(order(jobIds[i], tabIndexs[i]));
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
        if (StringUtils.isNotBlank(job.getId())) {
            // 修改职位
            Y9Job originY9Job = this.findById(job.getId());
            if (originY9Job != null) {
                Y9Job updatedY9Job = new Y9Job();
                Y9BeanUtil.copyProperties(originY9Job, updatedY9Job);
                Y9BeanUtil.copyProperties(job, updatedY9Job);
                updatedY9Job = y9JobManager.save(updatedY9Job);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Job, updatedY9Job));

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(updatedY9Job, Job.class), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_JOB, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新职位信息", "更新职位" + updatedY9Job.getName());

                return updatedY9Job;
            }
        }

        // 新增职位
        checkIfPositionExists(job.getName());
        
        Y9Job y9Job = new Y9Job();
        if (StringUtils.isNotBlank(job.getId())) {
            // 使用指定的id
            y9Job.setId(job.getId());
        } else {
            y9Job.setId(Y9IdGenerator.genId());
        }
        y9Job.setCode(job.getCode());
        y9Job.setName(job.getName());
        Integer maxTabIndex = getMaxTabIndex();
        y9Job.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        y9Job = y9JobManager.save(y9Job);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(y9Job, Job.class), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_JOB, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增职位信息", "新增职位" + job.getName());

        return y9Job;
    }

    @Override
    @Transactional(readOnly = false)
    public void create(String jobId, String name, String code) {
        if (!y9JobRepository.existsById(jobId)) {
            Y9Job y9Job = new Y9Job();
            y9Job.setId(jobId);
            y9Job.setName(name);
            y9Job.setCode(code);
            this.saveOrUpdate(y9Job);
        }
    }

}
