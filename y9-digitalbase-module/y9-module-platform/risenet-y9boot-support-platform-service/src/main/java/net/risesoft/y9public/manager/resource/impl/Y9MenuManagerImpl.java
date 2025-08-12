package net.risesoft.y9public.manager.resource.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;

/**
 * 菜单 Manager 实现类
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.RESOURCE_MENU)
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9MenuManagerImpl implements Y9MenuManager {

    private final Y9MenuRepository y9MenuRepository;

    private final CompositeResourceManager compositeResourceManager;

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Menu> findById(String id) {
        return y9MenuRepository.findById(id);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Menu insert(Y9Menu y9Menu) {
        Y9ResourceBase parent = compositeResourceManager.getResourceAsParent(y9Menu.getParentId());

        if (StringUtils.isBlank(y9Menu.getId())) {
            y9Menu.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9Menu.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), y9Menu.getId()));
        }
        Integer tabIndex = getNextTabIndexByParentId(y9Menu.getParentId());
        y9Menu.setTabIndex(tabIndex);
        Y9Menu savedMenu = y9MenuRepository.save(y9Menu);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Menu));

        return savedMenu;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Menu.id", condition = "#y9Menu.id!=null")
    public Y9Menu update(Y9Menu y9Menu) {
        Y9ResourceBase parent = compositeResourceManager.getResourceAsParent(y9Menu.getParentId());

        Y9Menu currentMenu = this.getById(y9Menu.getId());
        Y9Menu originalMenu = new Y9Menu();
        Y9BeanUtil.copyProperties(currentMenu, originalMenu);
        Y9BeanUtil.copyProperties(y9Menu, currentMenu);

        currentMenu.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), currentMenu.getId()));
        Y9Menu savedMenu = y9MenuRepository.save(currentMenu);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalMenu, savedMenu));

        return savedMenu;
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Menu getById(String id) {
        return y9MenuRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.MENU_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Menu.id")
    public void delete(Y9Menu y9Menu) {
        y9MenuRepository.delete(y9Menu);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Menu updateTabIndex(String id, int index) {
        Y9Menu y9Menu = this.getById(id);
        y9Menu.setTabIndex(index);
        return this.update(y9Menu);
    }

    private Integer getNextTabIndexByParentId(String parentId) {
        return y9MenuRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9Menu::getTabIndex).orElse(0) + 1;
    }
}
