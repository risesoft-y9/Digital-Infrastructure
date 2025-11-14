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
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.platform.org.Job;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
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
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;

    @Override
    public String buildName(Job y9Job, List<Person> personList) {
        return y9PositionManager.buildName(y9Job.getName(), personList);
    }

    @Override
    @Transactional
    public Position changeDisabled(String id) {
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);
        boolean disableStatusToUpdate = !currentPosition.getDisabled();

        currentPosition.setDisabled(disableStatusToUpdate);
        Y9Position savedPosition = y9PositionManager.update(currentPosition);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE_DISABLED.getDescription(),
                savedPosition.getName(), disableStatusToUpdate ? "禁用" : "启用"))
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
        boolean exist = false;
        if (!list.isEmpty()) {
            for (Position p : list) {
                if (p.getName().equals(positionName)) {
                    exist = true;
                    break;
                }
            }
        }

        return exist;
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
        List<Y9PersonsToPositions> ppsList =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        List<Position> pList = new ArrayList<>();
        for (Y9PersonsToPositions pps : ppsList) {
            Position y9Position = this.getById(pps.getPositionId());
            if (disabled == null || disabled.equals(y9Position.getDisabled())) {
                pList.add(y9Position);
            }
        }
        return pList;
    }

    @Override
    @Transactional
    public Position move(String id, String parentId) {
        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = PlatformModelConvertUtil.convert(currentPosition, Y9Position.class);

        currentPosition.setParentId(parentId);
        currentPosition.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        Y9Position savedPosition = y9PositionManager.update(currentPosition);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE_PARENTID.getDescription(),
                currentPosition.getName(), parentToMove.getName()))
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
            orgPositionList.add(y9PositionManager.updateTabIndex(positionId, tabIndex++));
        }

        return PlatformModelConvertUtil.y9PositionToPosition(orgPositionList);
    }

    @Override
    @Transactional
    public Position saveOrUpdate(Position position) {
        Y9Position y9Position = PlatformModelConvertUtil.convert(position, Y9Position.class);

        if (StringUtils.isNotEmpty(y9Position.getId())) {
            Optional<Y9Position> positionOptional = y9PositionManager.findById(y9Position.getId());
            if (positionOptional.isPresent()) {
                Y9Position originalPosition =
                    PlatformModelConvertUtil.convert(positionOptional.get(), Y9Position.class);

                // 修改的岗位容量不能小于当前岗位人数
                checkHeadCountAvailability(y9Position);
                Y9Position savedPosition = y9PositionManager.update(y9Position);

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

        currentPosition.setProperties(properties);
        Y9Position savedPosition = y9PositionManager.update(currentPosition);

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
                y9PositionManager.update(position);
            }
        } else if (Y9OrgUtil.isTabIndexChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Position> positionList = compositeOrgBaseManager.listAllDescendantPositions(updatedOrgBase.getId());
            for (Y9Position position : positionList) {
                y9PositionManager.update(position);
            }
        }
    }

    private void checkHeadCountAvailability(Y9Position position) {
        Integer personCount = y9PersonsToPositionsRepository.countByPositionId(position.getId());
        Y9Assert.lessThanOrEqualTo(personCount, position.getCapacity(), OrgUnitErrorCodeEnum.POSITION_IS_FULL,
            position.getName());
    }
}
