package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import net.risesoft.model.platform.org.Job;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9JobRepository;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author sdb
 * @author ls
 * @date 2022/9/22
 */
@Service
@RequiredArgsConstructor
public class Y9JobServiceImpl implements Y9JobService {

    private final Y9JobRepository y9JobRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final Y9JobManager y9JobManager;
    private final Y9PositionManager y9PositionManager;

    private void checkIfRelatedPositionExists(String id) {
        Y9AssertUtil.lessThanOrEqualTo(y9PositionRepository.countByJobId(id), 0,
            OrgUnitErrorCodeEnum.RELATED_POSITION_EXISTS);
    }

    @Override
    @Transactional
    public Job create(String name, String code) {
        Optional<Y9Job> y9JobOptional = y9JobRepository.findByName(name);
        if (y9JobOptional.isPresent()) {
            return entityToModel(y9JobOptional.get());
        }
        Job y9Job = new Job();
        y9Job.setName(name);
        y9Job.setCode(code);
        return this.saveOrUpdate(y9Job);
    }

    @Override
    @Transactional
    public void delete(List<String> ids) {
        for (String id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    @Transactional
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
    public Optional<Job> findById(String id) {
        return y9JobManager.findByIdFromCache(id).map(this::entityToModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> findByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        List<String> positionIdList =
            y9PersonsToPositionsList.stream().map(Y9PersonsToPositions::getPositionId).collect(Collectors.toList());
        List<Job> y9JobList = new ArrayList<>();
        for (String positionId : positionIdList) {
            Y9Position y9Position = y9PositionManager.getByIdFromCache(positionId);
            y9JobList.add(this.getById(y9Position.getJobId()));
        }
        return y9JobList;
    }

    @Override
    public Job getById(String id) {
        return entityToModel(y9JobManager.getByIdFromCache(id));
    }

    @Override
    public List<Job> listAll() {
        List<Y9Job> y9JobList = y9JobRepository.findAll(Sort.by(Sort.Direction.ASC, "tabIndex"));
        return entityToModel(y9JobList);
    }

    @Override
    public List<Job> listByNameLike(String name) {
        List<Y9Job> y9JobList = y9JobRepository.findByNameContainingOrderByTabIndex(name);
        return entityToModel(y9JobList);
    }

    @Override
    @Transactional
    public List<Job> order(List<String> jobIds) {
        List<Y9Job> jobList = new ArrayList<>();

        int tabIndex = 0;
        for (String jobId : jobIds) {
            jobList.add(y9JobManager.updateTabIndex(jobId, tabIndex++));
        }
        return entityToModel(jobList);
    }

    @Override
    @Transactional
    public Job saveOrUpdate(Job job) {
        Y9Job y9Job = PlatformModelConvertUtil.convert(job, Y9Job.class);

        // 检查名称是否可用
        Y9AssertUtil.isTrue(y9JobManager.isNameAvailable(y9Job.getName(), y9Job.getId()),
            OrgUnitErrorCodeEnum.JOB_EXISTS, y9Job.getName());

        if (StringUtils.isNotBlank(y9Job.getId())) {
            // 修改职位
            Optional<Y9Job> y9JobOptional = y9JobManager.findById(y9Job.getId());
            if (y9JobOptional.isPresent()) {
                Y9Job originalJob = PlatformModelConvertUtil.convert(y9JobOptional.get(), Y9Job.class);
                Y9Job savedJob = y9JobManager.update(y9Job);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.JOB_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.JOB_UPDATE.getDescription(), y9Job.getName()))
                    .objectId(savedJob.getId())
                    .oldObject(originalJob)
                    .currentObject(savedJob)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return entityToModel(savedJob);
            }
        }

        Y9Job savedJob = y9JobManager.insert(y9Job);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.JOB_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.JOB_CREATE.getDescription(), savedJob.getName()))
            .objectId(savedJob.getId())
            .oldObject(null)
            .currentObject(savedJob)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedJob);
    }

    private Job entityToModel(Y9Job y9Job) {
        return PlatformModelConvertUtil.convert(y9Job, Job.class);
    }

    private List<Job> entityToModel(List<Y9Job> y9JobList) {
        return PlatformModelConvertUtil.convert(y9JobList, Job.class);
    }

}
