package net.risesoft.service.org.impl;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9OrgBaseMoved;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.Y9OrgBaseMovedRepository;
import net.risesoft.service.org.Y9OrgBaseMovedService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OrgBaseMovedServiceImpl
    implements Y9OrgBaseMovedService, ApplicationListener<Y9EntityUpdatedEvent<? extends Y9OrgBase>> {

    private final Y9OrgBaseMovedRepository y9OrgBaseMovedRepository;

    @Override
    public void onApplicationEvent(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {

        Y9OrgBase originEntity = event.getOriginEntity();
        Y9OrgBase updatedEntity = event.getUpdatedEntity();

        if (Y9OrgUtil.isMoved(originEntity, updatedEntity)) {
            save(originEntity, updatedEntity);
        }

    }

    @Transactional(readOnly = false)
    public void save(Y9OrgBase originEntity, Y9OrgBase updatedEntity) {
        Y9OrgBaseMoved y9OrgBaseMoved = new Y9OrgBaseMoved();
        y9OrgBaseMoved.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9OrgBaseMoved.setFinished(false);
        y9OrgBaseMoved.setOperator(Y9LoginUserHolder.getUserInfo().getName());
        y9OrgBaseMoved.setOrgId(originEntity.getId());
        y9OrgBaseMoved.setOrgType(originEntity.getOrgType());
        y9OrgBaseMoved.setParentIdFrom(originEntity.getParentId());
        y9OrgBaseMoved.setParentIdTo(updatedEntity.getParentId());
        y9OrgBaseMovedRepository.save(y9OrgBaseMoved);
    }

}