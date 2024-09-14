package net.risesoft.service.org.impl;

import java.util.Optional;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9OrgBaseDeleted;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.Y9OrgBaseDeletedRepository;
import net.risesoft.service.org.Y9OrgBaseDeletedService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OrgBaseDeletedServiceImpl
    implements Y9OrgBaseDeletedService, ApplicationListener<Y9EntityDeletedEvent<? extends Y9OrgBase>> {

    private final Y9OrgBaseDeletedRepository y9OrgBaseDeletedRepository;

    @Override
    public Y9OrgBaseDeletedRepository getRepository() {
        return y9OrgBaseDeletedRepository;
    }

    @Override
    @Transactional(readOnly = false)
    public void onApplicationEvent(Y9EntityDeletedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        String json = Y9JsonUtil.writeValueAsString(y9OrgBase);
        Y9OrgBaseDeleted y9OrgBaseDeleted = new Y9OrgBaseDeleted();
        y9OrgBaseDeleted.setId(Y9IdGenerator.genId());
        y9OrgBaseDeleted.setOrgId(y9OrgBase.getId());
        y9OrgBaseDeleted.setParentId(y9OrgBase.getParentId());
        y9OrgBaseDeleted.setDn(y9OrgBase.getDn());
        y9OrgBaseDeleted.setOrgType(y9OrgBase.getOrgType());
        y9OrgBaseDeleted.setJsonContent(json);
        y9OrgBaseDeleted
            .setOperator(Optional.ofNullable(Y9LoginUserHolder.getUserInfo()).map(UserInfo::getName).orElse(null));
        y9OrgBaseDeletedRepository.save(y9OrgBaseDeleted);
    }
}