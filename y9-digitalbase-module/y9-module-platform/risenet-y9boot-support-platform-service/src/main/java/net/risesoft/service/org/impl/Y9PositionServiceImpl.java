package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.platform.org.Position;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9PositionServiceImpl implements Y9PositionService {

    private final Y9PositionRepository y9PositionRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;
    private final Y9JobManager y9JobManager;
    private final Y9PersonManager y9PersonManager;

    private final Y9SettingService y9SettingService;

    @Override
    @Transactional
    public Position changeDisabled(String id) {
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);

        Boolean disableStatus = currentPosition.changeDisabled();
        Y9Position savedPosition = y9PositionManager.update(currentPosition, originalPosition);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE_DISABLED.getDescription(),
                savedPosition.getName(), disableStatus ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalPosition)
            .currentObject(savedPosition)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
    }

    @Override
    @Transactional
    public Position create(String parentId, String jobId) {
        Position y9Position = new Position();
        y9Position.setParentId(parentId);
        y9Position.setJobId(jobId);
        return this.saveOrUpdate(y9Position);
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
        final Y9Position y9Position = y9PositionManager.getById(id);

        y9PersonsToPositionsManager.deleteByPositionId(id);
        y9PositionManager.delete(y9Position);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_DELETE.getDescription(), y9Position.getName()))
            .objectId(id)
            .oldObject(y9Position)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    @Transactional
    public void deleteByParentId(String parentId) {
        List<Position> positionList = listByParentId(parentId, null);
        for (Position position : positionList) {
            deleteById(position.getId());
        }
    }

    @Override
    public Optional<Position> findById(String id) {
        return y9PositionManager.findByIdFromCache(id).map(PlatformModelConvertUtil::y9PositionToPosition);
    }

    @Override
    public List<Position> findByJobId(String jobId) {
        List<Y9Position> y9PositionList = y9PositionRepository.findByJobId(jobId);
        return PlatformModelConvertUtil.y9PositionToPosition(y9PositionList);
    }

    @Override
    public List<String> findIdByGuidPathStartingWith(String guidPath) {
        return y9PositionRepository.findIdByGuidPathStartingWith(guidPath);
    }

    @Override
    public Position getById(String id) {
        return PlatformModelConvertUtil.y9PositionToPosition(y9PositionManager.getByIdFromCache(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasPosition(String positionName, String personId) {
        List<Position> list = listByPersonId(personId, Boolean.FALSE);
        for (Position p : list) {
            if (p.getName().equals(positionName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Position> listAll() {
        return PlatformModelConvertUtil.y9PositionToPosition(y9PositionRepository.findAll());
    }

    @Override
    public List<Position> listByParentId(String parentId, Boolean disabled) {
        List<Y9Position> y9PositionList;
        if (disabled == null) {
            y9PositionList = y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
        } else {
            y9PositionList = y9PositionRepository.findByParentIdAndDisabledOrderByTabIndexAsc(parentId, disabled);
        }
        return PlatformModelConvertUtil.y9PositionToPosition(y9PositionList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> listByPersonId(String personId, Boolean disabled) {
        return PlatformModelConvertUtil.y9PositionToPosition(y9PositionManager.listByPersonId(personId, disabled));
    }

    @Override
    @Transactional
    public Position move(String id, String parentId) {
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);

        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Integer nextSubTabIndex = compositeOrgBaseManager.getNextSubTabIndex(parentId);
        List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(parentId);
        currentPosition.changeParent(parent, nextSubTabIndex, ancestorList);
        Y9Position savedPosition = y9PositionManager.update(currentPosition, originalPosition);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE_PARENTID.getDescription(),
                currentPosition.getName(), parent.getName()))
            .objectId(id)
            .oldObject(originalPosition)
            .currentObject(currentPosition)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
    }

    @Override
    @Transactional
    public Position save(Position position) {
        Y9Position y9Position;
        if (StringUtils.isNotBlank(position.getId())) {
            y9Position = y9PositionManager.getById(position.getId());
            Y9BeanUtil.copyProperties(position, y9Position);
        } else {
            y9Position = PlatformModelConvertUtil.convert(position, Y9Position.class);
        }

        Y9Position savedPosition = y9PositionManager.save(y9Position);
        return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
    }

    @Override
    @Transactional
    public List<Position> saveOrder(List<String> positionIds) {
        List<Y9Position> orgPositionList = new ArrayList<>();

        int tabIndex = 0;
        for (String positionId : positionIds) {
            Y9Position currentPosition = y9PositionManager.getById(positionId);
            Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);

            List<Y9OrgBase> ancestorList =
                compositeOrgBaseManager.listOrgUnitAndAncestor(currentPosition.getParentId());
            currentPosition.changeTabIndex(tabIndex, ancestorList);
            orgPositionList.add(y9PositionManager.update(currentPosition, originalPosition));
        }

        return PlatformModelConvertUtil.y9PositionToPosition(orgPositionList);
    }

    @Override
    @Transactional
    public Position saveOrUpdate(Position position) {
        if (StringUtils.isNotEmpty(position.getId())) {
            Optional<Y9Position> positionOptional = y9PositionManager.findById(position.getId());
            if (positionOptional.isPresent()) {
                Y9Position originalPosition =
                    PlatformModelConvertUtil.convert(positionOptional.get(), Y9Position.class);
                Y9Position currentPosition = positionOptional.get();

                Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());
                List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(position.getParentId());
                String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
                Y9Job y9Job = y9JobManager.getById(position.getJobId());
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(position.getId(), null);
                currentPosition.update(position, y9Job, positionNameTemplate, parent, ancestorList, y9PersonList);
                Y9Position savedPosition = y9PositionManager.update(currentPosition, originalPosition);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.POSITION_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE.getDescription(), savedPosition.getName()))
                    .objectId(savedPosition.getId())
                    .oldObject(originalPosition)
                    .currentObject(savedPosition)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
            }
        }

        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());
        Integer nextSubTabIndex = compositeOrgBaseManager.getNextSubTabIndex(position.getParentId());
        List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(position.getParentId());
        Y9Job y9Job = y9JobManager.getById(position.getJobId());
        String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();

        Y9Position y9Position =
            new Y9Position(position, y9Job, positionNameTemplate, parent, ancestorList, nextSubTabIndex);
        Y9Position savedPosition = y9PositionManager.insert(y9Position);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_CREATE.getDescription(), savedPosition.getName()))
            .objectId(savedPosition.getId())
            .oldObject(null)
            .currentObject(savedPosition)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
    }

    @Override
    @Transactional
    public Position saveProperties(String id, String properties) {
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);

        currentPosition.changeProperties(properties);
        Y9Position savedPosition = y9PositionManager.update(currentPosition, originalPosition);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE_PROPERTIES.getDescription(),
                savedPosition.getName(), savedPosition.getProperties()))
            .objectId(id)
            .oldObject(originalPosition)
            .currentObject(savedPosition)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PositionToPosition(savedPosition);
    }

    @EventListener
    @Transactional
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下岗位也要删除
        deleteByParentId(parentDepartment.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除部门时其下岗位同步删除执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下岗位也要删除
        deleteByParentId(y9Organization.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织时其下岗位同步删除执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Position> positionList =
                y9PositionRepository.findByParentIdOrderByTabIndexAsc(updatedOrgBase.getId());
            for (Y9Position position : positionList) {
                Y9Position originalPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);

                List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(updatedOrgBase.getId());
                Y9Job y9Job = y9JobManager.getById(position.getJobId());
                String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(position.getId(), null);

                position.update(new Position(), y9Job, positionNameTemplate, updatedOrgBase, ancestorList,
                    y9PersonList);
                y9PositionManager.update(position, originalPosition);
            }
        } else if (Y9OrgUtil.isTabIndexChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Position> positionList = compositeOrgBaseManager.listAllDescendantPositions(updatedOrgBase.getId());
            for (Y9Position position : positionList) {
                Y9Position originalPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);

                List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(updatedOrgBase.getId());
                Y9Job y9Job = y9JobManager.getById(position.getJobId());
                String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(position.getId(), null);

                position.update(new Position(), y9Job, positionNameTemplate, updatedOrgBase, ancestorList,
                    y9PersonList);
                y9PositionManager.update(position, originalPosition);
            }
        }
    }

    @EventListener
    @Transactional
    public void onY9JobUpdated(Y9EntityUpdatedEvent<Y9Job> event) {
        Y9Job y9Job = event.getUpdatedEntity();

        List<Y9Position> positionList = y9PositionRepository.findByJobId(y9Job.getId());
        for (Y9Position position : positionList) {
            Y9Position originalPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);

            String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
            List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(position.getId(), null);
            Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());

            position.changeJob(y9Job, positionNameTemplate, parent, y9PersonList);
            y9PositionManager.update(position, originalPosition);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
    public void onY9PersonUpdated(Y9EntityUpdatedEvent<Y9Person> event) {
        Y9Person originY9Person = event.getOriginEntity();
        Y9Person updatedY9Person = event.getUpdatedEntity();

        if (Y9OrgUtil.isRenamed(originY9Person, updatedY9Person)) {
            List<Y9Position> y9PositionList = y9PositionManager.listByPersonId(updatedY9Person.getId(), null);
            for (Y9Position y9Position : y9PositionList) {
                Y9Position originalPosition = Y9ModelConvertUtil.convert(y9Position, Y9Position.class);

                String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
                Y9Job y9Job = y9JobManager.getById(y9Position.getJobId());
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(y9Position.getId(), null);
                Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Position.getParentId());

                y9Position.changeName(positionNameTemplate, y9Job, parent, y9PersonList);
                y9PositionManager.update(y9Position, originalPosition);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新人员触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Y9Position y9Position = y9PositionManager.getById(y9PersonsToPositions.getPositionId());
        Y9Position originalPosition = Y9ModelConvertUtil.convert(y9Position, Y9Position.class);

        String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
        Y9Job y9Job = y9JobManager.getById(y9Position.getJobId());
        List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(y9Position.getId(), null);
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Position.getParentId());

        y9Position.changeName(positionNameTemplate, y9Job, parent, y9PersonList);
        y9PositionManager.update(y9Position, originalPosition);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Y9Position y9Position = y9PositionManager.getById(y9PersonsToPositions.getPositionId());
        Y9Position originalPosition = Y9ModelConvertUtil.convert(y9Position, Y9Position.class);

        String positionNameTemplate = y9SettingService.getTenantSetting().getPositionNameTemplate();
        Y9Job y9Job = y9JobManager.getById(y9Position.getJobId());
        List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(y9Position.getId(), null);
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Position.getParentId());

        y9Position.changeName(positionNameTemplate, y9Job, parent, y9PersonList);
        y9PositionManager.update(y9Position, originalPosition);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新岗位名称执行完成");
        }
    }

}
