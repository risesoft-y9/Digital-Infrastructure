package net.risesoft.manager.authorization.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.authorization.Y9AuthorizationManager;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;

/**
 * 授权 Manager 实现类
 *
 * @author shidaobang
 * @date 2025/08/12
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9AuthorizationManagerImpl implements Y9AuthorizationManager {

    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final CompositeResourceManager compositeResourceManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization insert(Y9Authorization y9Authorization) {
        if (StringUtils.isBlank(y9Authorization.getId())) {
            y9Authorization.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(y9Authorization.getResourceId());
        y9Authorization.setResourceName(y9ResourceBase.getName());
        y9Authorization.setResourceType(y9ResourceBase.getResourceType());
        y9Authorization.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Authorization
            .setAuthorizer(Optional.ofNullable(Y9LoginUserHolder.getUserInfo()).map(UserInfo::getName).orElse(null));
        Y9Authorization savedY9Authorization = y9AuthorizationRepository.save(y9Authorization);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedY9Authorization));

        return savedY9Authorization;
    }
}
