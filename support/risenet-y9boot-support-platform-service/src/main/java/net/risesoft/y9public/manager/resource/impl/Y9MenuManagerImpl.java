package net.risesoft.y9public.manager.resource.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.MenuErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
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
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9MenuManagerImpl implements Y9MenuManager {
    
    private final Y9MenuRepository y9MenuRepository;

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Menu findById(String id) {
        return y9MenuRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Menu.id", condition = "#y9Menu.id!=null")
    public Y9Menu save(Y9Menu y9Menu) {
        return y9MenuRepository.save(y9Menu);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Menu getById(String id) {
        return y9MenuRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(MenuErrorCodeEnum.MENU_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Menu.id")
    public void delete(Y9Menu y9Menu) {
        y9MenuRepository.delete(y9Menu);
    }
}
