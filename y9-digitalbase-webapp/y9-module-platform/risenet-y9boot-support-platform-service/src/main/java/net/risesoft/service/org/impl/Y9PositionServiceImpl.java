package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.platform.Position;
import net.risesoft.repository.Y9DepartmentPropRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.repository.relation.Y9PositionsToGroupsRepository;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
public class Y9PositionServiceImpl implements Y9PositionService {

    private final EntityManagerFactory entityManagerFactory;

    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9PositionsToGroupsRepository y9PositionsToGroupsRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;
    private final Y9PositionToRoleRepository y9PositionToRoleRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9DepartmentPropRepository y9DepartmentPropRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;

    public Y9PositionServiceImpl(@Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        Y9PositionRepository y9PositionRepository, Y9PersonsToPositionsRepository y9PersonsToPositionsRepository,
        Y9PositionsToGroupsRepository y9PositionsToGroupsRepository,
        Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository,
        Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository,
        Y9PositionToRoleRepository y9PositionToRoleRepository, Y9AuthorizationRepository y9AuthorizationRepository,
        Y9DepartmentPropRepository y9DepartmentPropRepository, CompositeOrgBaseManager compositeOrgBaseManager,
        Y9PositionManager y9PositionManager, Y9PersonsToPositionsManager y9PersonsToPositionsManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.y9PositionRepository = y9PositionRepository;
        this.y9PersonsToPositionsRepository = y9PersonsToPositionsRepository;
        this.y9PositionsToGroupsRepository = y9PositionsToGroupsRepository;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.y9PositionToResourceAndAuthorityRepository = y9PositionToResourceAndAuthorityRepository;
        this.y9PositionToRoleRepository = y9PositionToRoleRepository;
        this.y9AuthorizationRepository = y9AuthorizationRepository;
        this.y9DepartmentPropRepository = y9DepartmentPropRepository;
        this.compositeOrgBaseManager = compositeOrgBaseManager;
        this.y9PositionManager = y9PositionManager;
        this.y9PersonsToPositionsManager = y9PersonsToPositionsManager;
    }

    @Override
    public String buildName(Y9Job y9Job, List<Y9PersonsToPositions> personsToPositionsList) {
        return y9PositionManager.buildName(y9Job, personsToPositionsList);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position changeDisabled(String id) {
        Y9Position y9Position = y9PositionManager.findByIdNotCache(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.POSITION_NOT_FOUND, id));
        if (y9Position == null) {
            return null;
        }
        boolean isDisabled = !y9Position.getDisabled();
        y9Position.setDisabled(isDisabled);
        Y9Position savePosition = y9PositionManager.save(y9Position);

        if (isDisabled) {
            // y9PersonsToPositionsManager.deleteByPositionId(id);
            // y9PositionsToGroupsManager.deleteByPositionId(id);
            // y9DepartmentPropManager.deleteByOrgUnitId(id);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                String event = Boolean.TRUE.equals(isDisabled) ? "禁用" : "启用";
                Y9MessageOrg<Position> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savePosition, Position.class),
                        Y9OrgEventTypeConst.POSITION_UPDATE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, event + "岗位", event + savePosition.getName());
            }
        });
        return savePosition;
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
    public Y9Position create(Y9Position y9Position) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Position.getParentId());

        if (StringUtils.isBlank(y9Position.getId())) {
            y9Position.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9Position.setTabIndex(Integer.MAX_VALUE);
        y9Position.setParentId(parent.getId());
        y9Position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + y9Position.getName()
            + OrgLevelConsts.SEPARATOR + parent.getDn());
        y9Position.setDisabled(false);

        return save(y9Position);
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
    public void deleteById(String positionId) {
        final Y9Position y9Position = this.getById(positionId);

        // 删除岗位关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(positionId);
        y9PositionToRoleRepository.deleteByPositionId(positionId);
        y9PositionToResourceAndAuthorityRepository.deleteByPositionId(positionId);
        y9PersonsToPositionsManager.deleteByPositionId(positionId);
        y9DepartmentPropRepository.deleteByOrgBaseId(positionId);
        y9PositionsToGroupsRepository.deleteByPositionId(positionId);
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(positionId,
            AuthorizationPrincipalTypeEnum.POSITION);

        y9PositionManager.delete(y9Position);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Position));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Position> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(y9Position, Position.class),
                    Y9OrgEventTypeConst.POSITION_DELETE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除岗位", "删除" + y9Position.getName());
            }
        });
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
    @Transactional(readOnly = true)
    public Y9Position getById(String id) {
        return y9PositionManager.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
    public List<Y9Position> listByNameLike(String name, String dn) {
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
    public Y9Position move(String positionId, String parentId) {
        Y9Position originPosition = new Y9Position();
        Y9Position updatedPosition = y9PositionManager.findByIdNotCache(positionId)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.POSITION_NOT_FOUND, positionId));
        Y9BeanUtil.copyProperties(updatedPosition, originPosition);

        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        updatedPosition.setParentId(parent.getId());
        updatedPosition.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + originPosition.getId());
        updatedPosition.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + originPosition.getName()
            + OrgLevelConsts.SEPARATOR + parent.getDn());
        updatedPosition.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parentId));
        final Y9Position savedPosition = this.save(updatedPosition);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originPosition, savedPosition));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Position> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedPosition, Position.class),
                        Y9OrgEventTypeConst.POSITION_UPDATE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移动岗位",
                    originPosition.getName() + "移动到" + parent.getName());
            }
        });

        return savedPosition;
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

        if (Y9OrgUtil.isAncestorChanged(originOrgBase, updatedOrgBase)) {
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
        return y9PositionManager.saveOrUpdate(position);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position saveProperties(String positionId, String properties) {
        return y9PositionManager.saveProperties(positionId, properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Y9Position> search(String whereClause) {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        Assert.notNull(em, "EntityManager must not be null");
        Query query = null;
        if (whereClause == null || "".equals(whereClause.trim())) {
            query = em.createNativeQuery(" select * from Y9_ORG_POSITION ", Y9Position.class);

        } else {
            query = em.createNativeQuery(" select * from Y9_ORG_POSITION where " + whereClause, Y9Position.class);
        }
        return query.getResultList();
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

}
