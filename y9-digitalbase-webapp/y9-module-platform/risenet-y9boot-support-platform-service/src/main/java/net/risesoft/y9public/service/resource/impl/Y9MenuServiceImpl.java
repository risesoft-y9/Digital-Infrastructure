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
        y9MenuManager.delete(y9Menu);

        // 删除租户关联数据
        List<Y9TenantApp> y9TenantAppList =
            y9TenantAppRepository.findByAppIdAndTenancy(y9Menu.getAppId(), Boolean.TRUE);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());
            this.deleteTenantRelatedByMenuId(id);
        }

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Menu));
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
    public Optional<Y9Menu> findById(String id) {
        return y9MenuManager.findById(id);
    }

    @Override
    public List<Y9Menu> findByNameLike(String name) {
        return y9MenuRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public Y9Menu getById(String id) {
        return y9MenuManager.getById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu saveOrUpdate(Y9Menu y9Menu) {
        if (StringUtils.isNotBlank(y9Menu.getId())) {
            Optional<Y9Menu> y9MenuOptional = y9MenuManager.findById(y9Menu.getId());
            if (y9MenuOptional.isPresent()) {
                Y9Menu originMenuResource = y9MenuOptional.get();
                Y9Menu updatedMenuResource = new Y9Menu();
                Y9BeanUtil.copyProperties(originMenuResource, updatedMenuResource);
                Y9BeanUtil.copyProperties(y9Menu, updatedMenuResource);
                updatedMenuResource = y9MenuManager.save(updatedMenuResource);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originMenuResource, updatedMenuResource));

                return updatedMenuResource;
            }
        } else {
            y9Menu.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));

            Integer maxTabIndex = getMaxIndexByParentId(y9Menu.getParentId());
            y9Menu.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        }
        y9Menu = y9MenuManager.save(y9Menu);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Menu));

        return y9Menu;
    }

    @Override
    public Y9Menu updateTabIndex(String id, int index) {
        return y9MenuManager.updateTabIndex(id, index);
    }

    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Menu> y9MenuList = this.findByParentId(parentId);
        for (Y9Menu y9Menu : y9MenuList) {
            this.delete(y9Menu.getId());
        }
    }

    /**
     * 删除相关租户数据 <br/>
     * 切换不同的数据源 需开启新事务
     *
     * @param menuId 菜单id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteTenantRelatedByMenuId(String menuId) {
        y9AuthorizationRepository.deleteByResourceId(menuId);
        y9PersonToResourceAndAuthorityRepository.deleteByResourceId(menuId);
        y9PositionToResourceAndAuthorityRepository.deleteByResourceId(menuId);
    }

    @Override
    public List<Y9Menu> findByParentId(String parentId) {
        return y9MenuRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @Override
    public Integer getMaxIndexByParentId(String parentId) {
        return y9MenuRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9Menu::getTabIndex).orElse(0);
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
        List<Y9Menu> y9MenuList = y9MenuRepository.findByAppId(entity.getAppId());
        for (Y9Menu y9Menu : y9MenuList) {
            this.deleteTenantRelatedByMenuId(y9Menu.getId());
        }
    }

}
