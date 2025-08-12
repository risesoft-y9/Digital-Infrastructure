package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PositionServiceImpl implements Y9PositionService {

    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;

    @Override
    public String buildName(Y9Job y9Job, List<Y9PersonsToPositions> personsToPositionsList) {
        return y9PositionManager.buildName(y9Job, personsToPositionsList);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position changeDisabled(String id) {
        Y9Position currentPosition = y9PositionManager.getByIdNotCache(id);
        Y9Position originalPosition = Y9ModelConvertUtil.convert(currentPosition, Y9Position.class);
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

        return savedPosition;
    }

    @Override
    public Y9Position create(String parentId, String jobId) {
        Y9Position y9Position = new Y9Position();
        y9Position.setParentId(parentId);
        y9Position.setJobId(jobId);
        return this.saveOrUpdate(y9Position);
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
        final Y9Position y9Position = this.getById(id);

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
    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Position> positionList = listByParentId(parentId, null);
        for (Y9Position position : positionList) {
            deleteById(position.getId());
        }
    }

    @Override
    public boolean existsById(String id) {
        return y9PositionRepository.existsById(id);
    }

    @Override
    public Optional<Y9Position> findById(String id) {
        return y9PositionManager.findById(id);
    }

    @Override
    public List<Y9Position> findByJobId(String jobId) {
        return y9PositionRepository.findByJobId(jobId);
    }

    @Override
    public List<String> findIdByGuidPathStartingWith(String guidPath) {
        return y9PositionRepository.findIdByGuidPathStartingWith(guidPath);
    }

    @Override
    public Y9Position getById(String id) {
        return y9PositionManager.getById(id);
    }

    @Override
    public Boolean hasPosition(String positionName, String personId) {
        List<Y9Position> list = listByPersonId(personId, Boolean.FALSE);
        boolean exist = false;
        if (!list.isEmpty()) {
            for (Y9Position p : list) {
                if (p.getName().equals(positionName)) {
                    exist = true;
                    break;
                }
            }
        }

        return exist;
    }

    @Override
    public List<Y9Position> listAll() {
        return y9PositionRepository.findAll();
    }

    @Override
    public List<Y9Position> listByDn(String dn) {
        return y9PositionRepository.findByDn(dn);
    }

    @Override
    public List<Y9Position> listByNameLike(String name) {
        return y9PositionRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    @Override
    public List<Y9Position> listByNameLikeAndDn(String name, String dn) {
        return y9PositionRepository.findByNameContainingAndDnContainingOrDnContaining(name, OrgLevelConsts.UNIT + dn,
            OrgLevelConsts.ORGANIZATION + dn);
    }

    @Override
    public List<Y9Position> listByParentId(String parentId, Boolean disabled) {
        if (disabled == null) {
            return y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
        } else {
            return y9PositionRepository.findByParentIdAndDisabledOrderByTabIndexAsc(parentId, disabled);
        }
    }

    @Override
    public List<Y9Position> listByPersonId(String personId, Boolean disabled) {
        List<Y9PersonsToPositions> ppsList =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        List<Y9Position> pList = new ArrayList<>();
        for (Y9PersonsToPositions pps : ppsList) {
            Y9Position y9Position = this.getById(pps.getPositionId());
            if (disabled == null || disabled.equals(y9Position.getDisabled())) {
                pList.add(y9Position);
            }
        }
        return pList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position move(String id, String parentId) {
        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Y9Position currentPosition = y9PositionManager.getByIdNotCache(id);
        Y9Position originalPosition = Y9ModelConvertUtil.convert(currentPosition, Y9Position.class);

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

        return savedPosition;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position save(Y9Position position) {
        return y9PositionManager.save(position);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Position> saveOrder(List<String> positionIds) {
        List<Y9Position> orgPositionList = new ArrayList<>();

        int tabIndex = 0;
        for (String positionId : positionIds) {
            orgPositionList.add(y9PositionManager.updateTabIndex(positionId, tabIndex++));
        }

        return orgPositionList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position saveOrUpdate(Y9Position position) {
        if (StringUtils.isNotEmpty(position.getId())) {
            Optional<Y9Position> positionOptional = y9PositionManager.findByIdNotCache(position.getId());
            if (positionOptional.isPresent()) {
                Y9Position originalPosition = Y9ModelConvertUtil.convert(positionOptional.get(), Y9Position.class);

                // 修改的岗位容量不能小于当前岗位人数
                checkHeadCountAvailability(position);
                Y9Position savedPosition = y9PositionManager.update(position);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.POSITION_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.POSITION_UPDATE.getDescription(), savedPosition.getName()))
                    .objectId(savedPosition.getId())
                    .oldObject(originalPosition)
                    .currentObject(savedPosition)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedPosition;
            }
        }

        Y9Position savedPosition = y9PositionManager.insert(position);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_CREATE.getDescription(), savedPosition.getName()))
            .objectId(savedPosition.getId())
            .oldObject(null)
            .currentObject(savedPosition)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedPosition;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position saveProperties(String id, String properties) {
        Y9Position currentPosition = y9PositionManager.getById(id);
        Y9Position originalPosition = Y9ModelConvertUtil.convert(currentPosition, Y9Position.class);

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

        return savedPosition;
    }

    @Override
    public List<Y9Position> treeSearch(String name) {
        List<Y9Position> positionList = new ArrayList<>();
        List<Y9Position> positionListTemp = y9PositionRepository.findByNameContainingOrderByTabIndexAsc(name);
        positionList.addAll(positionListTemp);
        for (Y9Position p : positionListTemp) {
            recursionUpPosition(positionList, p.getParentId());
        }
        return positionList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position updateTabIndex(String id, int tabIndex) {
        return y9PositionManager.updateTabIndex(id, tabIndex);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下岗位也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下岗位也要删除
        deleteByParentId(y9Organization.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Position> positionList =
                y9PositionRepository.findByParentIdOrderByTabIndexAsc(updatedOrgBase.getId());
            for (Y9Position position : positionList) {
                this.saveOrUpdate(position);
            }
        }
    }

    private void recursionUpPosition(List<Y9Position> positionList, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return;
        }
        Y9Position position = this.getById(parentId);
        positionList.add(position);
        recursionUpPosition(positionList, position.getParentId());
    }

    private void checkHeadCountAvailability(Y9Position position) {
        Integer personCount = y9PersonsToPositionsRepository.countByPositionId(position.getId());
        Y9Assert.lessThanOrEqualTo(personCount, position.getCapacity(), OrgUnitErrorCodeEnum.POSITION_IS_FULL,
            position.getName());
    }
}
