package net.risesoft.y9public.service.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.resource.Y9OperationManager;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class Y9OperationServiceImpl implements Y9OperationService {

    private final Y9OperationRepository y9OperationRepository;
    private final Y9TenantAppRepository y9TenantAppRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    private final Y9OperationManager y9OperationManager;
    private final CompositeResourceManager compositeResourceManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> idList) {
        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Operation y9Operation = this.getById(id);
        y9OperationManager.delete(y9Operation);

        // 删除租户与按钮资源关联的数据
        List<Y9TenantApp> y9TenantAppList =
            y9TenantAppRepository.findByAppIdAndTenancy(y9Operation.getAppId(), Boolean.TRUE);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());
            LOGGER.debug("删除租户[{}]与按钮资源关联的数据", y9TenantApp.getTenantId());
            this.deleteTenantRelatedByOperationId(id);
        }

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Operation));
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Operation> disable(List<String> idList) {
        List<Y9Operation> y9OperationList = new ArrayList<>();
        for (String id : idList) {
            y9OperationList.add(this.disable(id));
        }
        return y9OperationList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Operation disable(String id) {
        Y9Operation y9Operation = this.getById(id);
        y9Operation.setEnabled(Boolean.FALSE);
        return this.saveOrUpdate(y9Operation);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Operation> enable(List<String> idList) {
        List<Y9Operation> y9OperationList = new ArrayList<>();
        for (String id : idList) {
            y9OperationList.add(this.enable(id));
        }
        return y9OperationList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Operation enable(String id) {
        Y9Operation y9Operation = this.getById(id);
        y9Operation.setEnabled(Boolean.TRUE);
        return this.saveOrUpdate(y9Operation);
    }

    @Override
    public boolean existsById(String id) {
        return y9OperationRepository.existsById(id);
    }

    @Override
    public Optional<Y9Operation> findById(String id) {
        return y9OperationManager.findById(id);
    }

    @Override
    public List<Y9Operation> findByNameLike(String name) {
        return y9OperationRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public Y9Operation getById(String id) {
        return y9OperationManager.getById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Operation saveOrUpdate(Y9Operation y9Operation) {
        Y9ResourceBase parent = compositeResourceManager.getResourceAsParent(y9Operation.getParentId());

        if (StringUtils.isNotBlank(y9Operation.getId())) {
            Optional<Y9Operation> y9OperationOptional = y9OperationManager.findById(y9Operation.getId());
            if (y9OperationOptional.isPresent()) {
                Y9Operation originOperation = y9OperationOptional.get();
                Y9Operation updatedOperation = new Y9Operation();
                Y9BeanUtil.copyProperties(originOperation, updatedOperation);
                Y9BeanUtil.copyProperties(y9Operation, updatedOperation);

                updatedOperation.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), updatedOperation.getId()));

                updatedOperation = y9OperationManager.save(updatedOperation);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originOperation, updatedOperation));

                return updatedOperation;
            }
        } else {
            y9Operation.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9Operation.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), y9Operation.getId()));
            Integer maxTabIndex = getMaxIndexByParentId(y9Operation.getParentId());
            y9Operation.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        }

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Operation));

        return y9OperationManager.save(y9Operation);
    }

    @Override
    public Y9Operation updateTabIndex(String id, int index) {
        return y9OperationManager.updateTabIndex(id, index);
    }

    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Operation> y9OperationList = this.findByParentId(parentId);
        for (Y9Operation y9Operation : y9OperationList) {
            this.delete(y9Operation.getId());
        }
    }

    /**
     * 删除相关租户数据 <br>
     * 切换不同的数据源 需开启新事务
     *
     * @param operationId 应用id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteTenantRelatedByOperationId(String operationId) {
        y9AuthorizationRepository.deleteByResourceId(operationId);
        y9PersonToResourceAndAuthorityRepository.deleteByResourceId(operationId);
        y9PositionToResourceAndAuthorityRepository.deleteByResourceId(operationId);
    }

    @Override
    public List<Y9Operation> findByParentId(String parentId) {
        return y9OperationRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @Override
    public Integer getMaxIndexByParentId(String parentId) {
        return y9OperationRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9Operation::getTabIndex)
            .orElse(0);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onMenuDeleted(Y9EntityDeletedEvent<Y9Menu> event) {
        Y9Menu entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onTenantAppDeleted(Y9EntityDeletedEvent<Y9TenantApp> event) {
        Y9TenantApp entity = event.getEntity();
        Y9LoginUserHolder.setTenantId(entity.getTenantId());
        List<Y9Operation> y9OperationList = y9OperationRepository.findByAppId(entity.getAppId());
        for (Y9Operation y9Operation : y9OperationList) {
            this.deleteTenantRelatedByOperationId(y9Operation.getId());
        }
    }
}
