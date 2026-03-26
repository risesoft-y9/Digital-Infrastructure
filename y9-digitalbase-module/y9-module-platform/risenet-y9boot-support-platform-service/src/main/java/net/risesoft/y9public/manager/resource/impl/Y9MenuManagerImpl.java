package net.risesoft.y9public.manager.resource.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.resource.Y9Menu;
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
@RequiredArgsConstructor
public class Y9MenuManagerImpl implements Y9MenuManager {

    private final Y9MenuRepository y9MenuRepository;

    @Override
    public Optional<Y9Menu> findById(String id) {
        return y9MenuRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Menu> findByIdFromCache(String id) {
        return y9MenuRepository.findById(id);
    }

    @Override
    public Y9Menu getById(String id) {
        return y9MenuRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.MENU_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Menu getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public Y9Menu insert(Y9Menu y9Menu) {
        Y9Menu savedMenu = y9MenuRepository.save(y9Menu);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedMenu));

        return savedMenu;
    }

    @Override
    @CacheEvict(key = "#y9Menu.id", condition = "#y9Menu.id!=null")
    public Y9Menu update(Y9Menu y9Menu, Y9Menu originalMenu) {
        Y9Menu savedMenu = y9MenuRepository.save(y9Menu);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalMenu, savedMenu));

        return savedMenu;
    }

    @Override
    @CacheEvict(key = "#y9Menu.id")
    public void delete(Y9Menu y9Menu) {
        y9MenuRepository.delete(y9Menu);
    }

    @Override
    @CacheEvict(key = "#id", condition = "#id!=null")
    public Y9Menu updateTabIndex(String id, int index) {
        Y9Menu currentMenu = this.getById(id);
        Y9Menu originalMenu = PlatformModelConvertUtil.convert(currentMenu, Y9Menu.class);

        currentMenu.changeTabIndex(index);
        return this.update(currentMenu, originalMenu);
    }
}
