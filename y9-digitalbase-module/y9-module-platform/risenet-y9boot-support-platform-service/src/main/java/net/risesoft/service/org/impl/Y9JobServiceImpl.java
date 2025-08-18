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

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9JobRepository;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

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

    @Override
    public long count() {
        return y9JobRepository.count();
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

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.JOB_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.JOB_DELETE.getDescription(), y9Job.getName()))
            .objectId(id)
            .oldObject(y9Job)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

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

    @Override
    public List<Y9Job> listAll() {
        return y9JobRepository.findAll(Sort.by(Sort.Direction.ASC, "tabIndex"));
    }

    @Override
    public List<Y9Job> listByNameLike(String name) {
        return y9JobRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Job> order(List<String> jobIds) {
        List<Y9Job> jobList = new ArrayList<>();

        int tabIndex = 0;
        for (String jobId : jobIds) {
            jobList.add(y9JobManager.updateTabIndex(jobId, tabIndex++));
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
        Y9Assert.isTrue(y9JobManager.isNameAvailable(job.getName(), job.getId()), OrgUnitErrorCodeEnum.JOB_EXISTS,
            job.getName());

        if (StringUtils.isNotBlank(job.getId())) {
            // 修改职位
            Optional<Y9Job> y9JobOptional = y9JobManager.findByIdNotCache(job.getId());
            if (y9JobOptional.isPresent()) {
                Y9Job originalJob = Y9ModelConvertUtil.convert(y9JobOptional.get(), Y9Job.class);
                Y9Job savedJob = y9JobManager.update(job);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.JOB_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.JOB_UPDATE.getDescription(), job.getName()))
                    .objectId(savedJob.getId())
                    .oldObject(originalJob)
                    .currentObject(savedJob)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedJob;
            }
        }

        Y9Job savedJob = y9JobManager.insert(job);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.JOB_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.JOB_CREATE.getDescription(), savedJob.getName()))
            .objectId(savedJob.getId())
            .oldObject(null)
            .currentObject(savedJob)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedJob;
    }

}
