package net.risesoft.y9public.service.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;
import net.risesoft.y9public.service.resource.Y9MenuService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9MenuServiceImpl implements Y9MenuService {

    private final Y9MenuRepository y9MenuRepository;
    private final Y9TenantAppRepository y9TenantAppRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    private final Y9MenuManager y9MenuManager;

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
        Y9Menu y9Menu = this.getById(id);
        // 删除关联数据
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Menu));

        List<Y9TenantApp> y9TenantAppList =
            y9TenantAppRepository.findByAppIdAndTenancy(y9Menu.getAppId(), Boolean.TRUE);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());

            y9AuthorizationRepository.deleteByResourceId(y9Menu.getId());
            y9PersonToResourceAndAuthorityRepository.deleteByResourceId(y9Menu.getId());
            y9PositionToResourceAndAuthorityRepository.deleteByResourceId(y9Menu.getId());
        }

        y9MenuManager.delete(y9Menu);
    }

    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Menu> y9MenuList = this.findByParentId(parentId);
        for (Y9Menu y9Menu : y9MenuList) {
            this.delete(y9Menu.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Menu> disable(List<String> idList) {
        List<Y9Menu> y9MenuList = new ArrayList<>();
        for (String id : idList) {
            y9MenuList.add(this.disable(id));
        }
        return y9MenuList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu disable(String id) {
        Y9Menu y9Menu = this.getById(id);
        y9Menu.setEnabled(Boolean.FALSE);
        return this.saveOrUpdate(y9Menu);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Menu> enable(List<String> idList) {
        List<Y9Menu> y9MenuList = new ArrayList<>();
        for (String id : idList) {
            y9MenuList.add(this.enable(id));
        }
        return y9MenuList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu enable(String id) {
        Y9Menu y9Menu = this.getById(id);
        y9Menu.setEnabled(Boolean.TRUE);
        return this.saveOrUpdate(y9Menu);
    }

    @Override
    public boolean existsById(String id) {
        return y9MenuRepository.existsById(id);
    }

    @Override
    public Y9Menu findById(String id) {
        return y9MenuManager.findById(id);
    }

    @Override
    public List<Y9Menu> findByNameLike(String name) {
        return y9MenuRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public List<Y9Menu> findByParentId(String parentId) {
        return y9MenuRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @Override
    public Y9Menu getById(String id) {
        return y9MenuManager.getById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu move(String id, String parentId) {
        Y9Menu y9Menu = this.getById(id);
        y9Menu.setParentId(parentId);
        return this.saveOrUpdate(y9Menu);
    }

    @EventListener
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App entity = event.getEntity();
        deleteByParentId(entity.getId());
    }

    @EventListener
    public void onMenuDeleted(Y9EntityDeletedEvent<Y9Menu> event) {
        Y9Menu entity = event.getEntity();
        deleteByParentId(entity.getId());
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu saveOrUpdate(Y9Menu y9Menu) {
        if (StringUtils.isNotBlank(y9Menu.getId())) {
            Y9Menu originMenuResource = y9MenuManager.findById(y9Menu.getId());
            if (originMenuResource != null) {
                Y9Menu updatedMenuResource = new Y9Menu();
                Y9BeanUtil.copyProperties(originMenuResource, updatedMenuResource);
                Y9BeanUtil.copyProperties(y9Menu, updatedMenuResource);
                updatedMenuResource = y9MenuManager.save(updatedMenuResource);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originMenuResource, updatedMenuResource));

                return updatedMenuResource;
            }
        } else {
            y9Menu.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9Menu = y9MenuManager.save(y9Menu);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Menu));

        return y9Menu;
    }

    @Override
    public Y9Menu updateTabIndex(String id, int index) {
        return y9MenuManager.updateTabIndex(id, index);
    }

}
