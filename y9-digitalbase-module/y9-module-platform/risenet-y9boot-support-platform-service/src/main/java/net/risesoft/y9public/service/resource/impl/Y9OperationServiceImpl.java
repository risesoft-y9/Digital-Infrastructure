package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.manager.resource.Y9OperationManager;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9OperationServiceImpl implements Y9OperationService {

    private final Y9OperationRepository y9OperationRepository;

    private final Y9OperationManager y9OperationManager;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(List<String> idList) {
        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        Y9Operation y9Operation = y9OperationManager.getById(id);
        y9OperationManager.delete(y9Operation);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.OPERATION_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.OPERATION_DELETE.getDescription(), y9Operation.getName()))
            .objectId(id)
            .oldObject(y9Operation)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Operation));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<Y9Operation> disable(List<String> idList) {
        List<Y9Operation> y9OperationList = new ArrayList<>();
        for (String id : idList) {
            y9OperationList.add(this.disable(id));
        }
        return y9OperationList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Operation disable(String id) {
        Y9Operation currentOperation = this.getById(id);
        Y9Operation originalOperation = Y9ModelConvertUtil.convert(currentOperation, Y9Operation.class);

        currentOperation.setEnabled(Boolean.FALSE);
        Y9Operation savedOperation = y9OperationManager.update(currentOperation);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.OPERATION_UPDATE_ENABLE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.OPERATION_UPDATE_ENABLE.getDescription(),
                savedOperation.getName(), "禁用"))
            .objectId(id)
            .oldObject(originalOperation)
            .currentObject(savedOperation)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOperation;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<Y9Operation> enable(List<String> idList) {
        List<Y9Operation> y9OperationList = new ArrayList<>();
        for (String id : idList) {
            y9OperationList.add(this.enable(id));
        }
        return y9OperationList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Operation enable(String id) {
        Y9Operation currentOperation = this.getById(id);
        Y9Operation originalOperation = Y9ModelConvertUtil.convert(currentOperation, Y9Operation.class);

        currentOperation.setEnabled(Boolean.TRUE);
        Y9Operation savedOperation = y9OperationManager.update(currentOperation);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.OPERATION_UPDATE_ENABLE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.OPERATION_UPDATE_ENABLE.getDescription(),
                savedOperation.getName(), "启用"))
            .objectId(id)
            .oldObject(originalOperation)
            .currentObject(savedOperation)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOperation;
    }

    @Override
    public boolean existsById(String id) {
        return y9OperationRepository.existsById(id);
    }

    @Override
    public Optional<Y9Operation> findById(String id) {
        return y9OperationManager.findByIdFromCache(id);
    }

    @Override
    public List<Y9Operation> findByNameLike(String name) {
        return y9OperationRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public Y9Operation getById(String id) {
        return y9OperationManager.getByIdFromCache(id);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Operation saveOrUpdate(Y9Operation y9Operation) {
        if (StringUtils.isNotBlank(y9Operation.getId())) {
            Optional<Y9Operation> y9OperationOptional = y9OperationManager.findById(y9Operation.getId());
            if (y9OperationOptional.isPresent()) {
                Y9Operation originOperation = Y9ModelConvertUtil.convert(y9OperationOptional.get(), Y9Operation.class);
                Y9Operation savedOperation = y9OperationManager.update(y9Operation);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.OPERATION_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.OPERATION_UPDATE.getDescription(), savedOperation.getName()))
                    .objectId(savedOperation.getId())
                    .oldObject(originOperation)
                    .currentObject(savedOperation)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedOperation;
            }
        }

        Y9Operation savedOperation = y9OperationManager.insert(y9Operation);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.OPERATION_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.OPERATION_CREATE.getDescription(), savedOperation.getName()))
            .objectId(savedOperation.getId())
            .oldObject(null)
            .currentObject(savedOperation)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOperation;
    }

    @Override
    public Y9Operation updateTabIndex(String id, int index) {
        return y9OperationManager.updateTabIndex(id, index);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByParentId(String parentId) {
        List<Y9Operation> y9OperationList = this.findByParentId(parentId);
        for (Y9Operation y9Operation : y9OperationList) {
            this.delete(y9Operation.getId());
        }
    }

    @Override
    public List<Y9Operation> findByParentId(String parentId) {
        return y9OperationRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onMenuDeleted(Y9EntityDeletedEvent<Y9Menu> event) {
        Y9Menu entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

}
